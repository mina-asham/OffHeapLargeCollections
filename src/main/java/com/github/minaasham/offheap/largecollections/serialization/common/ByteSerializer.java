package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * ByteSerializer, the implementation of ObjectSerializer for the byte type
 */
public final class ByteSerializer extends FixedSizeObjectSerializer<Byte> {

    /**
     * Singleton instance of the {@link ByteSerializer} class
     */
    public static final ByteSerializer INSTANCE = new ByteSerializer();

    /**
     * ByteSerializer constructor
     */
    private ByteSerializer() {
        super(Byte.BYTES);
    }

    /**
     * Serializes a byte using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The byte to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Byte object) {
        writer.writeByte(object);
    }

    /**
     * Deserializes a byte using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized byte
     */
    @Override
    public Byte deserialize(MemoryReader reader) {
        return reader.readByte();
    }
}
