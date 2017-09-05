package com.github.minaasham.offheap.largecollections;

import java.util.Map.Entry;

/**
 * LargeMap, the main interface for all large maps
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public interface LargeMap<K, V> extends Iterable<Entry<K, V>>, AutoCloseable {

    /**
     * Gets key's value from the map
     *
     * @param key The key to lookup
     * @return The value associated with the key
     */
    V get(K key);

    /**
     * Puts the key and value in the map
     *
     * @param key   The key to insert in the map
     * @param value The value to insert in the map
     * @return The old value related to that key
     */
    V put(K key, V value);

    /**
     * Removes the key from the map if it exists
     *
     * @param key The key to remove from the map
     * @return The value of the key
     */
    V remove(K key);

    /**
     * Clear the map from all keys and values
     */
    void clear();

    /**
     * Gets the current size of the map
     *
     * @return The size of the map
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
