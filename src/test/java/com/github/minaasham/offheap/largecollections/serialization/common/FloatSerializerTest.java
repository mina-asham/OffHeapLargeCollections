package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

public class FloatSerializerTest {

    @Test
    public void test() {
        testRoundtrip(Float.MIN_VALUE, FloatSerializer::new);
        testRoundtrip(-1.0f, FloatSerializer::new);
        testRoundtrip(0.0f, FloatSerializer::new);
        testRoundtrip(1.0f, FloatSerializer::new);
        testRoundtrip(Float.MAX_VALUE, FloatSerializer::new);
    }
}