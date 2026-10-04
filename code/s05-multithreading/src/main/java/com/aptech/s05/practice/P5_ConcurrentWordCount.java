package com.aptech.s05.practice;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PRACTICE 5 (Medium) — count words with several threads.
 *
 * Each page is counted on its own thread, and all of them write into ONE
 * ConcurrentHashMap. merge() is atomic, so two threads updating the same word can
 * never lose an update the way a plain HashMap would.
 */
public class P5_ConcurrentWordCount {

    static final List<String> PAGES = List.of(
            "the quick brown fox jumps over the lazy dog",
            "the dog barks and the fox runs",
            "the lazy dog sleeps");

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        Thread[] threads = new Thread[PAGES.size()];
        for (int i = 0; i < PAGES.size(); i++) {
            String page = PAGES.get(i);
            threads[i] = new Thread(() -> {
                for (String word : page.split(" ")) {
                    counts.merge(word, 1, Integer::sum);      // atomic read-modify-write
                }
            }, "page-" + (i + 1));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Different words found: " + counts.size());
        System.out.println("  \"the\" appears " + counts.get("the") + " times");
        System.out.println("  \"dog\" appears " + counts.get("dog") + " times");
        System.out.println("  \"fox\" appears " + counts.get("fox") + " times");
        System.out.println("Total words counted: "
                + counts.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println();
        System.out.println("A plain HashMap here would lose counts, or corrupt itself and throw");
        System.out.println("ConcurrentModificationException - the same bug as Part 1, hiding in a class.");
    }
}
