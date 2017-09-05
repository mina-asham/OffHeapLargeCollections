package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.common.IntSerializer;
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
    private static final LargeSet<String> EMPTY_SET = LargeHashSet.of(STRING_SERIALIZER, 1);

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
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.contains("element1"));
            assertTrue(set.contains("element2"));
        }
    }

    @Test
    public void testAdd() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertTrue(set.add("element1"));
            assertTrue(set.add("element2"));

            assertFalse(set.add("element1"));
            assertFalse(set.add("element2"));
        }
    }

    @Test
    public void testRemove() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertTrue(set.remove("element1"));
            assertTrue(set.remove("element2"));
        }
    }

    @Test
    public void testRemoveAll() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            List<String> randomValues = IntStream.range(0, 10000).mapToObj(ignored -> randomString()).collect(toList());

            randomValues.forEach(set::add);
            randomValues.forEach(set::remove);

            assertEquals(0, set.size());
        }
    }

    @Test
    public void testClear() {
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
    public void testSize() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            assertEquals(2, set.size());
        }
    }

    @Test
    public void testIterator() {
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

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsIfNoNext() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
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
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");

            Iterator<String> iterator = set.iterator();

            set.add("element2");

            iterator.next();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithContains() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.contains("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithAdd() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.add("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithRemove() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.remove("");
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClear() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.clear();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithSize() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.size();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithIterator() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfClosedWithClose() {
        LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5);
        set.close();
        set.close();
    }

    @Test
    public void testHashCode() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set.add("element1");
            set.add("element2");

            int expectedHashCode = "element1".hashCode() + "element2".hashCode();
            assertEquals(expectedHashCode, set.hashCode());
        }
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsSameObject() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertTrue(set.equals(set));
        }
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void testEqualsNull() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertFalse(set.equals(null));
        }
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEqualsWrongType() {
        try (LargeSet<String> set = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            assertFalse(set.equals(1));
        }
    }

    @Test
    public void testEqualsSizeMismatch() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element1");

            set1.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    public void testEqualsCloseMismatch() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5);
            set2.close();

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    public void testEqualsNotEqual() {
        try (LargeSet<String> set1 = LargeHashSet.of(STRING_SERIALIZER, 5);
             LargeSet<String> set2 = LargeHashSet.of(STRING_SERIALIZER, 5)) {
            set1.add("element1");
            set2.add("element2");

            assertFalse(set1.equals(set2));
        }
    }

    @Test
    public void testEquals() {
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
    public void testToString() {
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
    public void testFixedSize() {
        try (LargeSet<Integer> set = LargeHashSet.of(new IntSerializer())) {
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
    public void testStress() {
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