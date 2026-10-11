package com.aptech.s11b.ds;

/**
 * A hash map, built by hand: an array of buckets, each bucket a short chain of nodes.
 *
 * The whole idea in three steps:
 *
 *   1. turn the key into a number          - key.hashCode()
 *   2. turn that number into a bucket      - hash & (buckets.length - 1)
 *   3. walk that bucket's chain comparing   - node.key.equals(key)
 *
 * Step 2 works with &amp; instead of % only because the bucket count is always a power of two, and
 * that is why the JDK's HashMap grows 16, 32, 64 ... rather than 10, 20, 30. The constructor
 * rounds up so the masking is always legal.
 *
 * Two counters make the cost visible without a clock: {@code probes} counts the key comparisons
 * every lookup performs, and {@code rehashes} counts how many times the whole table was rebuilt.
 */
public class SimpleMap {

    private static final double LOAD_FACTOR = 0.75;

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
    private int rehashes;
    private long probes;

    public SimpleMap() {
        this(8);
    }

    public SimpleMap(int wantedBuckets) {
        buckets = new Node[powerOfTwoAtLeast(wantedBuckets)];
    }

    /** How many keys are stored. */
    public int size() {
        return size;
    }

    public int bucketCount() {
        return buckets.length;
    }

    /** Key comparisons performed by every put/get so far. Compare this between two hashes. */
    public long probes() {
        return probes;
    }

    /** How many times the table outgrew itself and was rebuilt. */
    public int rehashes() {
        return rehashes;
    }

    public void put(Object key, int value) {
        int hash = hashOf(key);
        int index = hash & (buckets.length - 1);
        for (Node n = buckets[index]; n != null; n = n.next) {
            probes++;
            if (n.hash == hash && n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        buckets[index] = new Node(key, hash, value, buckets[index]);
        size++;
        if (size > buckets.length * LOAD_FACTOR) {
            rehash();
        }
    }

    /** The value, or null when the key is not in the map. */
    public Integer get(Object key) {
        int hash = hashOf(key);
        int index = hash & (buckets.length - 1);
        for (Node n = buckets[index]; n != null; n = n.next) {
            probes++;
            if (n.hash == hash && n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    /**
     * get() alone cannot tell "stored with the value null" from "not stored at all". This can,
     * and it is why the method exists.
     */
    public boolean containsKey(Object key) {
        int hash = hashOf(key);
        int index = hash & (buckets.length - 1);
        for (Node n = buckets[index]; n != null; n = n.next) {
            probes++;
            if (n.hash == hash && n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * How many keys sit in each bucket, in bucket order. Printing this is how the spread becomes
     * visible: with a good hash the numbers are all 0s and 1s, and with a bad one a single bucket
     * takes everything.
     */
    public int[] chainLengths() {
        int[] lengths = new int[buckets.length];
        for (int i = 0; i < buckets.length; i++) {
            int count = 0;
            for (Node n = buckets[i]; n != null; n = n.next) {
                count++;
            }
            lengths[i] = count;
        }
        return lengths;
    }

    /** The longest chain - the worst case a single lookup can meet. */
    public int longestChain() {
        int longest = 0;
        for (int length : chainLengths()) {
            if (length > longest) {
                longest = length;
            }
        }
        return longest;
    }

    /**
     * The keys of one bucket's chain, in order, for printing. Only ever used on a bucket whose
     * chain is short enough to read - never on a whole hash-backed collection, whose print order
     * is not defined.
     */
    public String chainIn(int bucket) {
        StringBuilder text = new StringBuilder();
        for (Node n = buckets[bucket]; n != null; n = n.next) {
            if (text.length() > 0) {
                text.append(" -> ");
            }
            text.append(n.key);
        }
        return text.toString();
    }

    /**
     * Doubles the table and moves every node into its new bucket. Note that the node keeps its
     * stored hash: the spread does not depend on the table size, only the mask does, so no key
     * has to be re-hashed from its object.
     */
    private void rehash() {
        Node[] old = buckets;
        buckets = new Node[old.length * 2];
        rehashes++;
        for (Node head : old) {
            for (Node n = head; n != null; ) {
                Node next = n.next;
                int index = n.hash & (buckets.length - 1);
                n.next = buckets[index];
                buckets[index] = n;
                n = next;
            }
        }
    }

    /**
     * The same defence the JDK uses: XOR the top bits down into the bottom ones, because the
     * mask only ever looks at the low bits. Without this, keys whose hashCodes differ only in
     * the high bits would land in the same bucket for no good reason.
     */
    private static int hashOf(Object key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private static int powerOfTwoAtLeast(int wanted) {
        int capacity = 1;
        while (capacity < wanted) {
            capacity <<= 1;
        }
        return capacity;
    }
}
