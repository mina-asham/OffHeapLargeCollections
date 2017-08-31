package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

public class DoubleSerializerTest {

    @Test
    public void test() {
        testRoundtrip(Double.MIN_VALUE, DoubleSerializer::new);
        testRoundtrip(-1.0, DoubleSerializer::new);
        testRoundtrip(0.0, DoubleSerializer::new);
        testRoundtrip(1.0, DoubleSerializer::new);
        testRoundtrip(Double.MAX_VALUE, DoubleSerializer::new);
    }
}