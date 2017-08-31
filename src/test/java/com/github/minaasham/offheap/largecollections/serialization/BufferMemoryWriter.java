package com.github.minaasham.offheap.largecollections.serialization;

import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class BufferMemoryWriter implements MemoryWriter {

    private final ByteBuffer buffer;

    @Override
    public void writeByte(byte value) {
        buffer.put(value);
    }

    @Override
    public void writeShort(short value) {
        buffer.putShort(value);
    }

    @Override
    public void writeChar(char value) {
        buffer.putChar(value);
    }

    @Override
    public void writeInt(int value) {
        buffer.putInt(value);
    }

    @Override
    public void writeLong(long value) {
        buffer.putLong(value);
    }

    @Override
    public void writeFloat(float value) {
        buffer.putFloat(value);
    }

    @Override
    public void writeDouble(double value) {
        buffer.putDouble(value);
    }
}
