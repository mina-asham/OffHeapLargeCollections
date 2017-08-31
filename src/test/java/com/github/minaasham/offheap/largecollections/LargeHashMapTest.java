package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.common.StringSerializer;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.randomString;
import static org.junit.Assert.*;

public class LargeHashMapTest {

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();
    private static final LargeHashMap<String, String> EMPTY_MAP = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1);

    @AfterClass
    public static void tearDown() {
        EMPTY_MAP.close();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullKeySerializer() {
        LargeHashMap.of(null, STRING_SERIALIZER).close();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullObjectSerializer() {
        LargeHashMap.of(null, STRING_SERIALIZER).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfNegativeCapacity() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, -1).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfZeroCapacity() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0).close();
    }

    @Test
    public void testSucceedsCapacity() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 3).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfNegativeLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, -1.0).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfZeroLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0.0).close();
    }

    @Test
    public void testSucceedsLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0.5).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfOneLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1.0).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfMoreThanOneLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1.1).close();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullKeyInGet() {
        EMPTY_MAP.get(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullKeyInPut() {
        EMPTY_MAP.put(null, "value");
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullValueInPut() {
        EMPTY_MAP.put("key", null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullKeyInRemove() {
        EMPTY_MAP.remove(null);
    }

    @Test
    public void testGet() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals("value1", map.get("key1"));
            assertEquals("value2", map.get("key2"));
        }
    }

    @Test
    public void testPut() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            assertNull(map.put("key1", "value1"));
            assertNull(map.put("key2", "value2"));

            assertEquals("value1", map.put("key1", "value11"));
            assertEquals("value2", map.put("key2", "value22"));
        }
    }

    @Test
    public void testRemove() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals("value1", map.remove("key1"));
            assertEquals("value2", map.remove("key2"));
        }
    }

    @Test
    public void testClear() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");
            map.clear();

            assertNull(map.get("key1"));
            assertNull(map.get("key2"));
            assertEquals(0, map.size());
        }
    }

    @Test
    public void testSize() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals(2, map.size());
        }
    }

    @Test
    public void testIterator() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            Iterator<Entry<String, String>> iterator = map.iterator();
            assertTrue(iterator.hasNext());
            assertEquals(new SimpleEntry<>("key1", "value1"), iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals(new SimpleEntry<>("key2", "value2"), iterator.next());
            assertFalse(iterator.hasNext());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsIfNoNext() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            Iterator<Entry<String, String>> iterator = map.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            iterator.next();
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsIfMapChangeds() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");

            Iterator<Entry<String, String>> iterator = map.iterator();

            map.put("key2", "value2");

            iterator.next();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithGet() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.get("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithPut() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.put("", "");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithRemove() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.remove("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClear() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.clear();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithSize() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.size();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithIterator() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClose() {
        LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        map.close();
    }

    @Test
    public void testStress() {
        try (LargeHashMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            Map<String, String> expectedMap = new HashMap<>();

            // Put
            IntStream.range(0, 10000).forEach(ignored -> {
                String key = randomString();
                String value = randomString();
                assertEquals(expectedMap.put(key, value), map.put(key, value));
            });

            // Get
            IntStream.range(0, 10000).forEach(ignored -> {
                String key = randomString();
                assertEquals(expectedMap.get(key), map.get(key));
            });

            // Remove
            IntStream.range(0, 10000).forEach(ignored -> {
                String key = randomString();
                assertEquals(expectedMap.remove(key), map.remove(key));
            });

            // Size
            assertEquals(expectedMap.size(), map.size());

            // Iterator
            map.iterator().forEachRemaining(entry -> assertEquals(expectedMap.get(entry.getKey()), entry.getValue()));
            expectedMap.forEach((key, value) -> assertEquals(map.get(key), value));

            // Clear
            map.clear();
            expectedMap.keySet().forEach(key -> assertNull(map.get(key)));
            assertEquals(0, map.size());
            assertFalse(map.iterator().hasNext());
        }
    }
}