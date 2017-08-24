package com.github.minaasham.offheap.largecollections;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * UnsafeUtils, a simple wrapper around the Unsafe object
 * It handles allocation and freeing of memory
 * This class is package private as it's an internal implementation detail
 */
@UtilityClass
final class UnsafeUtils {

    /**
     * The JVM's static Unsafe object
     */
    private static final Unsafe UNSAFE = getTheUnsafe();

    /**
     * Allocate a specific size of memory and set it all to zeroes
     *
     * @param bytes The size of the memory to allocate
     * @return The address pointing to the first byte in the allocated memory
     */
    static long allocate(long bytes) {
        long address = UNSAFE.allocateMemory(bytes);
        UNSAFE.setMemory(address, bytes, (byte) 0);
        return address;
    }

    /**
     * Free previously allocated memory
     *
     * @param address The address pointing to the first byte in the allocated memory
     */
    static void free(long address) {
        UNSAFE.freeMemory(address);
    }

    /**
     * Gets the JVM's static Unsafe object using reflection
     *
     * @return The JVM's static Unsafe object
     */
    @SneakyThrows
    private static Unsafe getTheUnsafe() {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}
