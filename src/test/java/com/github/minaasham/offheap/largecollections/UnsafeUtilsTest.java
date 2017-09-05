package com.github.minaasham.offheap.largecollections;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import sun.misc.Unsafe;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class)
public class UnsafeUtilsTest {

    private static final Random RANDOM = new Random();
    private static final double DELTA = 1e-10;

    @Test
    public void testAllocate() {
        long expectedAddress = 123;
        long expectedBytes = 456;
        new MockUp<Unsafe>() {

            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @Mock
            long allocateMemory(long bytes) {
                assertEquals(expectedBytes, bytes);
                return expectedAddress;
            }

            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @Mock
            void setMemory(long address, long bytes, byte value) {
                assertEquals(expectedAddress, address);
                assertEquals(expectedBytes, bytes);
                assertEquals(0, value);
            }
        };

        assertEquals(expectedAddress, UnsafeUtils.allocate(expectedBytes));
    }


    @Test
    public void testFree() {
        long expectedAddress = 123;
        new MockUp<Unsafe>() {

            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @Mock
            void freeMemory(long address) {
                assertEquals(expectedAddress, address);
            }
        };

        UnsafeUtils.free(expectedAddress);
    }

    @Test
    public void testByte() {
        long address = UnsafeUtils.allocate(Byte.BYTES);
        byte value = (byte) RANDOM.nextInt();
        UnsafeUtils.putByte(address, value);
        assertEquals(value, UnsafeUtils.getByte(address));
        UnsafeUtils.free(address);
    }

    @Test
    public void testShort() {
        long address = UnsafeUtils.allocate(Short.BYTES);
        short value = (short) RANDOM.nextInt();
        UnsafeUtils.putShort(address, value);
        assertEquals(value, UnsafeUtils.getShort(address));
        UnsafeUtils.free(address);
    }

    @Test
    public void testChar() {
        long address = UnsafeUtils.allocate(Character.BYTES);
        char value = (char) RANDOM.nextInt();
        UnsafeUtils.putChar(address, value);
        assertEquals(value, UnsafeUtils.getChar(address));
        UnsafeUtils.free(address);
    }

    @Test
    public void testInt() {
        long address = UnsafeUtils.allocate(Integer.BYTES);
        int value = RANDOM.nextInt();
        UnsafeUtils.putInt(address, value);
        assertEquals(value, UnsafeUtils.getInt(address));
        UnsafeUtils.free(address);
    }

    @Test
    public void testLong() {
        long address = UnsafeUtils.allocate(Long.BYTES);
        long value = RANDOM.nextLong();
        UnsafeUtils.putLong(address, value);
        assertEquals(value, UnsafeUtils.getLong(address));
        UnsafeUtils.free(address);
    }

    @Test
    public void testFloat() {
        long address = UnsafeUtils.allocate(Float.BYTES);
        float value = RANDOM.nextFloat();
        UnsafeUtils.putFloat(address, value);
        assertEquals(value, UnsafeUtils.getFloat(address), DELTA);
        UnsafeUtils.free(address);
    }

    @Test
    public void testDouble() {
        long address = UnsafeUtils.allocate(Double.BYTES);
        double value = RANDOM.nextDouble();
        UnsafeUtils.putDouble(address, value);
        assertEquals(value, UnsafeUtils.getDouble(address), DELTA);
        UnsafeUtils.free(address);
    }
}