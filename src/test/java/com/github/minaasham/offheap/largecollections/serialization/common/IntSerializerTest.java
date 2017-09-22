package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class IntSerializerTest {

    @Test
    void test() {
        testRoundtrip(Integer.MIN_VALUE, () -> IntSerializer.INSTANCE);
        testRoundtrip(-1, () -> IntSerializer.INSTANCE);
        testRoundtrip(0, () -> IntSerializer.INSTANCE);
        testRoundtrip(1, () -> IntSerializer.INSTANCE);
        testRoundtrip(Integer.MAX_VALUE, () -> IntSerializer.INSTANCE);
    }
}