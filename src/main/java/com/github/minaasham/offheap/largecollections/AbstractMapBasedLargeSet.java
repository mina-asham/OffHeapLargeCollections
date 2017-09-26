package com.github.minaasham.offheap.largecollections;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map.Entry;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractMapBasedLargeSet<E> implements LargeSet<E> {
    /**
     * The inner map used for this set
     */
    private final LargeMap<E, Object> inner;

    /**
     * Returns <tt>true</tt> if this set contains the specified element
     *
     * @param element Element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    @Override
    public final boolean contains(E element) {
        return inner.get(element) != null;
    }

    /**
     * Adds the specified element to this set if it is not already present
     *
     * @param element Element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    @Override
    public final boolean add(E element) {
        return inner.put(element, ZeroBytesFixedSerializer.DUMMY) == null;
    }

    /**
     * Removes the specified element from this set if it is present
     *
     * @param element Element to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     */
    @Override
    public final boolean remove(E element) {
        return inner.remove(element) != null;
    }

    /**
     * Clear the set from all elements
     */
    @Override
    public final void clear() {
        inner.clear();
    }

    /**
     * Gets the current size of the set
     *
     * @return The size of the set
     */
    @Override
    public final long size() {
        return inner.size();
    }

    /**
     * Returns an iterator over elements of type {@code E}
     *
     * @return The set's iterator
     */
    @Override
    public final Iterator<E> iterator() {
        return new LargeSetIterator<>(inner.iterator());
    }

    /**
     * Disposes of the off heap allocations
     */
    @Override
    public final void close() {
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
    public final int hashCode() {
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
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return inner.equals(((AbstractMapBasedLargeSet<E>) o).inner);
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
    public final String toString() {
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
     * LargeSetIterator, an inner class wrapping the iterator logic for the set
     *
     * @param <E> The element type
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class LargeSetIterator<E> implements Iterator<E> {

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
