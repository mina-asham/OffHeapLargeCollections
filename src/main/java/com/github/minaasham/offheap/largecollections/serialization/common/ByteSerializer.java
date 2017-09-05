package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public final class ByteSerializer implements ObjectSerializer<Byte> {

    @Override
    public void serialize(MemoryWriter writer, Byte object) {
        writer.writeByte(object);
    }

    @Override
    public Byte deserialize(MemoryReader reader) {
        return reader.readByte();
    }

    @Override
    public long sizeInBytes(Byte object) {
        return Byte.BYTES;
    }
}
