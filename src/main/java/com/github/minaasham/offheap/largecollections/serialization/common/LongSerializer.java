package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * LongSerializer, the implementation of ObjectSerializer for the long type
 */
public final class LongSerializer implements ObjectSerializer<Long> {

    /**
     * Serializes a long using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The long to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Long object) {
        writer.writeLong(object);
    }

    /**
     * Deserializes a long using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized long
     */
    @Override
    public Long deserialize(MemoryReader reader) {
        return reader.readLong();
    }

    /**
     * Gets the long size in bytes
     *
     * @param object The long to the get the size of
     * @return The size of the passed long in bytes
     */
    @Override
    public int sizeInBytes(Long object) {
        return Long.BYTES;
    }
}
