package com.aptech.s01;

import java.util.Set;
import java.util.TreeSet;

/**
 * Example 6 — Union, intersection and difference (like Venn diagrams from maths class).
 */
public class E06_SetOperations {
    public static void main(String[] args) {
        Set<String> java   = new TreeSet<>(Set.of("Ada", "Ben", "Chi", "Dayo"));
        Set<String> python = new TreeSet<>(Set.of("Chi", "Dayo", "Efe"));

        Set<String> union = new TreeSet<>(java);   // copy first, so 'java' is not changed
        union.addAll(python);
        System.out.println("Either course (union):      " + union);

        Set<String> both = new TreeSet<>(java);
        both.retainAll(python);
        System.out.println("Both courses (intersection): " + both);

        Set<String> onlyJava = new TreeSet<>(java);
        onlyJava.removeAll(python);
        System.out.println("Java only (difference):      " + onlyJava);
    }
}
