package com.github.minaasham.offheap.largecollections.serialization.common;

import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;
import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.stream.Stream;

/**
 * ArraySerializer, a generic array serializer that utilizes an inner element serializer
 *
 * @param <T> The type of elements in the array
 */
@RequiredArgsConstructor
public class ArraySerializer<T> implements ObjectSerializer<T[]> {

    /**
     * The inner element serializer
     */
    private final ObjectSerializer<T> elementSerializer;

    /**
     * The element type for creating new arrays in a strong-typed/checked manner
     */
    private final Class<T> elementType;

    /**
     * Serializes the array elements using a memory writer
     *
     * @param writer The writer that is used for serialization
     * @param object The object to serialize
     */
    @Override
    public final void serialize(MemoryWriter writer, T[] object) {
        writer.writeInt(object.length);

        for (T element : object) {
            elementSerializer.serialize(writer, element);
        }
    }

    /**
     * Deserializes the array element using a memory reader
     *
     * @param reader The reader that is used for deserialization
     * @return The deserialized array
     */
    @SuppressWarnings("unchecked")
    @Override
    public final T[] deserialize(MemoryReader reader) {
        int arraySize = reader.readInt();
        T[] array = (T[]) Array.newInstance(elementType, arraySize);

        for (int i = 0; i < arraySize; i++) {
            array[i] = elementSerializer.deserialize(reader);
        }

        return array;
    }

    /**
     * Returns the array size in bytes, this is a single integer for
     * the array size plus each individual element size
     *
     * @param object The object to the get the size of
     * @return The size of the passed array
     */
    @Override
    public final int sizeInBytes(T[] object) {
        return Integer.BYTES + Stream.of(object).mapToInt(elementSerializer::sizeInBytes).sum();
    }
}
