package com.aptech.s01;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Example 9 — Counting things with a Map (the most common Map job in real programs).
 */
public class E09_WordFrequency {
    public static void main(String[] args) {
        String text = "the cat and the dog and the bird";

        Map<String, Integer> counts = new HashMap<>();
        for (String word : text.split(" ")) {
            // Beginner version: look up, then add one
            int current = counts.getOrDefault(word, 0);
            counts.put(word, current + 1);
        }
        System.out.println("HashMap: " + counts);

        // Same result in one line per word using merge()
        Map<String, Integer> sorted = new TreeMap<>();
        for (String word : text.split(" ")) {
            sorted.merge(word, 1, Integer::sum);
        }
        System.out.println("TreeMap: " + sorted);

        // Walk through the pairs with entrySet()
        for (Map.Entry<String, Integer> e : sorted.entrySet()) {
            System.out.println(e.getKey() + " -> " + "*".repeat(e.getValue()));
        }
    }
}
