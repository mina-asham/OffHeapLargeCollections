package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public class ShortSerializer implements ObjectSerializer<Short> {

    @Override
    public void serialize(MemoryWriter writer, Short object) {
        writer.writeShort(object);
    }

    @Override
    public Short deserialize(MemoryReader reader) {
        return reader.readShort();
    }

    @Override
    public long sizeInBytes(Short object) {
        return Short.BYTES;
    }
}
