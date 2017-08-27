package com.github.minaasham.offheap.largecollections.serialization;

/**
 * MemoryWriter, the main interface to write an object during binary serialization
 *
 * @see ObjectSerializer#serialize(MemoryWriter, Object)
 */
public interface MemoryWriter {

    /**
     * Writes a byte value
     *
     * @param value The byte value
     */
    void writeByte(byte value);

    /**
     * Writes a short value
     *
     * @param value The short value
     */
    void writeShort(short value);

    /**
     * Writes a character value
     *
     * @param value The character value
     */
    void writeChar(char value);

    /**
     * Writes an integer value
     *
     * @param value The integer value
     */
    void writeInt(int value);

    /**
     * Writes a long value
     *
     * @param value The long value
     */
    void writeLong(long value);

    /**
     * Writes a float value
     *
     * @param value The float value
     */
    void writeFloat(float value);

    /**
     * Writes a double value
     *
     * @param value The byte double
     */
    void writeDouble(double value);
}
