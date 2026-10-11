package com.aptech.l11b;

/**
 * Lab Task L11B — SimpleMap, buckets and chains.   <<< THIS IS THE SECOND FILE YOU COMPLETE >>>
 *
 * R5 — put() stores or replaces. get() returns the value, or null when the key is absent.
 * R6 — size() counts KEYS, so putting the same key twice must not make it two.
 * R7 — the table keeps its 0.75 load factor: when size > buckets.length * 0.75, rehash() first,
 *      and the bucket count doubles.
 * R8 — chainLengths() and longestChain() describe the spread, which is how a bad hashCode()
 *      becomes visible.
 *
 * The Node class, the bucket array, the constructor and hashOf() are given. Read hashOf() and
 * understand it before you write put() - the three steps of a hash map are all in there.
 *
 * Author: YOUR NAME HERE
 */
public final class SimpleMap {

    /** One key-and-value pair, plus the rest of its chain. GIVEN. */
    private static final class Node {
        final Object key;
        final int hash;
        int value;
        Node next;

        Node(Object key, int hash, int value, Node next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }
    }

    private Node[] buckets;
    private int size;

    /** Eight buckets, because that is small enough to see what is happening. GIVEN. */
    public SimpleMap() {
        buckets = new Node[8];
    }

    /** How many KEYS are stored. GIVEN. */
    public int size() {
        return size;
    }

    /** How many buckets the table has. GIVEN. */
    public int bucketCount() {
        return buckets.length;
    }

    /**
     * R5 — Stores the value under the key, or replaces the value already stored under it.
     *
     * The three steps, in order:
     *   1. hash  = hashOf(key)
     *   2. index = hash &amp; (buckets.length - 1)      // the mask, because the length is a power of two
     *   3. walk buckets[index] comparing  n.hash == hash && n.key.equals(key)
     * A match means this key is already here: change its value and RETURN - do not add a second.
     * No match means a new key: push a new Node onto the FRONT of the chain, then size++, then
     * check the load factor (R7).
     */
    public void put(Object key, int value) {
        // TODO R5 + R7
    }

    /** R5 — The value stored under the key, or null when it is not there. */
    public Integer get(Object key) {
        // TODO R5
        return null;
    }

    /**
     * R8 — How many keys sit in each bucket, in bucket order.
     * With a good hashCode these are all 0s and 1s; with a bad one, a single bucket holds
     * everything. That is the whole point of being able to ask.
     */
    public int[] chainLengths() {
        // TODO R8
        return new int[buckets.length];
    }

    /** R8 — The longest chain, which is the worst case one lookup can meet. */
    public int longestChain() {
        // TODO R8
        return 0;
    }

    /**
     * R7 — A new array twice as long, and every node moved into the bucket it now belongs to.
     * The index changes because the MASK changes, not because the hash does: each node keeps
     * the hash it was stored with, so no key has to be hashed again.
     */
    private void rehash() {
        // TODO R7
    }

    /**
     * The spread: XOR the top bits down into the bottom ones. GIVEN - read it, do not change it.
     * The mask only ever looks at the low bits, so without this, two keys whose hashCodes differ
     * only in the high bits would land in the same bucket for no good reason.
     */
    private static int hashOf(Object key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }
}
