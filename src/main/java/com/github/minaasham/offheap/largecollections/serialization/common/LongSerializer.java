package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;

public final class LongSerializer implements ObjectSerializer<Long> {

    @Override
    public void serialize(MemoryWriter writer, Long object) {
        writer.writeLong(object);
    }

    @Override
    public Long deserialize(MemoryReader reader) {
        return reader.readLong();
    }

    @Override
    public long sizeInBytes(Long object) {
        return Long.BYTES;
    }
}
