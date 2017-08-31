package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

public class LongSerializerTest {

    @Test
    public void test() {
        testRoundtrip(Long.MIN_VALUE, LongSerializer::new);
        testRoundtrip(-1L, LongSerializer::new);
        testRoundtrip(0L, LongSerializer::new);
        testRoundtrip(1L, LongSerializer::new);
        testRoundtrip(Long.MAX_VALUE, LongSerializer::new);
    }
}