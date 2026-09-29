package com.aptech.s01;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Example 3 — Four ways to walk through a list, and the safe way to delete while walking.
 */
public class E03_LoopingOverLists {
    public static void main(String[] args) {
        List<Integer> scores = new ArrayList<>(List.of(72, 45, 88, 39, 95));

        // 1) Classic for loop — use when you need the index
        for (int i = 0; i < scores.size(); i++) {
            System.out.println("Student " + i + " scored " + scores.get(i));
        }

        // 2) Enhanced for (for-each) — the most common, easiest to read
        int total = 0;
        for (int s : scores) {
            total += s;
        }
        System.out.println("Total = " + total);

        // 3) Iterator — the ONLY safe way to remove inside a loop (the old way)
        Iterator<Integer> it = scores.iterator();
        while (it.hasNext()) {
            if (it.next() < 50) {
                it.remove();               // removes the element just returned by next()
            }
        }
        System.out.println("Passed (Iterator): " + scores);

        // 4) removeIf + forEach with a lambda — the modern, shortest way
        List<Integer> more = new ArrayList<>(List.of(72, 45, 88, 39, 95));
        more.removeIf(s -> s < 50);
        more.forEach(s -> System.out.print(s + " "));
        System.out.println();
    }
}
