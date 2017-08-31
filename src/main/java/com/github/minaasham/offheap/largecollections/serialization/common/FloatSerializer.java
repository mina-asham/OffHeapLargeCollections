package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public class FloatSerializer implements ObjectSerializer<Float> {

    @Override
    public void serialize(MemoryWriter writer, Float object) {
        writer.writeFloat(object);
    }

    @Override
    public Float deserialize(MemoryReader reader) {
        return reader.readFloat();
    }

    @Override
    public long sizeInBytes(Float object) {
        return Float.BYTES;
    }
}
