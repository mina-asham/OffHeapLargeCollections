package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

class LongSerializerTest {

    @Test
    void test() {
        testRoundtrip(Long.MIN_VALUE, LongSerializer::new);
        testRoundtrip(-1L, LongSerializer::new);
        testRoundtrip(0L, LongSerializer::new);
        testRoundtrip(1L, LongSerializer::new);
        testRoundtrip(Long.MAX_VALUE, LongSerializer::new);
    }
}