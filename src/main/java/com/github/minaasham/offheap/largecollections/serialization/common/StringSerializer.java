package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

/**
 * StringSerializer, the implementation of ObjectSerializer for the string type
 */
@RequiredArgsConstructor
public final class StringSerializer implements ObjectSerializer<String> {

    /**
     * The character set encoding to be using in serialization and deserialization
     */
    private final Charset encoding;

    /**
     * Serializes a string using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The string to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, String object) {
        byte[] bytes = object.getBytes(encoding);
        writer.writeInt(bytes.length);

        for (byte b : bytes) {
            writer.writeByte(b);
        }
    }

    /**
     * Deserializes a string using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized string
     */
    @Override
    public String deserialize(MemoryReader reader) {
        byte[] bytes = new byte[reader.readInt()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = reader.readByte();
        }
        return new String(bytes, encoding);
    }

    /**
     * Gets the string size in bytes
     *
     * @param object The string to the get the size of
     * @return The size of the passed string in bytes
     */
    @Override
    public int sizeInBytes(String object) {
        return Integer.BYTES + object.getBytes(encoding).length;
    }
}
