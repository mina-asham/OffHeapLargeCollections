package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class ShortSerializerTest {

    @Test
    void test() {
        testRoundtrip(Short.MIN_VALUE, () -> ShortSerializer.INSTANCE);
        testRoundtrip((short) -1, () -> ShortSerializer.INSTANCE);
        testRoundtrip((short) 0, () -> ShortSerializer.INSTANCE);
        testRoundtrip((short) 1, () -> ShortSerializer.INSTANCE);
        testRoundtrip(Short.MAX_VALUE, () -> ShortSerializer.INSTANCE);
    }
}