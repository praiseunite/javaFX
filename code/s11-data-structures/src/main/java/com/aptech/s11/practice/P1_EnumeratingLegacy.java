package com.aptech.s11.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * P1 - Bullet 1, the Enumeration interface.
 *
 * You are handed a legacy source that offers nothing but an Enumeration. Get data out of it
 * four ways, and find the one thing an Enumeration cannot do.
 */
public class P1_EnumeratingLegacy {

    public static void main(String[] args) {
        Vector<String> cohort = new Vector<>(List.of("Ada", "Ben", "Chidi", "Dara", "Eze"));

        System.out.println("1. The 1996 loop: an Enumeration, walked by hand");
        List<String> byHand = new ArrayList<>();
        Enumeration<String> walk = cohort.elements();
        while (walk.hasMoreElements()) {
            byHand.add(walk.nextElement());
        }
        System.out.println("   " + byHand);
        System.out.println();

        System.out.println("2. The one-call replacement: Collections.list");
        System.out.println("   " + Collections.list(cohort.elements()));
        System.out.println();

        System.out.println("3. An Enumeration is not an Iterable, so a for-each needs asIterator()");
        int vowels = 0;
        for (Iterator<String> it = cohort.elements().asIterator(); it.hasNext(); ) {
            String name = it.next();
            if ("AEIOU".indexOf(name.charAt(0)) >= 0) {
                vowels++;
            }
        }
        System.out.println("   names starting with a vowel -> " + vowels);
        System.out.println();

        System.out.println("4. What is missing: asIterator() has no remove()");
        Iterator<String> readOnly = cohort.elements().asIterator();
        readOnly.next();
        try {
            readOnly.remove();
        } catch (UnsupportedOperationException ex) {
            System.out.println("   remove() -> " + ex);
        }
        System.out.println("   An Enumeration has no remove(), and asIterator() does not invent one.");
        System.out.println("   To take things out, copy into a real List first:");
        List<String> copy = new ArrayList<>(cohort);
        copy.removeIf(name -> name.startsWith("B"));
        System.out.println("   " + copy);
    }
}
