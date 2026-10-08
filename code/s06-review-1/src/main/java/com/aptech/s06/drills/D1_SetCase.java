package com.aptech.s06.drills;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * DRILL 1 — the forgetful collection.
 *
 * The bug: we add "Alice", "Bob" and "alice" to a Set and end up with THREE names,
 * when the office only has two customers.
 *
 * Why: a HashSet answers "have I seen this already?" with hashCode() and equals().
 * For String those compare the characters one by one, so "Alice" and "alice" are not
 * equal and do not land in the same bucket. Case is part of the value, always.
 *
 * The fix: decide a normal form and apply it on the way in. Here everything is
 * lower-cased before it is stored — and note the last line: a lookup has to be
 * normalised in exactly the same way, or contains() quietly starts lying to you.
 */
public class D1_SetCase {

    public static void main(String[] args) {
        System.out.println("Drill 1 - the forgetful collection");
        System.out.println();

        broken();
        System.out.println();
        fixed();
    }

    /** Exactly what the office wrote. */
    private static void broken() {
        Set<String> names = new HashSet<>();
        names.add("Alice");
        names.add("Bob");
        names.add("alice");

        System.out.println("BROKEN - added Alice, Bob, alice with no normalising");
        System.out.println("  unique names = " + names.size());
        System.out.println("  the set holds " + new TreeSet<>(names));
        System.out.println("  ...but Alice and alice are the same customer.");
    }

    /** The same three names, normalised first. */
    private static void fixed() {
        Set<String> names = new HashSet<>();
        names.add("Alice".toLowerCase());
        names.add("Bob".toLowerCase());
        names.add("alice".toLowerCase());

        System.out.println("FIXED - normalise every name on the way in");
        System.out.println("  unique names = " + names.size());
        System.out.println("  the set holds " + new TreeSet<>(names));
        System.out.println("  worth knowing: the LOOKUP must be normalised too");
        System.out.println("  names.contains(\"ALICE\") -> " + names.contains("ALICE"));
        System.out.println("  names.contains(\"ALICE\".toLowerCase()) -> " + names.contains("ALICE".toLowerCase()));
        System.out.println("  (the first lies: the set stores 'alice'. Normalise the lookup the same way.)");
    }
}
