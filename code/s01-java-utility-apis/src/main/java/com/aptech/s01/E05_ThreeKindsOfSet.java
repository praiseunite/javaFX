package com.aptech.s01;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Example 5 — Same data, three Sets, three different orders. Duplicates are always ignored.
 */
public class E05_ThreeKindsOfSet {
    public static void main(String[] args) {
        String[] visitors = {"Zara", "Musa", "Ada", "Musa", "Ben", "Zara"};

        Set<String> hash   = new HashSet<>();
        Set<String> linked = new LinkedHashSet<>();
        Set<String> tree   = new TreeSet<>();

        for (String v : visitors) {
            hash.add(v);
            linked.add(v);
            tree.add(v);
        }

        System.out.println("HashSet:       " + hash);    // fastest, order looks random
        System.out.println("LinkedHashSet: " + linked);  // remembers insertion order
        System.out.println("TreeSet:       " + tree);    // always sorted
        System.out.println("Unique visitors: " + hash.size());

        // add() tells you whether the element was new
        boolean added = hash.add("Ada");
        System.out.println("Was 'Ada' added again? " + added);
    }
}
