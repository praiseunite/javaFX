package com.aptech.s11b;

import com.aptech.s11b.ds.SimpleMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * E04 — breaking the map on purpose, then fixing it.
 *
 * Session 10 promised that one day you would watch equals() and hashCode() break in your own
 * hands. This is that program. It breaks the map in the two ways it can be broken, and both are
 * quiet: no exception, no warning, just a map that is slower than it should be, or a map that
 * holds the same key twice.
 *
 * The final demonstration runs the same broken class through the JDK's HashMap, which fails in
 * exactly the same way - proof that this is the contract, not our implementation.
 */
public class E04_BreakingHashCode {

    /** A correct value class: equals() and hashCode() look at the same field. */
    static class Person {
        private final String name;

        Person(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Person p && name.equals(p.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /** The same person, except hashCode() answers 1 for everybody. equals() is unchanged. */
    static final class CollidingPerson extends Person {
        CollidingPerson(String name) {
            super(name);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    /** equals() compares the id. hashCode() uses a field that equals() never looks at. */
    static final class Registration {
        private final String id;
        private final String nickname;

        Registration(String id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Registration r && id.equals(r.id);
        }

        @Override
        public int hashCode() {
            return nickname.hashCode();     // WRONG: nickname is not part of equals()
        }

        @Override
        public String toString() {
            return id + " (" + nickname + ")";
        }
    }

    /** The repair: hash the field that equals() compares, and let Objects.hash write it. */
    static final class FixedRegistration {
        private final String id;
        private final String nickname;

        FixedRegistration(String id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof FixedRegistration r && id.equals(r.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return id + " (" + nickname + ")";
        }
    }

    public static void main(String[] args) {
        String[] names = {"Ada", "Ben", "Chidi", "Dara", "Eze", "Femi"};

        System.out.println("1. A hashCode that answers 1 for everybody");
        SimpleMap good = new SimpleMap();
        SimpleMap bad = new SimpleMap();
        for (String name : names) {
            good.put(new Person(name), name.length());
            bad.put(new CollidingPerson(name), name.length());
        }
        System.out.println("   With Objects.hash(name)");
        System.out.println("      buckets used   " + bucketsUsed(good) + " of " + good.bucketCount());
        System.out.println("      longest chain  " + good.longestChain());
        System.out.println("   With hashCode() returning 1");
        System.out.println("      buckets used   " + bucketsUsed(bad) + " of " + bad.bucketCount());
        System.out.println("      longest chain  " + bad.longestChain());
        System.out.println("      bucket 1       " + bad.chainIn(1));
        System.out.println();

        System.out.println("2. The same six lookups against each");
        long goodProbes = probeAll(good, names, false);
        long badProbes = probeAll(bad, names, true);
        System.out.printf(Locale.ROOT, "   Objects.hash(name)   %d comparisons%n", goodProbes);
        System.out.printf(Locale.ROOT, "   hashCode() = 1       %d comparisons%n", badProbes);
        System.out.println();
        System.out.println("   Six lookups, twenty-one comparisons for the broken one - 1 + 2 + 3 +");
        System.out.println("   4 + 5 + 6, because every key is in one chain and the last key added");
        System.out.println("   is found first while the first key added is found last. Nothing threw.");
        System.out.println("   The map is still correct. It has simply stopped being a map.");
        System.out.println();

        System.out.println("3. The other way to break it: equals() and hashCode() disagree");
        SimpleMap registry = new SimpleMap();
        registry.put(new Registration("S1", "Ada"), 10);
        registry.put(new Registration("S1", "Ade"), 20);
        System.out.println("   Two puts, both with id S1, differing only in a field equals() ignores:");
        System.out.println("      size()        -> " + registry.size() + "   (one logical key, stored twice)");
        System.out.println("      get(Ada)      -> " + registry.get(new Registration("S1", "Ada")));
        System.out.println("      get(Ade)      -> " + registry.get(new Registration("S1", "Ade")));
        System.out.println("   equals() says these are the same key; hashCode() sent them to different");
        System.out.println("   buckets, so the map never got the chance to notice.");
        System.out.println();

        System.out.println("4. The repair: hash what equals() compares");
        SimpleMap fixed = new SimpleMap();
        fixed.put(new FixedRegistration("S1", "Ada"), 10);
        fixed.put(new FixedRegistration("S1", "Ade"), 20);
        System.out.println("      size()        -> " + fixed.size() + "   (the second put updated, not added)");
        System.out.println("      get(Ada)      -> " + fixed.get(new FixedRegistration("S1", "Ada")));
        System.out.println("      get(Ade)      -> " + fixed.get(new FixedRegistration("S1", "Ade")));
        System.out.println("   Both lookups find the same entry, which is what \"same key\" has to mean.");
        System.out.println();

        System.out.println("5. This is the contract, not our implementation");
        Map<Registration, Integer> jdk = new HashMap<>();
        jdk.put(new Registration("S1", "Ada"), 10);
        jdk.put(new Registration("S1", "Ade"), 20);
        System.out.println("   The JDK's HashMap, same broken class -> size() = " + jdk.size());
        System.out.println("   The JDK cannot repair a class whose two halves disagree either. It");
        System.out.println("   does not even fail loudly - it just holds the key twice, as we did.");
        System.out.println();
        System.out.println("   The rule, in one line: hashCode() must be computed from exactly the");
        System.out.println("   fields equals() compares, and nothing else. Objects.hash(...) writes");
        System.out.println("   it for you, so the two lists cannot drift apart later.");
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

    /**
     * Looks every name up once and returns the total number of key comparisons. The colliding
     * map needs CollidingPerson keys, because a lookup only finds an entry whose hash matches.
     */
    private static long probeAll(SimpleMap map, String[] names, boolean colliding) {
        long before = map.probes();
        for (String name : names) {
            map.get(colliding ? new CollidingPerson(name) : new Person(name));
        }
        return map.probes() - before;
    }
}
