package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * IntSerializer, the implementation of ObjectSerializer for the integer type
 */
public final class IntSerializer extends FixedSizeObjectSerializer<Integer> {

    /**
     * Singleton instance of the {@link IntSerializer} class
     */
    public static final IntSerializer INSTANCE = new IntSerializer();

    /**
     * IntSerializer constructor
     */
    private IntSerializer() {
        super(Integer.BYTES);
    }

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
}
