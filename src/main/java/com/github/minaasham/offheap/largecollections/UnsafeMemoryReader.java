package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;

/**
 * UnsafeMemoryReader, the internal implementation for the memory reader
 * This class is package private as it's an internal implementation detail
 */
class UnsafeMemoryReader implements MemoryReader {

    /**
     * The current address to read from
     */
    private long address;

    /**
     * The current number of bytes left to read
     */
    private long bytesLeft;

    /**
     * Reset the address and bytes left in the memory writer to new values
     *
     * @param address   The new address to set
     * @param bytesLeft The new number of bytes left to set
     * @return A reference to itself
     */
    UnsafeMemoryReader resetTo(long address, long bytesLeft) {
        this.address = address;
        this.bytesLeft = bytesLeft;
        return this;
    }

    @Override
    public long availableBytes() {
        return bytesLeft;
    }

    /**
     * Reads a byte
     *
     * @return The byte value
     */
    public byte readByte() {
        return UnsafeUtils.getByte(validateAndGetAddress(Byte.BYTES));
    }

    /**
     * Reads a short
     *
     * @return The short value
     */
    public short readShort() {
        return UnsafeUtils.getShort(validateAndGetAddress(Short.BYTES));
    }

    /**
     * Reads a character
     *
     * @return The character value
     */
    public char readChar() {
        return UnsafeUtils.getChar(validateAndGetAddress(Character.BYTES));
    }

    /**
     * Reads an integer
     *
     * @return The integer value
     */
    public int readInt() {
        return UnsafeUtils.getInt(validateAndGetAddress(Integer.BYTES));
    }

    /**
     * Reads a long
     *
     * @return The long value
     */
    public long readLong() {
        return UnsafeUtils.getLong(validateAndGetAddress(Long.BYTES));
    }

    /**
     * Reads a float
     *
     * @return The float value
     */
    public float readFloat() {
        return UnsafeUtils.getFloat(validateAndGetAddress(Float.BYTES));
    }

    /**
     * Reads a double
     *
     * @return The double value
     */
    public double readDouble() {
        return UnsafeUtils.getDouble(validateAndGetAddress(Double.BYTES));
    }

    /**
     * Validate that the number of bytes left is sufficient to read from,
     * and updates the address by the number of bytes we want to read
     *
     * @param bytes The number of bytes we want to read
     * @return The address to read from
     */
    private long validateAndGetAddress(int bytes) {
        bytesLeft -= bytes;
        if (bytesLeft < 0) throw new IllegalStateException("Cannot read more than object size!");
        long currentAddress = address;
        address += bytes;
        return currentAddress;
    }
}
