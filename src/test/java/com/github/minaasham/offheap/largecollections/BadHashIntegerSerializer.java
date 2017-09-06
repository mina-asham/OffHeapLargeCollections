package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

final class BadHashIntegerSerializer extends FixedSizeObjectSerializer<BadHashInteger> {

    BadHashIntegerSerializer() {
        super(Integer.BYTES);
    }

    @Override
    public void serialize(MemoryWriter writer, BadHashInteger object) {
        writer.writeInt(object.getValue());
    }

    @Override
    public BadHashInteger deserialize(MemoryReader reader) {
        return new BadHashInteger(reader.readInt());
    }
}
