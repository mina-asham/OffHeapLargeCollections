package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.common.StringSerializer;
import org.junit.AfterClass;
import org.junit.Test;

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
import static org.junit.Assert.*;

public class LargeHashSetTest {

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();
    private static final LargeHashSet<String> EMPTY_SET = LargeHashSet.of(STRING_SERIALIZER, 1);

    @AfterClass
    public static void tearDown() {
        EMPTY_SET.close();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullElementSerializer() {
        LargeHashSet.of(null).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfNegativeCapacity() {
        LargeHashSet.of(STRING_SERIALIZER, -1).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfZeroCapacity() {
        LargeHashSet.of(STRING_SERIALIZER, 0).close();
    }

    @Test
    public void testSucceedsCapacity() {
        LargeHashSet.of(STRING_SERIALIZER, 3).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfNegativeLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, -1.0).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfZeroLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, 0.0).close();
    }

    @Test
    public void testSucceedsLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, 0.5).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfOneLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, 1.0).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfMoreThanOneLoadFactor() {
        LargeHashSet.of(STRING_SERIALIZER, 1.1).close();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullElementInContains() {
        EMPTY_SET.contains(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullElementInAdd() {
        EMPTY_SET.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfNullElementInRemove() {
        EMPTY_SET.remove(null);
    }

    @Test
    public void testContains() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.contains("element1"));
            assertTrue(set.contains("element2"));
        }
    }

    @Test
    public void testAdd() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertTrue(set.add("element1"));
            assertTrue(set.add("element2"));

            assertFalse(set.add("element1"));
            assertFalse(set.add("element2"));
        }
    }

    @Test
    public void testRemove() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.remove("element1"));
            assertTrue(set.remove("element2"));
        }
    }

    @Test
    public void testRemoveAll() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            List<String> randomValues = IntStream.range(0, 10000).mapToObj(ignored -> randomString()).collect(toList());

            randomValues.forEach(set::add);
            randomValues.forEach(set::remove);

            assertEquals(0, set.size());
        }
    }

    @Test
    public void testClear() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");
            set.clear();

            assertFalse(set.contains("element1"));
            assertFalse(set.contains("element2"));
            assertEquals(0, set.size());
        }
    }

    @Test
    public void testSize() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertEquals(2, set.size());
        }
    }

    @Test
    public void testIterator() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
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

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsIfNoNext() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            Iterator<String> iterator = set.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            iterator.next();
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsIfSetChanges() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");

            Iterator<String> iterator = set.iterator();

            set.add("element2");

            iterator.next();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithContains() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.contains("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithAdd() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.add("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithRemove() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.remove("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClear() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.clear();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithSize() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.size();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithIterator() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClose() {
        LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.close();
    }

    @Test
    public void testHashCode() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            int expectedHashCode = "element1".hashCode() + "element2".hashCode();
            assertEquals(expectedHashCode, set.hashCode());
        }
    }

    @Test
    public void testEqualsSizeMismatch() {
        try (LargeHashSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeHashSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element1");

            set1.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    public void testEqualsNotEqual() {
        try (LargeHashSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeHashSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    public void testEquals() {
        try (LargeHashSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeHashSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element1");

            set1.add("element2");
            set2.add("element2");

            assertTrue(set1.equals(set2));
        }
    }

    @Test
    public void testToString() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
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
    public void testStress() {
        try (LargeHashSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
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