package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class UnsafeMemoryReaderTest {

    @Test
    public void readByte() {
        simpleRead(Byte.BYTES, MemoryReader::readByte, (byte) 0);
    }

    @Test
    public void readShort() {
        simpleRead(Short.BYTES, MemoryReader::readShort, (short) 0);
    }

    @Test
    public void readChar() {
        simpleRead(Character.BYTES, MemoryReader::readChar, (char) 0);
    }

    @Test
    public void readInt() {
        simpleRead(Integer.BYTES, MemoryReader::readInt, 0);
    }

    @Test
    public void readLong() {
        simpleRead(Long.BYTES, MemoryReader::readLong, 0L);
    }

    @Test
    public void readFloat() {
        simpleRead(Float.BYTES, MemoryReader::readFloat, (float) 0);
    }

    @Test
    public void readDouble() {
        simpleRead(Double.BYTES, MemoryReader::readDouble, 0.0);
    }

    @Test
    public void readComplex() {
        int bytes = Long.BYTES + Integer.BYTES + Character.BYTES;
        long address = UnsafeUtils.allocate(bytes);

        UnsafeMemoryReader memoryReader = new UnsafeMemoryReader().resetTo(address, bytes);
        assertEquals(0, memoryReader.readLong());
        assertEquals(0, memoryReader.readInt());
        assertEquals(0, memoryReader.readChar());

        UnsafeUtils.free(address);
    }

    @Test(expected = IllegalStateException.class)
    public void readFails() {
        simpleRead(Byte.BYTES, MemoryReader::readInt, 0);
    }

    private static <T> void simpleRead(long bytes, Function<MemoryReader, T> readerFunction, T expected) {
        long address = UnsafeUtils.allocate(bytes);
        assertEquals(expected, readerFunction.apply(new UnsafeMemoryReader().resetTo(address, bytes)));
        UnsafeUtils.free(address);
    }
}