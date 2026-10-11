package com.aptech.s11b;

import com.aptech.s11b.ds.SimpleMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * E03 — buckets and chaining, with nothing hidden.
 *
 * Six keys, eight buckets, and the whole mechanism in the open: hash the key, mask it down to a
 * bucket number, then walk that bucket's chain comparing keys. The histogram below is the point
 * of the program. It is the picture on the page, produced by code rather than drawn.
 *
 * The keys are chosen so the arithmetic can be checked by eye: 40 % 8 = 0, 25 % 8 = 1,
 * 33 % 8 = 1, 19 % 8 = 3, 12 % 8 = 4, 4 % 8 = 4.
 */
public class E03_HashMapBuckets {

    public static void main(String[] args) {
        System.out.println("Six keys, eight buckets. Every number below is arithmetic you can redo");
        System.out.println("on paper, which is the difference between a hash map and magic.");
        System.out.println();

        SimpleMap map = new SimpleMap();
        int[] keys = {40, 25, 33, 19, 12, 4};
        for (int key : keys) {
            map.put(key, key * 10);
        }

        System.out.println("1. Where each key landed");
        System.out.println("   bucket   chain            keys to compare");
        int[] lengths = map.chainLengths();
        for (int bucket = 0; bucket < map.bucketCount(); bucket++) {
            String chain = lengths[bucket] == 0 ? "(empty)" : map.chainIn(bucket);
            System.out.printf(Locale.ROOT, "   %-8d %-16s %d%n", bucket, chain, lengths[bucket]);
        }
        System.out.println();
        System.out.println("   " + map.size() + " keys, " + map.bucketCount() + " buckets, longest chain "
                + map.longestChain() + ".");
        System.out.println("   Bucket 1 holds two keys because 25 and 33 both leave remainder 1.");
        System.out.println("   That is a collision, and a chain is how it is handled.");
        System.out.println();

        System.out.println("2. What a lookup costs, counted in key comparisons");
        for (int key : keys) {
            long probes = probesFor(map, key);
            System.out.printf(Locale.ROOT, "   get(%-4d)  %d comparison%s%n",
                    key, probes, probes == 1 ? "" : "s");
        }
        System.out.printf(Locale.ROOT, "   get(%-4d)  %d comparisons - a bucket with nothing in it%n",
                999, probesFor(map, 999));
        System.out.println();
        System.out.println("   Even the worst key here costs two comparisons, not six. Add a");
        System.out.println("   hundred keys and the chains stay short, so the cost stays flat:");
        System.out.println("   that is the O(1), and it comes from the SPREAD, not from luck.");
        System.out.println();

        System.out.println("3. A deliberately bad spread");
        SimpleMap clustered = new SimpleMap();
        for (int i = 0; i < 6; i++) {
            clustered.put(i * 8, i);     // every one of these has remainder 0
        }
        System.out.println("   keys 0, 8, 16, 24, 32, 40 -> one bucket, chain of "
                + clustered.longestChain());
        System.out.printf(Locale.ROOT, "   get(40) -> %d comparison   (40 was added last, so it is at the front)%n",
                probesFor(clustered, 40));
        System.out.printf(Locale.ROOT, "   get(0)  -> %d comparisons  (0 was added first, so it is at the back)%n",
                probesFor(clustered, 0));
        System.out.println("   Same class, same code, six times the work - because the keys chose");
        System.out.println("   the bucket, and these keys all chose the same one.");
        System.out.println();

        System.out.println("4. The load factor: when the table outgrows itself");
        System.out.println("   keys stored      buckets");
        SimpleMap growing = new SimpleMap();
        int previous = growing.bucketCount();
        System.out.printf(Locale.ROOT, "   %-16s %d%n", "0", previous);
        for (int i = 1; i <= 30; i++) {
            growing.put(i, i);
            if (growing.bucketCount() != previous) {
                System.out.printf(Locale.ROOT, "   %-16s %d   <- rehash %d: every key moved%n",
                        i, growing.bucketCount(), growing.rehashes());
                previous = growing.bucketCount();
            }
        }
        System.out.println();
        System.out.println("   The table doubles when it is three-quarters full, because a map");
        System.out.println("   that is mostly chains has stopped being a map. Rehashing is the");
        System.out.println("   hash map's version of E01's growth: rare, and expensive when it comes.");
        System.out.println();

        System.out.println("5. The JDK's HashMap does the same thing, with more of it");
        Map<Integer, Integer> jdk = new HashMap<>();
        for (int key : keys) {
            jdk.put(key, key * 10);
        }
        System.out.println("   HashMap size()    -> " + jdk.size());
        System.out.println("   HashMap get(33)   -> " + jdk.get(33));
        System.out.println("   HashMap get(999)  -> " + jdk.get(999) + "   (null, and it does not throw)");
        System.out.println("   HashMap's own javadoc says the rest out loud: using many keys with");
        System.out.println("   the same hashCode() is a sure way to slow down any hash table, and");
        System.out.println("   when the keys are Comparable this class may use their order to break");
        System.out.println("   the tie. That tie-breaking is section 3's problem, and Java 8 added");
        System.out.println("   the fix. Everything else is what you have just written.");
    }

    private static long probesFor(SimpleMap map, int key) {
        long before = map.probes();
        map.get(key);
        return map.probes() - before;
    }
}
