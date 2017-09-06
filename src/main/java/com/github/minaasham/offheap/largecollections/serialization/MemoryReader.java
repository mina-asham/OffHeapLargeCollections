package com.github.minaasham.offheap.largecollections.serialization;

/**
 * MemoryReader, the main interface to read an object during binary deserialization
 *
 * @see ObjectSerializer#deserialize(MemoryReader)
 */
public interface MemoryReader {

    /**
     * Reads a byte
     *
     * @return The byte read
     */
    byte readByte();

    /**
     * Reads a short
     *
     * @return The short read
     */
    short readShort();

    /**
     * Reads a character
     *
     * @return The character read
     */
    char readChar();

    /**
     * Reads an integer
     *
     * @return The integer read
     */
    int readInt();

    /**
     * Reads a long
     *
     * @return The long read
     */
    long readLong();

    /**
     * Reads a float
     *
     * @return The float read
     */
    float readFloat();

    /**
     * Reads a double
     *
     * @return The double read
     */
    double readDouble();
}
