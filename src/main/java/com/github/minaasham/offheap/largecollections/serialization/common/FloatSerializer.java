package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * FloatSerializer, the implementation of ObjectSerializer for the float type
 */
public final class FloatSerializer implements ObjectSerializer<Float> {

    /**
     * Serializes a float using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The float to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Float object) {
        writer.writeFloat(object);
    }

    /**
     * Deserializes a float using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized float
     */
    @Override
    public Float deserialize(MemoryReader reader) {
        return reader.readFloat();
    }

    /**
     * Gets the float size in bytes
     *
     * @param object The float to the get the size of
     * @return The size of the passed float in bytes
     */
    @Override
    public int sizeInBytes(Float object) {
        return Float.BYTES;
    }
}
