package com.github.minaasham.offheap.largecollections;

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

/**
 * LargeHashMap, an open address hash map that can handle a large number of entries
 * It utilizes the {@link sun.misc.Unsafe} object to allocate memory, hence it's not limited by the GC
 *
 * @param <K> The key type, cannot be null
 * @param <V> The value type, cannot be null
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LargeHashMap<K, V> implements LargeMap<K, V> {

    /**
     * Default load factor for the hash map
     */
    private static final double DEFAULT_LOAD_FACTOR = 0.65;

    /**
     * Default capacity for the hash map
     */
    private static final int DEFAULT_CAPACITY = 512;

    /**
     * The memory reader passed to key and value serializer, it's reset every time
     */
    private final UnsafeMemoryReader memoryReader;

    /**
     * The memory writer passed to key and value serializer, it's reset every time
     */
    private final UnsafeMemoryWriter memoryWriter;

    /**
     * The key object serializer
     */
    private final ObjectSerializer<K> keySerializer;

    /**
     * The value object serializer
     */
    private final ObjectSerializer<V> valueSerializer;

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
    public static <K, V> LargeHashMap<K, V> of(ObjectSerializer<K> keySerializer, ObjectSerializer<V> valueSerializer, int capacity) {
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
    public static <K, V> LargeHashMap<K, V> of(@NonNull ObjectSerializer<K> keySerializer, @NonNull ObjectSerializer<V> valueSerializer, double loadFactor, int capacity) {
        if (loadFactor <= 0 || 1 <= loadFactor) throw new IllegalArgumentException("Load factor must be bigger than 0 and less than 1");
        if (capacity <= 0) throw new IllegalArgumentException("Initial capacity must be at least 1");

        return new LargeHashMap<>(
                new UnsafeMemoryReader(),
                new UnsafeMemoryWriter(),
                keySerializer,
                valueSerializer,
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
        throwIfClosed();
        long offset = findOffset(key, capacity, entryPointerAddresses);
        long entryPointer = UnsafeUtils.getLong(entryPointerAddresses + offset);

        return entryPointer != 0 ? readValue(entryPointer) : null;
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

        long keySize = keySerializer.sizeInBytes(key);
        long valueSize = valueSerializer.sizeInBytes(value);

        entryPointer = UnsafeUtils.allocate(Long.BYTES + keySize + Long.BYTES + valueSize);
        UnsafeUtils.putLong(entryPointerAddresses + offset, entryPointer);

        UnsafeUtils.putLong(entryPointer, keySize);
        keySerializer.serialize(memoryWriter.resetTo(entryPointer + Long.BYTES, keySize), key);

        UnsafeUtils.putLong(entryPointer + Long.BYTES + keySize, valueSize);
        valueSerializer.serialize(memoryWriter.resetTo(entryPointer + Long.BYTES + keySize + Long.BYTES, valueSize), value);

        return previous;
    }

    /**
     * Removes the key from the map if it exists
     *
     * @param key The key to remove from the map
     * @return The value of the key
     */
    @Override
    public V remove(@NonNull K key) {
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
            } while (index <= bubbleUpIndex ? (index < entryIndex && entryIndex <= bubbleUpIndex) : (index < entryIndex || entryIndex <= bubbleUpIndex));

            UnsafeUtils.putLong(entryPointerAddresses + index * Long.BYTES, entryPointer);
            index = bubbleUpIndex;
        }
    }

    /**
     * Clear the map from all keys and values
     */
    @Override
    public void clear() {
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
    }

    /**
     * Gets the current size of the map
     *
     * @return The size of the map
     */
    @Override
    public long size() {
        throwIfClosed();
        return size;
    }

    /**
     * Returns an iterator over elements of type {@code Entry<K, V>}
     *
     * @return The map's iterator
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        throwIfClosed();
        return new LargeHashMapIterator<>(this, modifications);
    }

    /**
     * Disposes of the off heap allocations
     */
    @Override
    public void close() {
        throwIfClosed();
        clear();
        closed = true;
        UnsafeUtils.free(entryPointerAddresses);
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
        long keySize = UnsafeUtils.getLong(entryPointer);
        MemoryReader reader = memoryReader.resetTo(entryPointer + Long.BYTES, keySize);

        return keySerializer.deserialize(reader);
    }

    /**
     * Read a value from the map given the entry address pointer
     *
     * @param entryPointer The entry address pointer
     * @return The entry's value
     */
    private V readValue(long entryPointer) {
        long keySize = UnsafeUtils.getLong(entryPointer);
        long valuePointer = entryPointer + Long.BYTES + keySize;
        long valueSize = UnsafeUtils.getLong(valuePointer);
        MemoryReader reader = memoryReader.resetTo(valuePointer + Long.BYTES, valueSize);

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
            if (expectedModifications == map.modifications) {
                return read < map.size;
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
                long entryPointer = UnsafeUtils.getLong(map.entryPointerAddresses + offset);
                offset += Long.BYTES;

                while (entryPointer == 0) {
                    entryPointer = UnsafeUtils.getLong(map.entryPointerAddresses + offset);
                    offset += Long.BYTES;
                }

                read++;
                return map.readEntry(entryPointer);
            } else {
                throw new NoSuchElementException("Iterator exhausted, please use hasNext() to for available items first");
            }
        }
    }
}