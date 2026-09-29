package com.aptech.s01.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Practice 6 (challenge) — Group words that are anagrams of each other. */
public class P6_AnagramGroups {
    public static void main(String[] args) {
        String[] words = {"listen", "silent", "enlist", "google", "stone", "notes", "onset", "java"};

        // Key = the word's letters sorted (e.g. "listen" -> "eilnst"); value = all words with those letters
        Map<String, List<String>> groups = new TreeMap<>();
        for (String w : words) {
            char[] letters = w.toCharArray();
            Arrays.sort(letters);
            String key = new String(letters);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(w);
        }

        for (Map.Entry<String, List<String>> g : groups.entrySet()) {
            System.out.println(g.getKey() + " -> " + g.getValue());
        }
    }
}
