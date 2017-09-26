package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * LargeHashSet, an open address hash set that can handle a large number of entries
 * It utilizes the {@link sun.misc.Unsafe} object to allocate memory, hence it's not limited by the GC
 *
 * @param <E> The element type, cannot be null
 */
public final class LargeHashSet<E> extends AbstractMapBasedLargeSet<E> {

    /**
     * LargeHashSet constructor
     *
     * @param inner Inner map used for the set
     */
    private LargeHashSet(LargeMap<E, Object> inner) {
        super(inner);
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE));
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
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, loadFactor));
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
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, capacity));
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
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, double loadFactor, long capacity) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, loadFactor, capacity));
    }
}
