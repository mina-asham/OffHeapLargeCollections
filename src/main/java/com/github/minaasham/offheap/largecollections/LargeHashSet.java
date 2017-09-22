package com.github.minaasham.offheap.largecollections;

import com.github.minaasham.offheap.largecollections.serialization.ObjectSerializer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * LargeHashSet, an open address hash set that can handle a large number of entries
 * It utilizes the {@link sun.misc.Unsafe} object to allocate memory, hence it's not limited by the GC
 *
 * @param <E> The element type, cannot be null
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LargeHashSet<E> implements LargeSet<E> {

    /**
     * The inner map used for this set
     */
    private final LargeHashMap<E, Object> inner;

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE));
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param loadFactor        The load factor
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, double loadFactor) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, loadFactor));
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param capacity          The initial capacity
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, long capacity) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, capacity));
    }

    /**
     * Factory method for creating a {@link LargeHashSet} object
     *
     * @param elementSerializer The element serializer
     * @param loadFactor        The load factor, allowed values are more than 0 and less than or equal to 1
     * @param capacity          The initial capacity, must be a least 1
     * @param <E>               The element type
     * @return A {@link LargeHashSet} object
     */
    public static <E> LargeHashSet<E> of(ObjectSerializer<E> elementSerializer, double loadFactor, long capacity) {
        return new LargeHashSet<>(LargeHashMap.of(elementSerializer, ZeroBytesFixedSerializer.INSTANCE, loadFactor, capacity));
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element
     *
     * @param element Element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    @Override
    public boolean contains(E element) {
        return inner.get(element) != null;
    }

    /**
     * Adds the specified element to this set if it is not already present
     *
     * @param element Element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    @Override
    public boolean add(E element) {
        return inner.put(element, ZeroBytesFixedSerializer.DUMMY) == null;
    }

    /**
     * Removes the specified element from this set if it is present
     *
     * @param element Element to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     */
    @Override
    public boolean remove(E element) {
        return inner.remove(element) != null;
    }

    /**
     * Clear the set from all elements
     */
    @Override
    public void clear() {
        inner.clear();
    }

    /**
     * Gets the current size of the set
     *
     * @return The size of the set
     */
    @Override
    public long size() {
        return inner.size();
    }

    /**
     * Returns an iterator over elements of type {@code E}
     *
     * @return The set's iterator
     */
    @Override
    public Iterator<E> iterator() {
        return new LargeHashSetIterator<>(inner.iterator());
    }

    /**
     * Disposes of the off heap allocations
     */
    @Override
    public void close() {
        inner.close();
    }

    /**
     * Returns the hash code value for this set.  The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set.
     * This implementation iterates over the set, calling the
     * <tt>hashCode</tt> method on each element in the set, and adding up
     * the results.
     *
     * @return the hash code value for this set
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E element : this) {
            hashCode += element.hashCode();
        }
        return hashCode;
    }

    /**
     * Compares the specified object with this set for equality.
     * Returns {@code true} if the given object is a set with the same
     * mappings as this set.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    @SuppressWarnings({"unchecked", "SimplifiableIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return inner.equals(((LargeHashSet<E>) o).inner);
    }

    /**
     * Returns a string representation of this set.  The string
     * representation consists of a list of the set's elements in no
     * specific order, enclosed in braces (<tt>"{}"</tt>) and each starting
     * with <tt>"  "</tt> (two spaces for indentation).
     * Adjacent elements are separated by the characters <tt>",\n"</tt>
     * (comma and new line). Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this set
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(System.lineSeparator());
        for (Iterator<E> iterator = iterator(); iterator.hasNext(); ) {
            E element = iterator.next();
            sb.append("  ").append(element);
            if (iterator.hasNext()) {
                sb.append(",");
            }
            sb.append(System.lineSeparator());
        }
        return sb.append('}').toString();
    }

    /**
     * LargeHashSetIterator, an inner class wrapping the iterator logic for the set
     *
     * @param <E> The element type
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class LargeHashSetIterator<E> implements Iterator<E> {

        /**
         * Inner map iterator
         */
        private final Iterator<Entry<E, Object>> inner;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws java.util.NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next() {
            return inner.next().getKey();
        }
    }
}
