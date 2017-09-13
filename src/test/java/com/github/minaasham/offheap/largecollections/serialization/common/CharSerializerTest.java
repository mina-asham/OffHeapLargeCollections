package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class CharSerializerTest {

    @Test
    void test() {
        testRoundtrip(Character.MIN_VALUE, CharSerializer::new);
        testRoundtrip('a', CharSerializer::new);
        testRoundtrip('b', CharSerializer::new);
        testRoundtrip('x', CharSerializer::new);
        testRoundtrip(Character.MAX_VALUE, CharSerializer::new);
    }
}