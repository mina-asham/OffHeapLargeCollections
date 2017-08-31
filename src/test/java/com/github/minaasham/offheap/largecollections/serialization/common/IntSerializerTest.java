package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

public class IntSerializerTest {

    @Test
    public void test() {
        testRoundtrip(Integer.MIN_VALUE, IntSerializer::new);
        testRoundtrip(-1, IntSerializer::new);
        testRoundtrip(0, IntSerializer::new);
        testRoundtrip(1, IntSerializer::new);
        testRoundtrip(Integer.MAX_VALUE, IntSerializer::new);
    }
}