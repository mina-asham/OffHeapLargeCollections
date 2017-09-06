package com.github.minaasham.offheap.largecollections.serialization.common;

import org.junit.Test;

import java.util.stream.IntStream;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.*;

public class ArraySerializerTest {

    @Test
    public void testStringArray() {
        String[] strings = IntStream.range(0, 1000).mapToObj(ignored -> randomString()).toArray(String[]::new);

        testRoundtrip(strings, () -> new ArraySerializer<>(new StringSerializer(), String.class));
    }

    @Test
    public void testIntArray() {
        Integer[] integers = IntStream.range(0, 1000).mapToObj(ignored -> randomInt()).toArray(Integer[]::new);

        testRoundtrip(integers, () -> new ArraySerializer<>(new IntSerializer(), Integer.class));
    }
}