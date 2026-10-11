package com.aptech.s11;

import java.util.BitSet;

/**
 * E02 — the BitSet class: a set of non-negative ints, packed one bit each.
 *
 * Manual bullet 2 of 4: "Describe the BitSet class".
 *
 * BitSet is the surprise of this session. Every other class here is legacy in the sense of
 * "superseded": there is a modern replacement and you should prefer it. BitSet has no
 * replacement, because nothing else does what it does — it is not deprecated, and for dense
 * sets of small integers it is still the right answer.
 */
public class E02_BitSet {

    public static void main(String[] args) {
        setOperations();
        theSieve();
    }

    private static void setOperations() {
        System.out.println("1. A BitSet is a set of numbers, stored as bits");
        BitSet a = new BitSet();
        a.set(1);
        a.set(3);
        a.set(5);
        BitSet b = new BitSet();
        b.set(3);
        b.set(5);
        b.set(7);

        System.out.println("   a                  = " + a);
        System.out.println("   b                  = " + b);
        System.out.println("   a.cardinality()    = " + a.cardinality() + "   (how many bits are set)");

        BitSet both = (BitSet) a.clone();
        both.and(b);
        BitSet either = (BitSet) a.clone();
        either.or(b);
        BitSet exactlyOne = (BitSet) a.clone();
        exactlyOne.xor(b);
        BitSet onlyA = (BitSet) a.clone();
        onlyA.andNot(b);

        System.out.println("   a AND b            = " + both + "   (in both)");
        System.out.println("   a OR b             = " + either + "   (in either)");
        System.out.println("   a XOR b            = " + exactlyOne + "   (in exactly one)");
        System.out.println("   a ANDNOT b         = " + onlyA + "   (in a, but not in b)");

        System.out.println();
        System.out.println("2. Looking things up without walking the set");
        System.out.println("   a.get(3)           = " + a.get(3));
        System.out.println("   a.get(4)           = " + a.get(4));
        System.out.println("   a.nextSetBit(0)    = " + a.nextSetBit(0));
        System.out.println("   a.nextSetBit(2)    = " + a.nextSetBit(2));
        System.out.println("   a.nextSetBit(4)    = " + a.nextSetBit(4));
        System.out.println("   a.nextSetBit(6)    = " + a.nextSetBit(6) + "   (-1 means: no more set bits)");

        a.flip(1);
        System.out.println("   after a.flip(1)    = " + a + "   (flip toggles one bit)");
        System.out.println("   a.length()         = " + a.length() + "   (highest set bit + 1)");
        System.out.println("   a.size()           = " + a.size() + "   (room reserved; always a multiple of 64)");
    }

    private static void theSieve() {
        int limit = 60;

        // composite[i] is true when i is known NOT to be prime.
        BitSet composite = new BitSet(limit + 1);
        for (int p = 2; p * p <= limit; p++) {
            if (!composite.get(p)) {
                for (int multiple = p * p; multiple <= limit; multiple += p) {
                    composite.set(multiple);
                }
            }
        }

        // Start with every number 2..limit, then strike out the composites.
        BitSet primes = new BitSet(limit + 1);
        primes.set(2, limit + 1);
        primes.andNot(composite);

        System.out.println();
        System.out.println("3. A real use: the sieve of Eratosthenes up to " + limit);
        System.out.println("   primes        = " + primes);
        System.out.println("   cardinality() = " + primes.cardinality() + " primes found");
        System.out.println("   length()      = " + primes.length() + " bits for the numbers 0 to " + limit);
        System.out.println();
        System.out.println("   No boolean[" + (limit + 1) + "], no HashSet<Integer>, no boxing: one bit per");
        System.out.println("   number, and the whole sieve fits in " + primes.size() / 64 + " long.");
    }
}
