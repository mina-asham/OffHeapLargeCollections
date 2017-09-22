package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.common.IntSerializer;
import com.github.minaasham.offheap.largecollections.serialization.common.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.minaasham.offheap.largecollections.serialization.SerializationTestUtils.randomString;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class LargeHashSetTest {

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();
    private static final LargeSet<String> EMPTY_SET = LargeHashSet.of(STRING_SERIALIZER, 1);

    @AfterAll
    static void tearDownAll() {
        EMPTY_SET.close();
    }

    @Test
    void testThrowsIfNullElementSerializer() {
        assertThrows(NullPointerException.class, () -> LargeHashSet.of(null).close());
    }

    @Test
    void testThrowsIfNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, -1).close());
    }

    @Test
    void testThrowsIfZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, 0).close());
    }

    @Test
    void testSucceedsCapacity() {
        LargeHashSet.of(STRING_SERIALIZER, 3).close();
    }

    @Test
    void testThrowsIfNegativeLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, -1.0).close());
    }

    @Test
    void testThrowsIfZeroLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, 0.0).close());
    }

    @Test
    void testSucceedsLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, 0.5).close();
    }

    @Test
    void testThrowsIfOneLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, 1.0).close());
    }

    @Test
    void testThrowsIfMoreThanOneLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> LargeHashSet.of(STRING_SERIALIZER, 1.1).close());
    }

    @Test
    void testThrowsIfNullElementInContains() {
        assertThrows(NullPointerException.class, () -> EMPTY_SET.contains(null));
    }

    @Test
    void testThrowsIfNullElementInAdd() {
        assertThrows(NullPointerException.class, () -> EMPTY_SET.add(null));
    }

    @Test
    void testThrowsIfNullElementInRemove() {
        assertThrows(NullPointerException.class, () -> EMPTY_SET.remove(null));
    }

    @Test
    void testContains() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.contains("element1"));
            assertTrue(set.contains("element2"));
        }
    }

    @Test
    void testAdd() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertTrue(set.add("element1"));
            assertTrue(set.add("element2"));

            assertFalse(set.add("element1"));
            assertFalse(set.add("element2"));
        }
    }

    @Test
    void testRemove() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.remove("element1"));
            assertTrue(set.remove("element2"));
        }
    }

    @Test
    void testRemoveAll() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            List<String> randomValues = IntStream.range(0, 10000).mapToObj(ignored -> randomString()).collect(toList());

            randomValues.forEach(set::add);
            randomValues.forEach(set::remove);

            assertEquals(0, set.size());
        }
    }

    @Test
    void testClear() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");
            set.clear();

            assertFalse(set.contains("element1"));
            assertFalse(set.contains("element2"));
            assertEquals(0, set.size());
        }
    }

    @Test
    void testSize() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertEquals(2, set.size());
        }
    }

    @Test
    void testIterator() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            Iterator<String> iterator = set.iterator();
            assertTrue(iterator.hasNext());
            assertEquals("element1", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("element2", iterator.next());
            assertFalse(iterator.hasNext());
        }
    }

    @Test
    void testIteratorThrowsIfNoNext() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            Iterator<String> iterator = set.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            assertThrows(NoSuchElementException.class, iterator::next);
        }
    }

    @Test
    void testIteratorThrowsIfSetChanges() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");

            Iterator<String> iterator = set.iterator();

            set.add("element2");

            assertThrows(ConcurrentModificationException.class, iterator::next);
        }
    }

    @Test
    void testThrowsIfClosedWithContains() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, () -> set.contains(""));
    }

    @Test
    void testThrowsIfClosedWithAdd() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, () -> set.add(""));
    }

    @Test
    void testThrowsIfClosedWithRemove() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, () -> set.remove(""));
    }

    @Test
    void testThrowsIfClosedWithClear() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, set::clear);
    }

    @Test
    void testThrowsIfClosedWithSize() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, set::size);
    }

    @Test
    void testThrowsIfClosedWithIterator() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, set::iterator);
    }

    @Test
    void testThrowsIfClosedWithClose() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        assertThrows(IllegalStateException.class, set::close);
    }

    @Test
    void testHashCode() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            int expectedHashCode = "element1".hashCode() + "element2".hashCode();
            assertEquals(expectedHashCode, set.hashCode());
        }
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEqualsSameObject() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertTrue(set.equals(set));
        }
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    void testEqualsNull() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertFalse(set.equals(null));
        }
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    void testEqualsWrongType() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertFalse(set.equals(1));
        }
    }

    @Test
    void testEqualsSizeMismatch() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element1");

            set1.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    void testEqualsCloseMismatch() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5);
            set2.close();

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    void testEqualsNotEqual() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    void testEquals() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element1");

            set1.add("element2");
            set2.add("element2");

            assertTrue(set1.equals(set2));
        }
    }

    @Test
    void testToString() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            Set<String> expectedLines = unmodifiableSet(new HashSet<>(asList("  element1", "  element2")));

            String lineSeparator = System.lineSeparator();
            String[] actualLines = set.toString().split("," + lineSeparator + "|" + lineSeparator);

            assertEquals("{", actualLines[0]);
            for (int i = 1; i < actualLines.length - 1; i++) {
                assertTrue(expectedLines.contains(actualLines[i]));
            }
            assertEquals("}", actualLines[actualLines.length - 1]);
        }
    }

    @Test
    void testFixedSize() {
        try (LargeSet<Integer> set = LargeHashSet.of(IntSerializer.INSTANCE)) {
            assertTrue(set.add(1));
            assertTrue(set.add(2));
            assertTrue(set.add(3));

            assertTrue(set.remove(1));
            assertTrue(set.remove(2));
            assertTrue(set.remove(3));

            assertEquals(0, set.size());
        }
    }

    @Test
    void testBadHashAndHighLoad() {
        try (LargeSet<BadHashInteger> set = LargeHashSet.of(new BadHashIntegerSerializer(), 0.99, 1020)) {
            int limit = 1000;

            for (int i = 0; i <= limit; i++) {
                assertTrue(set.add(new BadHashInteger(i)));
            }

            for (int i = limit; i >= 0; i--) {
                assertTrue(set.remove(new BadHashInteger(i)));
            }

            for (int i = limit; i >= 0; i--) {
                assertTrue(set.add(new BadHashInteger(i)));
            }

            for (int i = 0; i <= limit; i++) {
                assertTrue(set.remove(new BadHashInteger(i)));
            }

            assertEquals(0, set.size());
        }
    }

    @Test
    void testStress() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            Set<String> expectedSet = new HashSet<>();

            // Add
            IntStream.range(0, 10000).forEach(ignored -> {
                String element = randomString();
                assertEquals(expectedSet.add(element), set.add(element));
            });

            // Contains
            IntStream.range(0, 10000).forEach(ignored -> {
                String element = randomString();
                assertEquals(expectedSet.contains(element), set.contains(element));
            });

            // Remove
            IntStream.range(0, 10000).forEach(ignored -> {
                String element = randomString();
                assertEquals(expectedSet.remove(element), set.remove(element));
            });

            // Size
            assertEquals(expectedSet.size(), set.size());

            // Iterator
            set.iterator().forEachRemaining(element -> assertTrue(expectedSet.contains(element)));
            expectedSet.forEach(element -> assertTrue(set.contains(element)));

            // Clear
            set.clear();
            expectedSet.forEach(element -> assertFalse(set.contains(element)));
            assertEquals(0, set.size());
            assertFalse(set.iterator().hasNext());
        }
    }
}