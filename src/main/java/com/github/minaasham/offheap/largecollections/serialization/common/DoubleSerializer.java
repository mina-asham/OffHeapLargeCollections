package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public final class DoubleSerializer implements ObjectSerializer<Double> {

    @Override
    public void serialize(MemoryWriter writer, Double object) {
        writer.writeDouble(object);
    }

    @Override
    public Double deserialize(MemoryReader reader) {
        return reader.readDouble();
    }

    @Override
    public long sizeInBytes(Double object) {
        return Double.BYTES;
    }
}
