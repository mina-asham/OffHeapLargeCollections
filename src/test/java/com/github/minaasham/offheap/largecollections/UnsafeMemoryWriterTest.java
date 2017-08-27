package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import org.junit.Test;

import java.util.function.BiConsumer;

public class UnsafeMemoryWriterTest {

    @Test
    public void writeByte() {
        simpleWrite(Byte.BYTES, (byte) 1, MemoryWriter::writeByte);
    }

    @Test
    public void writeShort() {
        simpleWrite(Short.BYTES, (short) 1, MemoryWriter::writeShort);
    }

    @Test
    public void writeChar() {
        simpleWrite(Character.BYTES, (char) 1, MemoryWriter::writeChar);
    }

    @Test
    public void writeInt() {
        simpleWrite(Integer.BYTES, 1, MemoryWriter::writeInt);
    }

    @Test
    public void writeLong() {
        simpleWrite(Long.BYTES, 1L, MemoryWriter::writeLong);
    }

    @Test
    public void writeFloat() {
        simpleWrite(Float.BYTES, (float) 1, MemoryWriter::writeFloat);
    }

    @Test
    public void writeDouble() {
        simpleWrite(Double.BYTES, 1.0, MemoryWriter::writeDouble);
    }

    @Test
    public void writeComplex() {
        int bytes = Long.BYTES + Integer.BYTES + Character.BYTES;
        long address = UnsafeUtils.allocate(bytes);

        UnsafeMemoryWriter memoryWriter = new UnsafeMemoryWriter().resetTo(address, bytes);
        memoryWriter.writeLong(1L);
        memoryWriter.writeInt(1);
        memoryWriter.writeChar('a');

        UnsafeUtils.free(address);
    }

    @Test(expected = IllegalStateException.class)
    public void writeFails() {
        simpleWrite(Byte.BYTES, 1, MemoryWriter::writeInt);
    }

    private static <T> void simpleWrite(long bytes, T value, BiConsumer<MemoryWriter, T> writerFunction) {
        long address = UnsafeUtils.allocate(bytes);
        writerFunction.accept(new UnsafeMemoryWriter().resetTo(address, bytes), value);
        UnsafeUtils.free(address);
    }
}