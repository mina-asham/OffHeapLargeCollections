package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class ShortSerializerTest {

    @Test
    void test() {
        testRoundtrip(Short.MIN_VALUE, ShortSerializer::new);
        testRoundtrip((short) -1, ShortSerializer::new);
        testRoundtrip((short) 0, ShortSerializer::new);
        testRoundtrip((short) 1, ShortSerializer::new);
        testRoundtrip(Short.MAX_VALUE, ShortSerializer::new);
    }
}