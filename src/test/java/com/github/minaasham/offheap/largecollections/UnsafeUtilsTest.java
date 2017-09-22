package com.github.minaasham.offheap.largecollections;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnsafeUtilsTest {

    private static final Random RANDOM = new Random();
    private static final double DELTA = 1e-10;

    @Test
    void testAllocate() {
        long expectedAddress = 123;
        long expectedBytes = 456;

        UnsafeMockUp unsafeMockUp = new UnsafeMockUp(expectedAddress, expectedBytes);

        assertEquals(expectedAddress, UnsafeUtils.allocate(expectedBytes));
        assertTrue(unsafeMockUp.allocateMemoryCalled);
        assertTrue(unsafeMockUp.setMemoryCalled);
        assertFalse(unsafeMockUp.freeMemoryCalled);
    }

    @Test
    void testFree() {
        long expectedAddress = 123;

        UnsafeMockUp unsafeMockUp = new UnsafeMockUp(expectedAddress, -1);

        UnsafeUtils.free(expectedAddress);
        assertFalse(unsafeMockUp.allocateMemoryCalled);
        assertFalse(unsafeMockUp.setMemoryCalled);
        assertTrue(unsafeMockUp.freeMemoryCalled);
    }

    @Test
    void testByte() {
        long address = UnsafeUtils.allocate(Byte.BYTES);
        byte value = (byte) RANDOM.nextInt();
        UnsafeUtils.putByte(address, value);
        assertEquals(value, UnsafeUtils.getByte(address));
        UnsafeUtils.free(address);
    }

    @Test
    void testShort() {
        long address = UnsafeUtils.allocate(Short.BYTES);
        short value = (short) RANDOM.nextInt();
        UnsafeUtils.putShort(address, value);
        assertEquals(value, UnsafeUtils.getShort(address));
        UnsafeUtils.free(address);
    }

    @Test
    void testChar() {
        long address = UnsafeUtils.allocate(Character.BYTES);
        char value = (char) RANDOM.nextInt();
        UnsafeUtils.putChar(address, value);
        assertEquals(value, UnsafeUtils.getChar(address));
        UnsafeUtils.free(address);
    }

    @Test
    void testInt() {
        long address = UnsafeUtils.allocate(Integer.BYTES);
        int value = RANDOM.nextInt();
        UnsafeUtils.putInt(address, value);
        assertEquals(value, UnsafeUtils.getInt(address));
        UnsafeUtils.free(address);
    }

    @Test
    void testLong() {
        long address = UnsafeUtils.allocate(Long.BYTES);
        long value = RANDOM.nextLong();
        UnsafeUtils.putLong(address, value);
        assertEquals(value, UnsafeUtils.getLong(address));
        UnsafeUtils.free(address);
    }

    @Test
    void testFloat() {
        long address = UnsafeUtils.allocate(Float.BYTES);
        float value = RANDOM.nextFloat();
        UnsafeUtils.putFloat(address, value);
        assertEquals(value, UnsafeUtils.getFloat(address), DELTA);
        UnsafeUtils.free(address);
    }

    @Test
    void testDouble() {
        long address = UnsafeUtils.allocate(Double.BYTES);
        double value = RANDOM.nextDouble();
        UnsafeUtils.putDouble(address, value);
        assertEquals(value, UnsafeUtils.getDouble(address), DELTA);
        UnsafeUtils.free(address);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class UnsafeMockUp extends MockUp<Unsafe> {

        private final long expectedAddress;
        private final long expectedBytes;

        private boolean allocateMemoryCalled;
        private boolean setMemoryCalled;
        private boolean freeMemoryCalled;

        @Mock
        long allocateMemory(long bytes) {
            allocateMemoryCalled = true;
            assertEquals(expectedBytes, bytes);
            return expectedAddress;
        }

        @Mock
        void setMemory(long address, long bytes, byte value) {
            setMemoryCalled = true;
            assertEquals(expectedAddress, address);
            assertEquals(expectedBytes, bytes);
            assertEquals(0, value);
        }

        @Mock
        void freeMemory(long address) {
            freeMemoryCalled = true;
            assertEquals(expectedAddress, address);
        }
    }
}