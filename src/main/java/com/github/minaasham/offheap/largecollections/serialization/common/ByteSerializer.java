package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * ByteSerializer, the implementation of ObjectSerializer for the byte type
 */
public final class ByteSerializer implements ObjectSerializer<Byte> {

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

    /**
     * Gets the byte size in bytes
     *
     * @param object The byte to the get the size of
     * @return The size of the passed byte in bytes
     */
    @Override
    public int sizeInBytes(Byte object) {
        return Byte.BYTES;
    }
}
