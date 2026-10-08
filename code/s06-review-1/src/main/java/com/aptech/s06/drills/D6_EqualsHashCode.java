package com.aptech.s06.drills;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * DRILL 6 — the duplicate that will not go away.
 *
 * The bug: two Student objects with the same student id, added to a HashSet, are kept as
 * two entries. contains() says "no" for a student that is plainly already in there.
 *
 * Why: without equals() a class inherits identity comparison from Object — "same object
 * in memory?", not "same student?". Without hashCode() the two objects usually land in
 * different buckets, so equals() is not even consulted first.
 *
 * The fix: honour the contract. equals() decides what "the same" means, hashCode() must
 * agree with it, and the pair must be consistent. Note which field this course treats as
 * identity — the id, not the name — because that is a business decision, not a Java one.
 *
 * The contract, in one line: if a.equals(b) is true, then a.hashCode() == b.hashCode()
 * must also be true. Break it and every hash-based collection misbehaves quietly.
 */
public class D6_EqualsHashCode {

    public static void main(String[] args) {
        System.out.println("Drill 6 - the duplicate that will not go away");
        System.out.println();

        // --- the broken class ---
        Set<BadStudent> bad = new HashSet<>();
        bad.add(new BadStudent("S-1001", "Ada"));
        bad.add(new BadStudent("S-1001", "Ada"));

        System.out.println("BROKEN - Student with no equals() and no hashCode()");
        System.out.println("  two copies of the same student were added");
        System.out.println("  set size = " + bad.size());
        System.out.println("  contains(a fresh copy of the same student) -> "
                + bad.contains(new BadStudent("S-1001", "Ada")));
        System.out.println("  -> identity was compared, not student id.");

        System.out.println();

        // --- the fixed class ---
        Set<GoodStudent> good = new HashSet<>();
        good.add(new GoodStudent("S-1001", "Ada"));
        good.add(new GoodStudent("S-1001", "Ada"));

        System.out.println("FIXED - equals() and hashCode() follow the contract");
        System.out.println("  two copies of the same student were added");
        System.out.println("  set size = " + good.size());
        System.out.println("  contains(a fresh copy of the same student) -> "
                + good.contains(new GoodStudent("S-1001", "Ada")));
        System.out.println("  -> the duplicate was rejected, which is what a Set promises.");

        System.out.println();

        // --- and the same fault ruins a Map key ---
        Map<BadStudent, String> badGrades = new java.util.HashMap<>();
        BadStudent key = new BadStudent("S-1001", "Ada");
        badGrades.put(key, "A");
        System.out.println("The same fault as a HashMap key - looks it up with an equal key:");
        System.out.println("  get() -> " + badGrades.get(new BadStudent("S-1001", "Ada"))
                + "   (null: the map cannot find what it stored)");
    }

    /** A student with NO equals()/hashCode(): two objects, two identities. */
    static final class BadStudent {
        final String id;
        final String name;

        BadStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() {
            return id + " " + name;
        }
    }

    /** The same student, this time with the contract honoured. */
    static final class GoodStudent {
        final String id;
        final String name;

        GoodStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GoodStudent that)) {
                return false;
            }
            return id.equals(that.id);
        }

        /** Must agree with equals(): the same field, in the same way. */
        @Override public int hashCode() {
            return Objects.hash(id);
        }

        @Override public String toString() {
            return id + " " + name;
        }
    }
}
