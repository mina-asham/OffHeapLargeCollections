package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.ThreadLocal.withInitial;

/**
 * LargeHashMap, an open address hash map that can handle a large number of entries
 * It utilizes the {@link sun.misc.Unsafe} object to allocate memory, hence it's not limited by the GC
 *
 * @param <K> The key type, cannot be null
 * @param <V> The value type, cannot be null
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LargeHashMap<K, V> implements LargeMap<K, V> {

    /**
     * Default load factor for the hash map
     */
    private static final double DEFAULT_LOAD_FACTOR = 0.65;

    /**
     * Default capacity for the hash map
     */
    private static final int DEFAULT_CAPACITY = 512;

    /**
     * The lock used to guarantee thread safety in map operations
     */
    private final ReentrantReadWriteLock lock;

    /**
     * The memory reader passed to key and value serializer, it's reset every time
     */
    private final ThreadLocal<UnsafeMemoryReader> memoryReader;

    /**
     * The memory writer passed to key and value serializer, it's reset every time
     */
    private final ThreadLocal<UnsafeMemoryWriter> memoryWriter;

    /**
     * The key object serializer
     */
    private final ObjectSerializer<K> keySerializer;

    /**
     * Does the key have a fixed size?
     */
    private final boolean keyFixedSize;

    /**
     * The key's header size; 0 for fixed and Integer.BYTES for variable
     */
    private final int keyHeaderSize;

    /**
     * The value object serializer
     */
    private final ObjectSerializer<V> valueSerializer;

    /**
     * Does the value have a fixed size?
     */
    private final boolean valueFixedSize;

    /**
     * The value's header size; 0 for fixed and Integer.BYTES for variable
     */
    private final int valueHeaderSize;

    /**
     * The load factor for the hash map
     */
    private final double loadFactor;

    /**
     * The address to the start of memory allocated for entry pointers
     */
    private long entryPointerAddresses;

    /**
     * The current capacity of the hash map
     */
    private long capacity;

    /**
     * The current size of the hash map
     */
    private long size;

    /**
     * The number of modifications that happened to the hash map,
     * this serves as an fail fast for the map's iterator
     */
    private int modifications;

    /**
     * If the map was closed and disposed of its resources
     */
    private boolean closed;

    /**
     * Factory method for creating a {@link LargeHashMap} object
     *
     * @param keySerializer   The key serializer
     * @param valueSerializer The value serializer
     * @param <K>             The key type
     * @param <V>             The value type
     * @return A {@link LargeHashMap} object
     */
    public static <K, V> LargeHashMap<K, V> of(ObjectSerializer<K> keySerializer, ObjectSerializer<V> valueSerializer) {
        return LargeHashMap.of(keySerializer, valueSerializer, DEFAULT_LOAD_FACTOR, DEFAULT_CAPACITY);
    }

    /**
     * Factory method for creating a {@link LargeHashMap} object
     *
     * @param keySerializer   The key serializer
     * @param valueSerializer The value serializer
     * @param loadFactor      The load factor
     * @param <K>             The key type
     * @param <V>             The value type
     * @return A {@link LargeHashMap} object
     */
    public static <K, V> LargeHashMap<K, V> of(ObjectSerializer<K> keySerializer, ObjectSerializer<V> valueSerializer, double loadFactor) {
        return LargeHashMap.of(keySerializer, valueSerializer, loadFactor, DEFAULT_CAPACITY);
    }

    /**
     * Factory method for creating a {@link LargeHashMap} object
     *
     * @param keySerializer   The key serializer
     * @param valueSerializer The value serializer
     * @param capacity        The initial capacity
     * @param <K>             The key type
     * @param <V>             The value type
     * @return A {@link LargeHashMap} object
     */
    public static <K, V> LargeHashMap<K, V> of(ObjectSerializer<K> keySerializer, ObjectSerializer<V> valueSerializer, long capacity) {
        return LargeHashMap.of(keySerializer, valueSerializer, DEFAULT_LOAD_FACTOR, capacity);
    }

    /**
     * Factory method for creating a {@link LargeHashMap} object
     *
     * @param keySerializer   The key serializer
     * @param valueSerializer The value serializer
     * @param loadFactor      The load factor, allowed values are more than 0 and less than or equal to 1
     * @param capacity        The initial capacity, must be a least 1
     * @param <K>             The key type
     * @param <V>             The value type
     * @return A {@link LargeHashMap} object
     */
    public static <K, V> LargeHashMap<K, V> of(@NonNull ObjectSerializer<K> keySerializer, @NonNull ObjectSerializer<V> valueSerializer, double loadFactor, long capacity) {
        if (loadFactor <= 0 || 1 <= loadFactor) throw new IllegalArgumentException("Load factor must be bigger than 0 and less than 1");
        if (capacity <= 0) throw new IllegalArgumentException("Initial capacity must be at least 1");

        boolean keyFixedSize = keySerializer instanceof FixedSizeObjectSerializer;
        boolean valueFixedSize = valueSerializer instanceof FixedSizeObjectSerializer;
        return new LargeHashMap<>(
                new ReentrantReadWriteLock(),
                withInitial(UnsafeMemoryReader::new),
                withInitial(UnsafeMemoryWriter::new),
                keySerializer,
                keyFixedSize,
                keyFixedSize ? 0 : Integer.BYTES,
                valueSerializer,
                valueFixedSize,
                valueFixedSize ? 0 : Integer.BYTES,
                loadFactor,
                UnsafeUtils.allocate(capacity * Long.BYTES),
                capacity,
                0,
                0,
                false
        );
    }

    /**
     * Gets key's value from the map
     *
     * @param key The key to lookup
     * @return The value associated with the key
     */
    @Override
    public V get(@NonNull K key) {
        lock.readLock().lock();
        try {
            throwIfClosed();
            long offset = findOffset(key, capacity, entryPointerAddresses);
            long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset);

            return entryPointer != 0 ? readValue(entryPointer) : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Puts the key and value in the map
     *
     * @param key   The key to insert in the map
     * @param value The value to insert in the map
     * @return The old value related to that key
     */
    @Override
    public V put(@NonNull K key, @NonNull V value) {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            resizeIfRequired();
            modifications++;

            long offset = findOffset(key, capacity, entryPointerAddresses);
            long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset);
            V previous = null;

            if (entryPointer != 0) {
                previous = readValue(entryPointer);
                UnsafeUtils.free(entryPointer);
            } else {
                size++;
            }

            int keySize = keySerializer.sizeInBytes(key);
            int valueSize = valueSerializer.sizeInBytes(value);

            entryPointer = UnsafeUtils.allocate(keyHeaderSize + keySize + valueHeaderSize + valueSize);
            UnsafeUtils.putLong(entryPointerAddresses + offset, entryPointer);

            if (!keyFixedSize) UnsafeUtils.putInt(entryPointer, keySize);
            keySerializer.serialize(memoryWriter.get().resetTo(entryPointer + keyHeaderSize, keySize), key);

            if (!valueFixedSize) UnsafeUtils.putInt(entryPointer + keyHeaderSize + keySize, valueSize);
            valueSerializer.serialize(memoryWriter.get().resetTo(entryPointer + keyHeaderSize + keySize + valueHeaderSize, valueSize), value);

            return previous;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes the key from the map if it exists
     *
     * @param key The key to remove from the map
     * @return The value of the key
     */
    @Override
    public V remove(@NonNull K key) {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            resizeIfRequired();
            long offset = findOffset(key, capacity, entryPointerAddresses);
            long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset);

            if (entryPointer == 0) return null;

            modifications++;
            size--;

            V value = readValue(entryPointer);
            UnsafeUtils.free(entryPointer);

            long index = offset / Long.BYTES;
            long bubbleUpIndex = index;
            while (true) {
                long entryIndex;
                do {
                    bubbleUpIndex = (bubbleUpIndex + 1) % capacity;
                    entryPointer = UnsafeUtils.getLong(entryPointerAddresses + bubbleUpIndex * Long.BYTES);
                    if (entryPointer == 0) {
                        UnsafeUtils.putLong(entryPointerAddresses + index * Long.BYTES, 0);
                        return value;
                    }
                    entryIndex = offset(readKey(entryPointer), capacity);
                } while (index <= bubbleUpIndex ? index < entryIndex && entryIndex <= bubbleUpIndex : index < entryIndex || entryIndex <= bubbleUpIndex);

                UnsafeUtils.putLong(entryPointerAddresses + index * Long.BYTES, entryPointer);
                index = bubbleUpIndex;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear the map from all keys and values
     */
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            throwIfClosed();
            for (int i = 0; i < capacity; i++) {
                long entryPointerAddress = entryPointerAddresses + i * Long.BYTES;
                long entryPointer = UnsafeUtils.getLong(entryPointerAddress);
                if (entryPointer != 0) {
                    modifications++;
                    UnsafeUtils.free(entryPointer);
                    UnsafeUtils.putLong(entryPointerAddress, 0);
                }
            }
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets the current size of the map
     *
     * @return The size of the map
     */
    @Override
    public long size() {
        lock.readLock().lock();
        try {
            throwIfClosed();
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an iterator over elements of type {@code Entry<K, V>}
     *
     * @return The map's iterator
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        lock.readLock().lock();
        try {
            throwIfClosed();
            return new LargeHashMapIterator<>(this, modifications);
        } finally {
            lock.readLock().unlock();
        }
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
            UnsafeUtils.free(entryPointerAddresses);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the hash code value for this {@link LargeMap}, i.e.,
     * the sum of, for each key-value pair in the map,
     * {@code key.hashCode() ^ value.hashCode()}.
     *
     * @return the hash code value for this map
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Entry<K, V> entry : this) {
            hashCode += entry.getKey().hashCode() ^ entry.getValue().hashCode();
        }
        return hashCode;
    }

    /**
     * Compares the specified object with this map for equality.
     * Returns {@code true} if the given object is a map with the same
     * mappings as this map.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LargeHashMap<K, V> that = (LargeHashMap<K, V>) o;

        if (size != that.size) return false;
        if (closed != that.closed) return false;

        for (Entry<K, V> entry : this) {
            V thisValue = entry.getValue();
            V thatValue = that.get(entry.getKey());
            if (thatValue == null || !thatValue.equals(thisValue)) return false;
        }

        return true;
    }

    /**
     * Returns a string representation of this map.  The string
     * representation consists of a list of key-value mappings (in no
     * particular order) enclosed in braces ("{@code {}}").  Adjacent
     * mappings are separated by the characters {@code ",\n"} (comma
     * and new line).  Each key-value mapping is rendered as {@code "  "}
     * (two white spaces; for indentation) the key
     * followed by an equals sign ("{@code =}") followed by the
     * associated value.
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(System.lineSeparator());
        for (Iterator<Entry<K, V>> iterator = iterator(); iterator.hasNext(); ) {
            Entry<K, V> entry = iterator.next();
            sb.append("  ").append(entry);
            if (iterator.hasNext()) {
                sb.append(",");
            }
            sb.append(System.lineSeparator());
        }
        return sb.append('}').toString();
    }

    /**
     * Checks if the map is already closed and throws an exception if so
     *
     * @throws IllegalStateException if the map was closed
     */
    private void throwIfClosed() {
        if (closed) throw new IllegalStateException("Map was already closed");
    }

    /**
     * Find the offset of a key in the map's underlying array
     *
     * @param key                   The key for which to find the offset
     * @param capacity              The current map capacity
     * @param entryPointerAddresses The address to the start of memory allocated for entry pointers
     * @return The offset of such key in the map
     */
    private long findOffset(K key, long capacity, long entryPointerAddresses) {
        long offset = offset(key, capacity);
        long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset * Long.BYTES);

        while (entryPointer != 0 && !key.equals(readKey(entryPointer))) {
            offset = ++offset % capacity;
            entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset * Long.BYTES);
        }

        return offset * Long.BYTES;
    }

    /**
     * Resize the map underlying array if required
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
            long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + i * Long.BYTES);
            if (entryPointer != 0) {
                K key = readKey(entryPointer);
                long offset = findOffset(key, newCapacity, newEntryPointerAddresses);
                UnsafeUtils.putLong(newEntryPointerAddresses + offset, entryPointer);
            }
        }

        UnsafeUtils.free(entryPointerAddresses);

        capacity = newCapacity;
        entryPointerAddresses = newEntryPointerAddresses;
    }

    /**
     * Read a key from the map given the entry address pointer
     *
     * @param entryPointer The entry address pointer
     * @return The entry's key
     */
    private K readKey(long entryPointer) {
        int keySize = keyFixedSize ? keySerializer.sizeInBytes(null) : UnsafeUtils.getInt(entryPointer);
        MemoryReader reader = memoryReader.get().resetTo(entryPointer + keyHeaderSize, keySize);

        return keySerializer.deserialize(reader);
    }

    /**
     * Read a value from the map given the entry address pointer
     *
     * @param entryPointer The entry address pointer
     * @return The entry's value
     */
    private V readValue(long entryPointer) {
        int keySize = keyFixedSize ? keySerializer.sizeInBytes(null) : UnsafeUtils.getInt(entryPointer);
        long valuePointer = entryPointer + keyHeaderSize + keySize;
        int valueSize = valueFixedSize ? valueSerializer.sizeInBytes(null) : UnsafeUtils.getInt(valuePointer);
        MemoryReader reader = memoryReader.get().resetTo(valuePointer + valueHeaderSize, valueSize);

        return valueSerializer.deserialize(reader);
    }

    /**
     * Read an entry from the map given its address pointer
     *
     * @param entryPointer The entry address pointer
     * @return The key and value pair
     */
    private Entry<K, V> readEntry(long entryPointer) {
        return new SimpleEntry<>(readKey(entryPointer), readValue(entryPointer));
    }

    /**
     * Helper function to hash, spread, and mod by capacity
     *
     * @param key      The key to hash
     * @param capacity The current capacity
     * @return The desired offset
     */
    private long offset(K key, long capacity) {
        return spread(key.hashCode()) % capacity;
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
     * LargeHashMapIterator, an inner class wrapping the iterator logic for the map
     *
     * @param <K> The key type
     * @param <V> The value type
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class LargeHashMapIterator<K, V> implements Iterator<Entry<K, V>> {
        /**
         * A reference to the map this iterator is iterating on
         */
        @NonNull
        private final LargeHashMap<K, V> map;

        /**
         * The number of modifications at the time we initialized this iterator,
         * this is to help fail fast if the map was changed midway
         */
        private final long expectedModifications;

        /**
         * The number of read items
         */
        private long read = 0;

        /**
         * The current offset in the entry pointers
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
            map.lock.readLock().lock();
            try {
                if (expectedModifications == map.modifications) {
                    return read < map.size;
                }
            } finally {
                map.lock.readLock().unlock();
            }

            throw new ConcurrentModificationException("Map has been modified since iterator was created");
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Entry<K, V> next() {
            if (hasNext()) {
                map.lock.readLock().lock();
                try {
                    long entryPointer = UnsafeUtils.getLong(map.entryPointerAddresses + offset);
                    offset += Long.BYTES;

                    while (entryPointer == 0) {
                        entryPointer = UnsafeUtils.getLong(map.entryPointerAddresses + offset);
                        offset += Long.BYTES;
                    }

                    read++;
                    return map.readEntry(entryPointer);
                } finally {
                    map.lock.readLock().unlock();
                }
            } else {
                throw new NoSuchElementException("Iterator exhausted, please use hasNext() to for available items first");
            }
        }
    }
}
