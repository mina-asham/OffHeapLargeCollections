package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.randomString;
import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.testRoundtrip;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

class StringSerializerTest {

    @Test
    void test() {
        for (int i = 0; i < 100; i++) {
            testRoundtrip(randomString(), StringSerializer::new);
        }

        for (int i = 0; i < 100; i++) {
            testRoundtrip(randomString(), () -> new StringSerializer(UTF_8));
        }

        for (int i = 0; i < 100; i++) {
            testRoundtrip(randomString(), () -> new StringSerializer(US_ASCII));
        }
    }
}