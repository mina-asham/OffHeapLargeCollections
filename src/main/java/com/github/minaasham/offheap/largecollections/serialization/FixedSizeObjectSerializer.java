package com.github.minaasham.offheap.largecollections.serialization;

import lombok.RequiredArgsConstructor;

/**
 * FixedSizeObjectSerializer, an abstract base supporting
 * serialization and deserialization for fixed sized objects
 *
 * @param <T> The type of object to serialize from or deserialize to
 */
@RequiredArgsConstructor
public abstract class FixedSizeObjectSerializer<T> implements ObjectSerializer<T> {

    /**
     * A fixed size in bytes for the object
     */
    private final int sizeInBytes;

    /**
     * Gets the object size in bytes
     *
     * @param object The object to the get the size of
     * @return The size of the passed object in bytes
     */
    @Override
    public final int sizeInBytes(T object) {
        return sizeInBytes;
    }
}
