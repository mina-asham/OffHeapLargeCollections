package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class BooleanSerializerTest {

    @Test
    void test() {
        testRoundtrip(true, () -> BooleanSerializer.INSTANCE);
        testRoundtrip(false, () -> BooleanSerializer.INSTANCE);
    }
}