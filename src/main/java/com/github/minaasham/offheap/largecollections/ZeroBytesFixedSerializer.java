package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.FixedSizeObjectSerializer;
import com.github.minaasham.offheap.largecollections.serialization.MemoryReader;
import com.github.minaasham.offheap.largecollections.serialization.MemoryWriter;

/**
 * A zero-bytes fixed serializer, used to create sets from map implementations without using more memory
 */
final class ZeroBytesFixedSerializer extends FixedSizeObjectSerializer<Object> {

    /**
     * Dummy object that serves as a place holder the actual value
     */
    static final Object DUMMY = new Object();

    /**
     * Singleton instance of the {@link ZeroBytesFixedSerializer} class
     */
    static final ZeroBytesFixedSerializer INSTANCE = new ZeroBytesFixedSerializer();

    /**
     * ZeroBytesFixedSerializer constructor
     */
    private ZeroBytesFixedSerializer() {
        super(0);
    }

    /**
     * No-op serializer
     *
     * @param writer The writer that is used for serialization
     * @param object The object to serialize
     */
    @Override
    public void serialize(MemoryWriter writer, Object object) {
    }

    /**
     * No-op deserializer, always returns the same object
     *
     * @param reader The reader that is used for deserialization
     * @return Always returns the DUMMY object
     */
    @Override
    public Object deserialize(MemoryReader reader) {
        return DUMMY;
    }
}
