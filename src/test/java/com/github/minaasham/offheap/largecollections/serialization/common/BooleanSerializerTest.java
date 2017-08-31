package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;

public class BooleanSerializerTest {

    @Test
    public void test() {
        testRoundtrip(true, BooleanSerializer::new);
        testRoundtrip(false, BooleanSerializer::new);
    }
}