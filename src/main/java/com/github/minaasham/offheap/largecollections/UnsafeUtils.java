package com.github.minaasham.offheap.largecollections;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * UnsafeUtils, a simple wrapper around the Unsafe object
 * It handles allocation and freeing of memory, as well as reading and writing native types
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
     * Reads a byte from the specific memory address
     *
     * @param address The address to read the byte from
     * @return The byte read
     */
    static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    /**
     * Writes a byte to the specific memory address
     *
     * @param address The address to write the byte at
     * @param value   The byte value
     */
    static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    /**
     * Reads a short from the specific memory address
     *
     * @param address The address to read the short from
     * @return The short read
     */
    static short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    /**
     * Writes a short to the specific memory address
     *
     * @param address The address to write the short at
     * @param value   The short value
     */
    static void putShort(long address, short value) {
        UNSAFE.putShort(address, value);
    }

    /**
     * Reads a character from the specific memory address
     *
     * @param address The address to read the character from
     * @return The character read
     */
    static char getChar(long address) {
        return UNSAFE.getChar(address);
    }

    /**
     * Writes a character to the specific memory address
     *
     * @param address The address to write the character at
     * @param value   The character value
     */
    static void putChar(long address, char value) {
        UNSAFE.putChar(address, value);
    }

    /**
     * Reads an integer from the specific memory address
     *
     * @param address The address to read the integer from
     * @return The integer read
     */
    static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    /**
     * Writes a integer to the specific memory address
     *
     * @param address The address to write the integer at
     * @param value   The integer value
     */
    static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    /**
     * Reads a long from the specific memory address
     *
     * @param address The address to read the long from
     * @return The long read
     */
    static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    /**
     * Writes a long to the specific memory address
     *
     * @param address The address to write the long at
     * @param value   The long value
     */
    static void putLong(long address, long value) {
        UNSAFE.putLong(address, value);
    }

    /**
     * Reads a float from the specific memory address
     *
     * @param address The address to read the float from
     * @return The float read
     */
    static float getFloat(long address) {
        return UNSAFE.getFloat(address);
    }

    /**
     * Writes a float to the specific memory address
     *
     * @param address The address to write the float at
     * @param value   The float value
     */
    static void putFloat(long address, float value) {
        UNSAFE.putFloat(address, value);
    }

    /**
     * Reads a double from the specific memory address
     *
     * @param address The address to read the double from
     * @return The double read
     */
    static double getDouble(long address) {
        return UNSAFE.getDouble(address);
    }

    /**
     * Writes a double to the specific memory address
     *
     * @param address The address to write the double at
     * @param value   The double value
     */
    static void putDouble(long address, double value) {
        UNSAFE.putDouble(address, value);
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
