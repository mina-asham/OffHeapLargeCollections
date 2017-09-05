package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * ShortSerializer, the implementation of ObjectSerializer for the short type
 */
public final class ShortSerializer implements ObjectSerializer<Short> {

    /**
     * Serializes a short using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The short to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Short object) {
        writer.writeShort(object);
    }

    /**
     * Deserializes a short using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized short
     */
    @Override
    public Short deserialize(MemoryReader reader) {
        return reader.readShort();
    }

    /**
     * Gets the short size in bytes
     *
     * @param object The short to the get the size of
     * @return The size of the passed short in bytes
     */
    @Override
    public long sizeInBytes(Short object) {
        return Short.BYTES;
    }
}
