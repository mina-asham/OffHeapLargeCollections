package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * LongSerializer, the implementation of ObjectSerializer for the long type
 */
public final class LongSerializer extends FixedSizeObjectSerializer<Long> {

    /**
     * Singleton instance of the {@link LongSerializer} class
     */
    public static final LongSerializer INSTANCE = new LongSerializer();

    /**
     * LongSerializer constructor
     */
    private LongSerializer() {
        super(Long.BYTES);
    }

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
}
