package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public class BooleanSerializer implements ObjectSerializer<Boolean> {

    @Override
    public void serialize(MemoryWriter writer, Boolean object) {
        writer.writeByte(object ? (byte) 1 : 0);
    }

    @Override
    public Boolean deserialize(MemoryReader reader) {
        return reader.readByte() == (byte) 1;
    }

    @Override
    public long sizeInBytes(Boolean object) {
        return Byte.BYTES;
    }
}
