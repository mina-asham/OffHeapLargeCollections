package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

@RequiredArgsConstructor
public final class StringSerializer implements ObjectSerializer<String> {

    private final Charset encoding;

    public StringSerializer() {
        this(Charset.defaultCharset());
    }

    @Override
    public void serialize(MemoryWriter writer, String object) {
        for (byte b : object.getBytes(encoding)) {
            writer.writeByte(b);
        }
    }

    @Override
    public String deserialize(MemoryReader reader) {
        byte[] bytes = new byte[(int) reader.availableBytes()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = reader.readByte();
        }
        return new String(bytes, encoding);
    }

    @Override
    public long sizeInBytes(String object) {
        return object.getBytes(encoding).length;
    }
}
