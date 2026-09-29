package com.aptech.s02;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 4 — A class with two type parameters, and a List of Pairs.
 */
public class E04_PairDemo {
    public static void main(String[] args) {
        Pair<String, Integer> ada = new Pair<>("Ada", 95);         // K = String, V = Integer
        System.out.println(ada.getFirst() + " scored " + ada.getSecond());

        Pair<Integer, String> swapped = ada.swap();                 // the types are swapped too
        System.out.println("Swapped: " + swapped);

        Pair<String, Boolean> paid = Pair.of("Ben", true);          // factory method, types inferred
        System.out.println("Paid? " + paid);

        List<Pair<String, Integer>> results = new ArrayList<>();    // generics inside generics
        results.add(ada);
        results.add(new Pair<>("Chi", 71));
        int total = 0;
        for (Pair<String, Integer> p : results) {
            total += p.getSecond();
        }
        System.out.println(results + " -> total " + total);
    }
}
