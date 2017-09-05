package com.github.minaasham.offheap.largecollections;

/**
 * LargeSet, the main interface for all large sets
 *
 * @param <E> The element type
 */
public interface LargeSet<E> extends Iterable<E>, AutoCloseable {

    /**
     * Returns <tt>true</tt> if this set contains the specified element
     *
     * @param element Element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    boolean contains(E element);

    /**
     * Adds the specified element to this set if it is not already present
     *
     * @param element Element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    boolean add(E element);

    /**
     * Removes the specified element from this set if it is present
     *
     * @param element Element to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     */
    boolean remove(E element);

    /**
     * Clear the set from all elements
     */
    void clear();

    /**
     * Gets the current size of the set
     *
     * @return The size of the set
     */
    long size();

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     */
    @Override
    void close();
}
