package com.aptech.s11b.practice;

import com.aptech.s11b.ds.SimpleMap;

import java.util.Locale;
import java.util.Objects;

/**
 * P3 - Medium. A hash that is bad in a subtler way than returning 1.
 *
 * The task: a colleague writes {@code hashCode()} as {@code id % 8}, on the reasonable-sounding
 * grounds that "it always fits the table". Store twelve students whose ids are all multiples of
 * eight, count the comparisons, then fix the hash and count again.
 *
 * This is the more useful bug of the two. {@code return 1;} is obvious the moment you read it.
 * {@code id % 8} looks like arithmetic that is helping, and it is worse than useless: it throws
 * away every bit above the third, and it stays broken after the table doubles to 16 buckets.
 */
public class P3_WhereDidMyKeysGo {

    /** The colleague's version. equals() is correct; only the hash is wrong. */
    static final class CappedHashStudent {
        private final int id;
        private final String name;

        CappedHashStudent(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CappedHashStudent s && id == s.id;
        }

        @Override
        public int hashCode() {
            return id % 8;          // WRONG: everything above the low three bits is discarded
        }

        @Override
        public String toString() {
            return name + "#" + id;
        }
    }

    /** The repair. Nothing else changes. */
    static final class Student {
        private final int id;
        private final String name;

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Student s && id == s.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return name + "#" + id;
        }
    }

    private static final int[] IDS = {8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96};
    private static final String[] NAMES = {
        "Ada", "Ben", "Chidi", "Dara", "Eze", "Femi",
        "Grace", "Hauwa", "Ifeoma", "Jide", "Kemi", "Ladi",
    };

    public static void main(String[] args) {
        System.out.println("Twelve students, ids 8, 16, 24 ... 96. Every id is a multiple of 8.");
        System.out.println();

        System.out.println("1. With hashCode() written as id % 8");
        SimpleMap capped = new SimpleMap();
        for (int i = 0; i < IDS.length; i++) {
            capped.put(new CappedHashStudent(IDS[i], NAMES[i]), IDS[i]);
        }
        System.out.println("   buckets          " + capped.bucketCount()
                + "   (it doubled from 8 when the seventh key arrived)");
        System.out.println("   buckets used     " + bucketsUsed(capped) + " of " + capped.bucketCount());
        System.out.println("   longest chain    " + capped.longestChain());
        long cappedProbes = probeAll(capped, true);
        System.out.printf(Locale.ROOT, "   twelve lookups   %d comparisons%n", cappedProbes);
        long firstProbes = probesFor(capped, new CappedHashStudent(8, "Ada"));
        long lastProbes = probesFor(capped, new CappedHashStudent(96, "Ladi"));
        System.out.printf(Locale.ROOT, "      get(8)   added first, but the rehash reversed the chain -> %d comparison%s%n",
                firstProbes, firstProbes == 1 ? "" : "s");
        System.out.printf(Locale.ROOT, "      get(96)  added last, so it is at the front              -> %d comparison%s%n",
                lastProbes, lastProbes == 1 ? "" : "s");
        System.out.println("      Twelve keys, one chain of twelve - and get(8) costs six, not twelve,");
        System.out.println("      because a rehash walks the old chain and pushes each node onto the");
        System.out.println("      new one, reversing it. Never depend on where in a chain a key sits.");
        System.out.println();

        System.out.println("2. The same twelve with hashCode() written as Objects.hash(id)");
        SimpleMap fixed = new SimpleMap();
        for (int i = 0; i < IDS.length; i++) {
            fixed.put(new Student(IDS[i], NAMES[i]), IDS[i]);
        }
        System.out.println("   buckets          " + fixed.bucketCount());
        System.out.println("   buckets used     " + bucketsUsed(fixed) + " of " + fixed.bucketCount());
        System.out.println("   longest chain    " + fixed.longestChain());
        long fixedProbes = probeAll(fixed, false);
        System.out.printf(Locale.ROOT, "   twelve lookups   %d comparisons%n", fixedProbes);
        System.out.println();

        System.out.printf(Locale.ROOT, "3. The one-line change cost %d comparisons and changed the shape of%n",
                cappedProbes - fixedProbes);
        System.out.println("   the table. id % 8 can only ever produce eight different answers, so of a");
        System.out.println("   sixteen-bucket table at most eight buckets are reachable - the other eight");
        System.out.println("   are decoration whatever keys arrive. These twelve ids are all multiples of");
        System.out.println("   eight, so every one of them produced the same answer, 0: the chain grew to");
        System.out.println("   twelve while fifteen buckets stood empty.");
        System.out.println();
        System.out.println("4. The lesson, stated plainly");
        System.out.println("   equals() was correct the whole time. Nothing about the class was \"mostly");
        System.out.println("   right\" - and that is what makes this bug dangerous. Every one of those");
        System.out.println("   twelve lookups returns the correct value, so any test that checks answers");
        System.out.println("   passes. A hash that collides is not a smaller hash, it is a linear search");
        System.out.println("   wearing a map's clothes, and only a count can tell you.");
    }

    private static int bucketsUsed(SimpleMap map) {
        int used = 0;
        for (int length : map.chainLengths()) {
            if (length > 0) {
                used++;
            }
        }
        return used;
    }

    private static long probeAll(SimpleMap map, boolean capped) {
        long before = map.probes();
        for (int i = 0; i < IDS.length; i++) {
            map.get(capped ? new CappedHashStudent(IDS[i], NAMES[i]) : new Student(IDS[i], NAMES[i]));
        }
        return map.probes() - before;
    }

    private static long probesFor(SimpleMap map, Object key) {
        long before = map.probes();
        map.get(key);
        return map.probes() - before;
    }
}
