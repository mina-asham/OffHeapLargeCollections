package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * DoubleSerializer, the implementation of ObjectSerializer for the double type
 */
public final class DoubleSerializer implements ObjectSerializer<Double> {

    /**
     * Serializes a double using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The double to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Double object) {
        writer.writeDouble(object);
    }

    /**
     * Deserializes a double using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized double
     */
    @Override
    public Double deserialize(MemoryReader reader) {
        return reader.readDouble();
    }

    /**
     * Gets the double size in bytes
     *
     * @param object The double to the get the size of
     * @return The size of the passed double in bytes
     */
    @Override
    public int sizeInBytes(Double object) {
        return Double.BYTES;
    }
}
