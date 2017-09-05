package com.github.minaasham.offheap.largecollections.serialization;

/**
 * ObjectSerializer, the main interface for binary serialization/deserialization
 * Users will have to implement this interface for both keys and values
 *
 * @param <T> The type of object to serialize from or deserialize to
 */
public interface ObjectSerializer<T> {

    /**
     * Serializes an object using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The object to serialize
     */
    void serialize(MemoryWriter writer, T object);

    /**
     * Deserializes an object using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized object
     */
    T deserialize(MemoryReader reader);

    /**
     * Gets the object size in bytes
     *
     * @param object The object to the get the size of
     * @return The size of the passed object in bytes
     */
    int sizeInBytes(T object);
}
