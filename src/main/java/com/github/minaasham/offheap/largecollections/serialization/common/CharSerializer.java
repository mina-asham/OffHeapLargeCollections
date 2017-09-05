package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

/**
 * CharSerializer, the implementation of ObjectSerializer for the character type
 */
public final class CharSerializer implements ObjectSerializer<Character> {

    /**
     * Serializes a character using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The character to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Character object) {
        writer.writeChar(object);
    }

    /**
     * Deserializes a character using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized character
     */
    @Override
    public Character deserialize(MemoryReader reader) {
        return reader.readChar();
    }

    /**
     * Gets the character size in bytes
     *
     * @param object The character to the get the size of
     * @return The size of the passed character in bytes
     */
    @Override
    public long sizeInBytes(Character object) {
        return Character.BYTES;
    }
}
