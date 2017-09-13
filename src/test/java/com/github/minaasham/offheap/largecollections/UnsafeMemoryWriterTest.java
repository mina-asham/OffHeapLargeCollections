package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UnsafeMemoryWriterTest {

    @Test
    void writeByte() {
        simpleWrite(Byte.BYTES, (byte) 1, MemoryWriter::writeByte);
    }

    @Test
    void writeShort() {
        simpleWrite(Short.BYTES, (short) 1, MemoryWriter::writeShort);
    }

    @Test
    void writeChar() {
        simpleWrite(Character.BYTES, (char) 1, MemoryWriter::writeChar);
    }

    @Test
    void writeInt() {
        simpleWrite(Integer.BYTES, 1, MemoryWriter::writeInt);
    }

    @Test
    void writeLong() {
        simpleWrite(Long.BYTES, 1L, MemoryWriter::writeLong);
    }

    @Test
    void writeFloat() {
        simpleWrite(Float.BYTES, (float) 1, MemoryWriter::writeFloat);
    }

    @Test
    void writeDouble() {
        simpleWrite(Double.BYTES, 1.0, MemoryWriter::writeDouble);
    }

    @Test
    void writeComplex() {
        int bytes = Long.BYTES + Integer.BYTES + Character.BYTES;
        long address = UnsafeUtils.allocate(bytes);

        UnsafeMemoryWriter memoryWriter = new UnsafeMemoryWriter().resetTo(address, bytes);
        memoryWriter.writeLong(1L);
        memoryWriter.writeInt(1);
        memoryWriter.writeChar('a');

        UnsafeUtils.free(address);
    }

    @Test
    void writeFails() {
        assertThrows(IllegalStateException.class, () -> simpleWrite(Byte.BYTES, 1, MemoryWriter::writeInt));
    }

    private static <T> void simpleWrite(long bytes, T value, BiConsumer<MemoryWriter, T> writerFunction) {
        long address = UnsafeUtils.allocate(bytes);
        writerFunction.accept(new UnsafeMemoryWriter().resetTo(address, bytes), value);
        UnsafeUtils.free(address);
    }
}