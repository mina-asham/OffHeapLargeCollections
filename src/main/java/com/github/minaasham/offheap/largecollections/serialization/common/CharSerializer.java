package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public class CharSerializer implements ObjectSerializer<Character> {

    @Override
    public void serialize(MemoryWriter writer, Character object) {
        writer.writeChar(object);
    }

    @Override
    public Character deserialize(MemoryReader reader) {
        return reader.readChar();
    }

    @Override
    public long sizeInBytes(Character object) {
        return Character.BYTES;
    }
}
