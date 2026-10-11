package com.aptech.s11b.practice;

import com.aptech.s11b.ds.SimpleMap;

import java.util.Locale;

/**
 * P2 - Easy. Bucket arithmetic on paper.
 *
 * The task: a new SimpleMap has eight buckets. Before you run this, work out on paper which
 * bucket each of these keys lands in, and how many comparisons get(20) will need:
 *
 *     20, 21, 22, 23, 28, 36
 *
 * The arithmetic is index = key % 8, because for a small positive int the "spread" changes
 * nothing. If you can do that sum in your head, you understand a hash map well enough to debug
 * one - and that is the whole point of the session.
 */
public class P2_BucketArithmetic {

    public static void main(String[] args) {
        System.out.println("The working, on paper, before any code runs:");
        System.out.println();
        System.out.println("   20 % 8 = 4      21 % 8 = 5      22 % 8 = 6");
        System.out.println("   23 % 8 = 7      28 % 8 = 4      36 % 8 = 4");
        System.out.println();
        System.out.println("So four buckets are used, and bucket 4 takes three of the six keys.");
        System.out.println();

        SimpleMap map = new SimpleMap();
        int[] keys = {20, 21, 22, 23, 28, 36};
        for (int key : keys) {
            map.put(key, key * 10);
        }

        System.out.println("Now the same thing from the code:");
        System.out.println();
        System.out.printf(Locale.ROOT, "   %-8s %-18s %s%n", "bucket", "chain", "keys to compare");
        int[] lengths = map.chainLengths();
        for (int bucket = 0; bucket < map.bucketCount(); bucket++) {
            String chain = lengths[bucket] == 0 ? "(empty)" : map.chainIn(bucket);
            System.out.printf(Locale.ROOT, "   %-8d %-18s %d%n", bucket, chain, lengths[bucket]);
        }
        System.out.println();

        System.out.println("And what a lookup costs:");
        for (int key : keys) {
            long probes = probesFor(map, key);
            System.out.printf(Locale.ROOT, "   get(%-3d) -> %d comparison%s%n",
                    key, probes, probes == 1 ? "" : "s");
        }
        System.out.println();

        System.out.println("Three of those keys differ by 8, so they share a bucket - and 20, the");
        System.out.println("one added first, is now at the BACK of the chain, because every new key");
        System.out.println("is pushed onto the front. That is why get(36) is cheap and get(20) is not,");
        System.out.println("and why \"the map is O(1)\" is an average, never a promise.");
        System.out.println();
        System.out.println("The habit worth keeping: when a lookup costs more than you expected, look");
        System.out.println("at what the key's hashCode() returns in binary. The mask only reads the");
        System.out.println("low bits, so two keys whose hashes agree down there end up in the same");
        System.out.println("bucket however different they look on paper. The class is innocent.");
        System.out.println();
        System.out.println("One more, on paper: the map above has 8 buckets and 6 keys. Add a seventh");
        System.out.println("key - any key - and the table doubles to 16 before your key is even stored.");
        System.out.println("That is P4's job, and the rule is 8 x 0.75 = 6.");
    }

    private static long probesFor(SimpleMap map, int key) {
        long before = map.probes();
        map.get(key);
        return map.probes() - before;
    }
}
