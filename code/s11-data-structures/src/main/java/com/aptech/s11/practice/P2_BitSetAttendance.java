package com.aptech.s11.practice;

import java.util.BitSet;

/**
 * P2 - Bullet 2, the BitSet class.
 *
 * A register of twelve students, two days of attendance. A BitSet answers set questions about
 * them in one line each - which is what it is for.
 */
public class P2_BitSetAttendance {

    public static void main(String[] args) {
        BitSet day1 = new BitSet();
        BitSet day2 = new BitSet();
        for (int id : new int[] {1, 2, 4, 5, 7, 9, 10, 12}) {
            day1.set(id);
        }
        for (int id : new int[] {2, 3, 4, 6, 7, 9, 11}) {
            day2.set(id);
        }

        System.out.println("day1             " + day1);
        System.out.println("day2             " + day2);
        System.out.println();
        System.out.println("present both     " + and(day1, day2));
        System.out.println("present either   " + or(day1, day2));
        System.out.println("day1 only        " + andNot(day1, day2));
        System.out.println("missed both      " + missedBoth(day1, day2));
        System.out.println();
        System.out.println("came on day1     " + day1.cardinality() + " students");
        System.out.println("came on day2     " + day2.cardinality() + " students");
        System.out.println("first on day1    " + day1.nextSetBit(0));
        System.out.println("after 7 on day1  " + day1.nextSetBit(8));
        System.out.println();

        System.out.println("One long holds 64 answers, so the space is round:");
        System.out.println("size()           " + day1.size() + " bits, always a multiple of 64");
        System.out.println("length()         " + day1.length() + " bits up to the highest student");
        System.out.println();
        System.out.println("A boolean[13] would spend 13 separate bytes on the same 12 answers,");
        System.out.println("and would have no and(), or() or cardinality() to ask with.");
    }

    private static BitSet and(BitSet a, BitSet b) {
        BitSet result = (BitSet) a.clone();
        result.and(b);
        return result;
    }

    private static BitSet or(BitSet a, BitSet b) {
        BitSet result = (BitSet) a.clone();
        result.or(b);
        return result;
    }

    private static BitSet andNot(BitSet a, BitSet b) {
        BitSet result = (BitSet) a.clone();
        result.andNot(b);
        return result;
    }

    /** Students 1..12 who are in neither day. */
    private static BitSet missedBoth(BitSet day1, BitSet day2) {
        BitSet all = new BitSet();
        all.set(1, 13);
        all.andNot(or(day1, day2));
        return all;
    }
}
