package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * IntSerializer, the implementation of ObjectSerializer for the integer type
 */
public final class IntSerializer implements ObjectSerializer<Integer> {

    /**
     * Serializes an integer using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The integer to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Integer object) {
        writer.writeInt(object);
    }

    /**
     * Deserializes an integer using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized integer
     */
    @Override
    public Integer deserialize(MemoryReader reader) {
        return reader.readInt();
    }

    /**
     * Gets the integer size in bytes
     *
     * @param object The integer to the get the size of
     * @return The size of the passed integer in bytes
     */
    @Override
    public long sizeInBytes(Integer object) {
        return Integer.BYTES;
    }
}
