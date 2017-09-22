package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.common.IntSerializer;
import com.github.minaasham.offheap.largecollections.serialization.common.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.randomString;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class LargeHashMapTest {

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();
    private static final LargeMap<String, String> EMPTY_MAP = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1);

    @AfterAll
    static void tearDownAll() {
        EMPTY_MAP.close();
    }

    @Test
    void testThrowsIfNullKeySerializer() {
        assertThrows(NullPointerException.class, () -> LargeHashMap.of(null, STRING_SERIALIZER).close());
    }

    @Test
    void testThrowsIfNullObjectSerializer() {
        assertThrows(NullPointerException.class, () -> LargeHashMap.of(null, STRING_SERIALIZER).close());
    }

    @Test
    void testThrowsIfNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, -1).close());
    }

    @Test
    void testThrowsIfZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0).close());
    }

    @Test
    void testSucceedsCapacity() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 3).close();
    }

    @Test
    void testThrowsIfNegativeLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, -1.0).close());
    }

    @Test
    void testThrowsIfZeroLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0.0).close());
    }

    @Test
    void testSucceedsLoadFactor() {
        LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 0.5).close();
    }

    @Test
    void testThrowsIfOneLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1.0).close());
    }

    @Test
    void testThrowsIfMoreThanOneLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 1.1).close());
    }

    @Test
    void testThrowsIfNullKeyInGet() {
        assertThrows(NullPointerException.class, () -> EMPTY_MAP.get(null));
    }

    @Test
    void testThrowsIfNullKeyInPut() {
        assertThrows(NullPointerException.class, () -> EMPTY_MAP.put(null, "value"));
    }

    @Test
    void testThrowsIfNullValueInPut() {
        assertThrows(NullPointerException.class, () -> EMPTY_MAP.put("key", null));
    }

    @Test
    void testThrowsIfNullKeyInRemove() {
        assertThrows(NullPointerException.class, () -> EMPTY_MAP.remove(null));
    }

    @Test
    void testGet() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals("value1", map.get("key1"));
            assertEquals("value2", map.get("key2"));
        }
    }

    @Test
    void testPut() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            assertNull(map.put("key1", "value1"));
            assertNull(map.put("key2", "value2"));

            assertEquals("value1", map.put("key1", "value11"));
            assertEquals("value2", map.put("key2", "value22"));
        }
    }

    @Test
    void testRemove() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals("value1", map.remove("key1"));
            assertEquals("value2", map.remove("key2"));
        }
    }

    @Test
    void testRemoveAll() {
        try (LargeMap<String, String> set = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            List<String> randomValues = IntStream.range(0, 10000).mapToObj(ignored -> randomString()).collect(toList());

            randomValues.forEach(key -> set.put(key, randomString()));
            randomValues.forEach(set::remove);

            assertEquals(0, set.size());
        }
    }

    @Test
    void testClear() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");
            map.clear();

            assertNull(map.get("key1"));
            assertNull(map.get("key2"));
            assertEquals(0, map.size());
        }
    }

    @Test
    void testSize() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            assertEquals(2, map.size());
        }
    }

    @Test
    void testIterator() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
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

    @Test
    void testIteratorThrowsIfNoNext() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            Iterator<Entry<String, String>> iterator = map.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            assertThrows(NoSuchElementException.class, iterator::next);
        }
    }

    @Test
    void testIteratorThrowsIfMapChanges() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");

            Iterator<Entry<String, String>> iterator = map.iterator();

            map.put("key2", "value2");

            assertThrows(ConcurrentModificationException.class, iterator::next);
        }
    }

    @Test
    void testThrowsIfClosedWithGet() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, () -> map.get(""));
    }

    @Test
    void testThrowsIfClosedWithPut() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, () -> map.put("", ""));
    }

    @Test
    void testThrowsIfClosedWithRemove() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, () -> map.remove(""));
    }

    @Test
    void testThrowsIfClosedWithClear() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, map::clear);
    }

    @Test
    void testThrowsIfClosedWithSize() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, map::size);
    }

    @Test
    void testThrowsIfClosedWithIterator() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, map::iterator);
    }

    @Test
    void testThrowsIfClosedWithClose() {
        LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
        map.close();
        assertThrows(IllegalStateException.class, map::close);
    }

    @Test
    void testHashCode() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            int expectedHashCode = ("key1".hashCode() ^ "value1".hashCode()) + ("key2".hashCode() ^ "value2".hashCode());
            assertEquals(expectedHashCode, map.hashCode());
        }
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEqualsSameObject() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            assertTrue(map.equals(map));
        }
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    void testEqualsNull() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            assertFalse(map.equals(null));
        }
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    void testEqualsWrongType() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            assertFalse(map.equals(1));
        }
    }

    @Test
    void testEqualsSizeMismatch() {
        try (LargeMap<String, String> map1 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
             LargeMap<String, String> map2 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map1.put("key1", "value1");
            map2.put("key1", "value1");

            map1.put("key2", "value2");

            assertFalse(map1.equals(map2));
        }
    }

    @Test
    void testEqualsCloseMismatch() {
        try (LargeMap<String, String> map1 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            LargeMap<String, String> map2 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
            map2.close();

            assertFalse(map1.equals(map2));
        }
    }

    @Test
    void testEqualsNotEqualKeyDoesntExist() {
        try (LargeMap<String, String> map1 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
             LargeMap<String, String> map2 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map1.put("key1", "value1");
            map2.put("key2", "value2");

            assertFalse(map1.equals(map2));
        }
    }

    @Test
    void testEqualsNotEqualValueDoesntMatch() {
        try (LargeMap<String, String> map1 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
             LargeMap<String, String> map2 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map1.put("key1", "value1");
            map2.put("key1", "value2");

            assertFalse(map1.equals(map2));
        }
    }

    @Test
    void testEquals() {
        try (LargeMap<String, String> map1 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5);
             LargeMap<String, String> map2 = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map1.put("key1", "value1");
            map2.put("key1", "value1");

            map1.put("key2", "value2");
            map2.put("key2", "value2");

            assertTrue(map1.equals(map2));
        }
    }

    @Test
    void testToString() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
            map.put("key1", "value1");
            map.put("key2", "value2");

            Set<String> expectedLines = unmodifiableSet(new HashSet<>(asList("  key1=value1", "  key2=value2")));

            String lineSeparator = System.lineSeparator();
            String[] actualLines = map.toString().split("," + lineSeparator + "|" + lineSeparator);

            assertEquals("{", actualLines[0]);
            for (int i = 1; i < actualLines.length - 1; i++) {
                assertTrue(expectedLines.contains(actualLines[i]));
            }
            assertEquals("}", actualLines[actualLines.length - 1]);
        }
    }

    @Test
    void testFixedSize() {
        try (LargeMap<Integer, Integer> map = LargeHashMap.of(IntSerializer.INSTANCE, IntSerializer.INSTANCE)) {
            assertNull(map.put(1, 10));
            assertNull(map.put(2, 20));
            assertNull(map.put(3, 30));

            assertEquals((Integer) 10, map.remove(1));
            assertEquals((Integer) 20, map.remove(2));
            assertEquals((Integer) 30, map.remove(3));

            assertEquals(0, map.size());
        }
    }

    @Test
    void testBadHashAndHighLoad() {
        try (LargeMap<BadHashInteger, Integer> map = LargeHashMap.of(new BadHashIntegerSerializer(), IntSerializer.INSTANCE, 0.99, 1020)) {
            int limit = 1000;

            for (int i = 0; i <= limit; i++) {
                assertNull(map.put(new BadHashInteger(i), i));
            }

            for (int i = limit; i >= 0; i--) {
                assertEquals((Integer) i, map.remove(new BadHashInteger(i)));
            }

            for (int i = limit; i >= 0; i--) {
                assertNull(map.put(new BadHashInteger(i), i));
            }

            for (int i = 0; i <= limit; i++) {
                assertEquals((Integer) i, map.remove(new BadHashInteger(i)));
            }

            assertEquals(0, map.size());
        }
    }

    @Test
    void testStress() {
        try (LargeMap<String, String> map = LargeHashMap.of(STRING_SERIALIZER, STRING_SERIALIZER, 5)) {
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