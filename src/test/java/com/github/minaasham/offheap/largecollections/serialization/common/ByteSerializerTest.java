package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class ByteSerializerTest {

    @Test
    void test() {
        testRoundtrip(Byte.MIN_VALUE, ByteSerializer::new);
        testRoundtrip((byte) -1, ByteSerializer::new);
        testRoundtrip((byte) 0, ByteSerializer::new);
        testRoundtrip((byte) 1, ByteSerializer::new);
        testRoundtrip(Byte.MAX_VALUE, ByteSerializer::new);
    }
}