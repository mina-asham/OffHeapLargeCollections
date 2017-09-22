package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * CharSerializer, the implementation of ObjectSerializer for the character type
 */
public final class CharSerializer extends FixedSizeObjectSerializer<Character> {

    /**
     * Singleton instance of the {@link CharSerializer} class
     */
    public static final CharSerializer INSTANCE = new CharSerializer();

    /**
     * CharSerializer constructor
     */
    private CharSerializer() {
        super(Character.BYTES);
    }

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
}
