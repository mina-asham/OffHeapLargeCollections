package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * UnsafeMemoryWriter, the internal implementation for the memory writer
 * This class is package private as it's an internal implementation detail
 */
class UnsafeMemoryWriter implements MemoryWriter {

    /**
     * The current address to write to
     */
    private long address;

    /**
     * The current number of bytes left to write
     */
    private long bytesLeft;

    /**
     * Reset the address and bytes left in the memory writer to new values
     *
     * @param address   The new address to set
     * @param bytesLeft The new number of bytes left to set
     * @return A reference to itself
     */
    UnsafeMemoryWriter resetTo(long address, long bytesLeft) {
        this.address = address;
        this.bytesLeft = bytesLeft;
        return this;
    }

    /**
     * Write a byte
     *
     * @param value The byte value
     */
    public void writeByte(byte value) {
        UnsafeUtils.putByte(validateAndGetAddress(Byte.BYTES), value);
    }

    /**
     * Write a short
     *
     * @param value The short value
     */
    public void writeShort(short value) {
        UnsafeUtils.putShort(validateAndGetAddress(Short.BYTES), value);
    }

    /**
     * Write a character
     *
     * @param value The character value
     */
    public void writeChar(char value) {
        UnsafeUtils.putChar(validateAndGetAddress(Character.BYTES), value);
    }

    /**
     * Write an integer
     *
     * @param value The integer value
     */
    public void writeInt(int value) {
        UnsafeUtils.putInt(validateAndGetAddress(Integer.BYTES), value);
    }

    /**
     * Write a long
     *
     * @param value The long value
     */
    public void writeLong(long value) {
        UnsafeUtils.putLong(validateAndGetAddress(Long.BYTES), value);
    }

    /**
     * Write a float
     *
     * @param value The float value
     */
    public void writeFloat(float value) {
        UnsafeUtils.putFloat(validateAndGetAddress(Float.BYTES), value);
    }

    /**
     * Write a double
     *
     * @param value The double value
     */
    public void writeDouble(double value) {
        UnsafeUtils.putDouble(validateAndGetAddress(Double.BYTES), value);
    }

    /**
     * Validate that the number of bytes left is sufficient to write to,
     * and updates the address by the number of bytes we want to write
     *
     * @param bytes The number of bytes we want to write
     * @return The address to write to
     */
    private long validateAndGetAddress(int bytes) {
        bytesLeft -= bytes;
        if (bytesLeft < 0) throw new IllegalStateException("Cannot write more than object size!");
        long currentAddress = address;
        address += bytes;
        return currentAddress;
    }
}
