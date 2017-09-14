package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.ThreadLocal.withInitial;

/**
 * LargeHashSet, an open address hash set that can handle a large number of entries
 * It utilizes the {@link sun.misc.Unsafe} object to allocate memory, hence it's not limited by the GC
 *
 * @param <E> The element type, cannot be null
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LargeHashSet<E> implements LargeSet<E> {

    /**
     * Default load factor for the hash set
     */
    private static final double DEFAULT_LOAD_FACTOR = 0.65;

    /**
     * Default capacity for the hash set
     */
    private static final int DEFAULT_CAPACITY = 512;

    /**
     * The lock used to guarantee thread safety in set operations
     */
    private final ReentrantReadWriteLock lock;

    /**
     * The memory reader passed to element serializer, it's reset every time
     */
    private final ThreadLocal<UnsafeMemoryReader> memoryReader;

    /**
     * The memory writer passed to element serializer, it's reset every time
     */
    private final ThreadLocal<UnsafeMemoryWriter> memoryWriter;

    /**
     * The element object serializer
     */
    private final ObjectSerializer<E> elementSerializer;

    /**
     * Does the element have a fixed size?
     */
    private final boolean fixedSize;

    /**
     * The header size; 0 for fixed and Integer.BYTES for variable
     */
    private final int headerSize;

    /**
     * The load factor for the hash set
     */
    private final double loadFactor;

    /**
     * The address to the start of memory allocated for entry pointers
     */
    private long elementPointerAddresses;

    /**
     * The current capacity of the hash set
     */
    private long capacity;

    /**
     * The current size of the hash set
     */
    private long size;

    /**
     * The number of modifications that happened to the hash set,
     * this serves as an fail fast for the set's iterator
     */
    private int modifications;

    /**
     * If the set was closed and disposed of its resources
     */
    private boolean closed;


    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer) {
        return LargeHashSet.of(elementSerializer, DEFAULT_LOAD_FACTOR, DEFAULT_CAPACITY);
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param loadFactor        The load factor
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, double loadFactor) {
        return LargeHashSet.of(elementSerializer, loadFactor, DEFAULT_CAPACITY);
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param capacity          The initial capacity
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, long capacity) {
        return LargeHashSet.of(elementSerializer, DEFAULT_LOAD_FACTOR, capacity);
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param loadFactor        The load factor, allowed values are more than 0 and less than or equal to 1
     * @param capacity          The initial capacity, must be a least 1
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(@NonNull ObjectSerializer<E> elementSerializer, double loadFactor, long capacity) {
        if (loadFactor <= 0 || 1 <= loadFactor) throw new IllegalArgumentException("Load factor must be bigger than 0 and less than 1");
        if (capacity <= 0) throw new IllegalArgumentException("Initial capacity must be at least 1");

        boolean fixedSize = elementSerializer instanceof FixedSizeObjectSerializer;
        return new LargeHashSet<>(
                new ReentrantReadWriteLock(),
                withInitial(UnsafeMemoryReader::new),
                withInitial(UnsafeMemoryWriter::new),
                elementSerializer,
                fixedSize,
                fixedSize ? 0 : Integer.BYTES,
                loadFactor,
                UnsafeUtils.allocate(capacity * Long.BYTES),
                capacity,
                0,
                0,
                false
        );
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element
     *
     * @param element Element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    @Override
    public boolean contains(@NonNull E element) {
        lock.readLock().lock();
        try {
            throwIfClosed();
            long offset = findOffset(element, capacity, elementPointerAddresses);
            long entryPointer = UnsafeUtils.getLong(elementPointerAddresses + offset);

            return entryPointer != 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Adds the specified element to this set if it is not already present
     *
     * @param element Element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    @Override
    public boolean add(@NonNull E element) {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            resizeIfRequired();
            modifications++;

            long offset = findOffset(element, capacity, elementPointerAddresses);
            long elementPointer = UnsafeUtils.getLong(elementPointerAddresses + offset);
            if (elementPointer != 0) return false;

            size++;

            int elementSize = elementSerializer.sizeInBytes(element);

            elementPointer = UnsafeUtils.allocate(headerSize + elementSize);
            UnsafeUtils.putLong(elementPointerAddresses + offset, elementPointer);

            if (!fixedSize) UnsafeUtils.putInt(elementPointer, elementSize);
            elementSerializer.serialize(memoryWriter.get().resetTo(elementPointer + headerSize, elementSize), element);

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes the specified element from this set if it is present
     *
     * @param element Element to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     */
    @Override
    public boolean remove(@NonNull E element) {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            resizeIfRequired();
            long offset = findOffset(element, capacity, elementPointerAddresses);
            long elementPointer = UnsafeUtils.getLong(elementPointerAddresses + offset);

            if (elementPointer == 0) return false;

            modifications++;
            size--;

            UnsafeUtils.free(elementPointer);

            long index = offset / Long.BYTES;
            long bubbleUpIndex = index;
            while (true) {
                long elementIndex;
                do {
                    bubbleUpIndex = (bubbleUpIndex + 1) % capacity;
                    elementPointer = UnsafeUtils.getLong(elementPointerAddresses + bubbleUpIndex * Long.BYTES);
                    if (elementPointer == 0) {
                        UnsafeUtils.putLong(elementPointerAddresses + index * Long.BYTES, 0);
                        return true;
                    }
                    elementIndex = offset(read(elementPointer), capacity);
                } while (index <= bubbleUpIndex ? index < elementIndex && elementIndex <= bubbleUpIndex : index < elementIndex || elementIndex <= bubbleUpIndex);

                UnsafeUtils.putLong(elementPointerAddresses + index * Long.BYTES, elementPointer);
                index = bubbleUpIndex;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear the set from all elements
     */
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            for (int i = 0; i < capacity; i++) {
                long elementPointerAddress = elementPointerAddresses + i * Long.BYTES;
                long elementPointer = UnsafeUtils.getLong(elementPointerAddress);
                if (elementPointer != 0) {
                    modifications++;
                    UnsafeUtils.free(elementPointer);
                    UnsafeUtils.putLong(elementPointerAddress, 0);
                }
            }
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets the current size of the set
     *
     * @return The size of the set
     */
    @Override
    public long size() {
        throwIfClosed();
        return size;
    }

    /**
     * Returns an iterator over elements of type {@code E}
     *
     * @return The set's iterator
     */
    @Override
    public Iterator<E> iterator() {
        throwIfClosed();
        return new LargeHashSetIterator<>(this, modifications);
    }

    /**
     * Disposes of the off heap allocations
     */
    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            clear();
            closed = true;
            UnsafeUtils.free(elementPointerAddresses);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the hash code value for this set.  The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set.
     * This implementation iterates over the set, calling the
     * <tt>hashCode</tt> method on each element in the set, and adding up
     * the results.
     *
     * @return the hash code value for this set
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E element : this) {
            hashCode += element.hashCode();
        }
        return hashCode;
    }

    /**
     * Compares the specified object with this set for equality.
     * Returns {@code true} if the given object is a set with the same
     * mappings as this set.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LargeHashSet<E> that = (LargeHashSet<E>) o;

        if (size != that.size) return false;
        if (closed != that.closed) return false;

        for (E element : this) {
            if (!that.contains(element)) return false;
        }

        return true;
    }

    /**
     * Returns a string representation of this set.  The string
     * representation consists of a list of the set's elements in no
     * specific order, enclosed in braces (<tt>"{}"</tt>) and each starting
     * with <tt>"  "</tt> (two spaces for indentation).
     * Adjacent elements are separated by the characters <tt>",\n"</tt>
     * (comma and new line). Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this set
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(System.lineSeparator());
        for (Iterator<E> iterator = iterator(); iterator.hasNext(); ) {
            E element = iterator.next();
            sb.append("  ").append(element);
            if (iterator.hasNext()) {
                sb.append(",");
            }
            sb.append(System.lineSeparator());
        }
        return sb.append('}').toString();
    }

    /**
     * Checks if the set is already closed and throws an exception if so
     *
     * @throws IllegalStateException if the set was closed
     */
    private void throwIfClosed() {
        if (closed) throw new IllegalStateException("Set was already closed");
    }

    /**
     * Find the offset of an element in the set's underlying array
     *
     * @param element                 The element for which to find the offset
     * @param capacity                The current map capacity
     * @param elementPointerAddresses The address to the start of memory allocated for element pointers
     * @return The offset of such element in the map
     */
    private long findOffset(E element, long capacity, long elementPointerAddresses) {
        long offset = offset(element, capacity);
        long elementPointer = UnsafeUtils.getLong(elementPointerAddresses + offset * Long.BYTES);

        while (elementPointer != 0 && !element.equals(read(elementPointer))) {
            offset = ++offset % capacity;
            elementPointer = UnsafeUtils.getLong(elementPointerAddresses + offset * Long.BYTES);
        }

        return offset * Long.BYTES;
    }

    /**
     * Resize the set underlying array if required
     */
    private void resizeIfRequired() {
        double load = size / (double) capacity;
        long newCapacity;
        if (load > loadFactor) {
            newCapacity = capacity * 2;
        } else if (load < loadFactor / 2 && capacity / 2 > DEFAULT_CAPACITY) {
            newCapacity = capacity / 2;
        } else {
            return;
        }

        modifications++;

        long newEntryPointerAddresses = UnsafeUtils.allocate(newCapacity * Long.BYTES);

        for (int i = 0; i < capacity; i++) {
            long entryPointer = UnsafeUtils.getLong(elementPointerAddresses + i * Long.BYTES);
            if (entryPointer != 0) {
                E element = read(entryPointer);
                long offset = findOffset(element, newCapacity, newEntryPointerAddresses);
                UnsafeUtils.putLong(newEntryPointerAddresses + offset, entryPointer);
            }
        }

        UnsafeUtils.free(elementPointerAddresses);

        capacity = newCapacity;
        elementPointerAddresses = newEntryPointerAddresses;
    }

    /**
     * Read an element from the map given the entry address pointer
     *
     * @param elementPointer The entry address pointer
     * @return The element
     */
    private E read(long elementPointer) {
        int elementSize = fixedSize ? elementSerializer.sizeInBytes(null) : UnsafeUtils.getInt(elementPointer);
        MemoryReader reader = memoryReader.get().resetTo(elementPointer + headerSize, elementSize);

        return elementSerializer.deserialize(reader);
    }

    /**
     * Helper function to hash, spread, and mod by capacity
     *
     * @param element  The element to hash
     * @param capacity The current capacity
     * @return The desired offset
     */
    private long offset(E element, long capacity) {
        return spread(element.hashCode()) % capacity;
    }

    /**
     * Copied from {@link java.util.concurrent.ConcurrentHashMap}:
     * Spreads (XORs) higher bits of hash to lower and also forces top
     * bit to 0. Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     *
     * @param hashCode The original hash code for the object
     * @return The new spread and positive hash code
     */
    private static int spread(int hashCode) {
        return (hashCode ^ (hashCode >>> 16)) & Integer.MAX_VALUE;
    }

    /**
     * LargeHashSetIterator, an inner class wrapping the iterator logic for the set
     *
     * @param <E> The element type
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class LargeHashSetIterator<E> implements Iterator<E> {
        /**
         * A reference to the set this iterator is iterating on
         */
        @NonNull
        private final LargeHashSet<E> set;

        /**
         * The number of modifications at the time we initialized this iterator,
         * this is to help fail fast if the set was changed midway
         */
        private final long expectedModifications;

        /**
         * The number of read items
         */
        private long read = 0;

        /**
         * The current offset in the element pointers
         */
        private long offset = 0;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            if (expectedModifications == set.modifications) {
                return read < set.size;
            }

            throw new ConcurrentModificationException("Set has been modified since iterator was created");
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next() {
            if (hasNext()) {
                long elementPointer = UnsafeUtils.getLong(set.elementPointerAddresses + offset);
                offset += Long.BYTES;

                while (elementPointer == 0) {
                    elementPointer = UnsafeUtils.getLong(set.elementPointerAddresses + offset);
                    offset += Long.BYTES;
                }

                read++;
                return set.read(elementPointer);
            } else {
                throw new NoSuchElementException("Iterator exhausted, please use hasNext() to for available items first");
            }
        }
    }
}
