package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class CharSerializerTest {

    @Test
    void test() {
        testRoundtrip(Character.MIN_VALUE, () -> CharSerializer.INSTANCE);
        testRoundtrip('a', () -> CharSerializer.INSTANCE);
        testRoundtrip('b', () -> CharSerializer.INSTANCE);
        testRoundtrip('x', () -> CharSerializer.INSTANCE);
        testRoundtrip(Character.MAX_VALUE, () -> CharSerializer.INSTANCE);
    }
}