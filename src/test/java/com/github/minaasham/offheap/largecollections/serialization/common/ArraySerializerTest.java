package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;

class ArraySerializerTest {

    @Test
    void testStringArray() {
        String[] strings = IntStream.range(0, 1000).mapToObj(ignored -> randomString()).toArray(String[]::new);

        testRoundtrip(strings, () -> new ArraySerializer<>(new StringSerializer(UTF_8), String.class));
    }

    @Test
    void testIntArray() {
        Integer[] integers = IntStream.range(0, 1000).mapToObj(ignored -> randomInt()).toArray(Integer[]::new);

        testRoundtrip(integers, () -> new ArraySerializer<>(IntSerializer.INSTANCE, Integer.class));
    }
}