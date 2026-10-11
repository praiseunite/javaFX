package com.aptech.s11b.practice;

import com.aptech.s11b.ds.SimpleMap;

import java.util.Locale;

/**
 * P4 - Medium. The load factor, traced key by key.
 *
 * The task: store the numbers 1 to 100 in a map and print every rehash - the size that triggered
 * it and the bucket count it went to. Then answer two questions with numbers: how many rehashes
 * does the hundredth key cost, and how much total work did all of them add up to?
 *
 * The rule inside SimpleMap is: rehash when size &gt; buckets x 0.75. Read the source and check
 * the output against it; if the two ever disagree, the source is what you believe.
 */
public class P4_RehashTrace {

    private static final int KEYS = 100;

    public static void main(String[] args) {
        System.out.println("Storing the numbers 1 to " + KEYS + ", and writing down every rehash.");
        System.out.println();
        System.out.printf(Locale.ROOT, "   %-8s %-9s %s%n", "keys", "buckets", "what happened");

        SimpleMap map = new SimpleMap();
        int previous = map.bucketCount();
        System.out.printf(Locale.ROOT, "   %-8s %-9d %s%n", "none yet", previous, "the starting table");

        long moved = 0;
        for (int i = 1; i <= KEYS; i++) {
            map.put(i, i);
            if (map.bucketCount() != previous) {
                moved += i;
                System.out.printf(Locale.ROOT, "   %-8d %-9d rehash %d - every key so far moved%n",
                        i, map.bucketCount(), map.rehashes());
                previous = map.bucketCount();
            }
        }

        System.out.println();
        System.out.println("   " + KEYS + " keys, " + map.bucketCount() + " buckets, "
                + map.rehashes() + " rehashes, longest chain " + map.longestChain() + ".");
        System.out.println();
        System.out.println("Read the trigger column against the rule: 8 x 0.75 = 6, so the seventh key");
        System.out.println("finds a table that is three-quarters full and doubles it. Then 16 x 0.75 =");
        System.out.println("12, so the thirteenth key doubles it again. The numbers 7, 13, 25, 49 and");
        System.out.println("97 are not arbitrary: each one is double the one before it, minus one,");
        System.out.println("because the table it triggers is double the one before it too.");
        System.out.println();
        System.out.printf(Locale.ROOT,
                "Total keys moved by all the rehashes: %d, for %d keys stored.", moved, KEYS);
        System.out.println();
        System.out.println("That is the same bargain E01 measured in a growing array, made again: the");
        System.out.println("expensive operation happens rarely enough that the average stays small.");
        System.out.println("Below 2 moves per key, every time, whatever n is - and that is what");
        System.out.println("\"amortised O(1)\" has meant both times.");
        System.out.println();
        System.out.println("One habit to take away: a load factor of 0.75 is a compromise, not a law.");
        System.out.println("Higher and the table stays small but the chains grow; lower and lookups");
        System.out.println("stay cheap but you pay for the rehashing and waste memory. The JDK's");
        System.out.println("HashMap picked 0.75 too, for the same reason, and documented it.");
    }
}
