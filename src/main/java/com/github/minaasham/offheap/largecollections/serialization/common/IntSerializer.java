package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public class IntSerializer implements ObjectSerializer<Integer> {

    @Override
    public void serialize(MemoryWriter writer, Integer object) {
        writer.writeInt(object);
    }

    @Override
    public Integer deserialize(MemoryReader reader) {
        return reader.readInt();
    }

    @Override
    public long sizeInBytes(Integer object) {
        return Integer.BYTES;
    }
}
