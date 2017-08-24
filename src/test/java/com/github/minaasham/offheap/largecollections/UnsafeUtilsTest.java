package com.github.minaasham.offheap.largecollections;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import sun.misc.Unsafe;

import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class)
public class UnsafeUtilsTest {

    @Test
    public void testAllocate() {
        long expectedAddress = 123;
        long expectedBytes = 456;
        new MockUp<Unsafe>() {
            @Mock
            long allocateMemory(long bytes) {
                assertEquals(expectedBytes, bytes);
                return expectedAddress;
            }

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
            @Mock
            void freeMemory(long address) {
                assertEquals(expectedAddress, address);
            }
        };

        UnsafeUtils.free(expectedAddress);
    }
}