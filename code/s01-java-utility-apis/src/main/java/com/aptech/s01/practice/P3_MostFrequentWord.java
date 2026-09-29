package com.aptech.s01.practice;

import java.util.HashMap;
import java.util.Map;

/** Practice 3 (medium) — Which word appears most often? */
public class P3_MostFrequentWord {
    public static void main(String[] args) {
        String text = "to be or not to be that is the question to ask";

        Map<String, Integer> counts = new HashMap<>();
        for (String word : text.split(" ")) {
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }

        String bestWord = null;
        int bestCount = 0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > bestCount) {
                bestWord = e.getKey();
                bestCount = e.getValue();
            }
        }
        System.out.println("Most frequent: \"" + bestWord + "\" (" + bestCount + " times)");
    }
}
