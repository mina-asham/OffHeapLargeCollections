package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * DoubleSerializer, the implementation of ObjectSerializer for the double type
 */
public final class DoubleSerializer extends FixedSizeObjectSerializer<Double> {

    /**
     * Singleton instance of the {@link DoubleSerializer} class
     */
    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    /**
     * DoubleSerializer constructor
     */
    private DoubleSerializer() {
        super(Double.BYTES);
    }

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
}
