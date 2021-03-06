package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class DoubleSerializerTest {

    @Test
    void test() {
        testRoundtrip(Double.MIN_VALUE, () -> DoubleSerializer.INSTANCE);
        testRoundtrip(-1.0, () -> DoubleSerializer.INSTANCE);
        testRoundtrip(0.0, () -> DoubleSerializer.INSTANCE);
        testRoundtrip(1.0, () -> DoubleSerializer.INSTANCE);
        testRoundtrip(Double.MAX_VALUE, () -> DoubleSerializer.INSTANCE);
    }
}