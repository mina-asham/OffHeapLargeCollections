package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * BooleanSerializer, the implementation of ObjectSerializer for the boolean type
 */
public final class BooleanSerializer extends FixedSizeObjectSerializer<Boolean> {

    /**
     * BooleanSerializer constructor
     */
    public BooleanSerializer() {
        super(Byte.BYTES);
    }

    /**
     * Serializes a boolean using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The boolean to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Boolean object) {
        writer.writeByte(object ? (byte) 1 : 0);
    }

    /**
     * Deserializes a boolean using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized boolean
     */
    @Override
    public Boolean deserialize(MemoryReader reader) {
        return reader.readByte() == (byte) 1;
    }
}
