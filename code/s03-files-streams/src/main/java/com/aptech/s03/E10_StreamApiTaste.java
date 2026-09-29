package com.aptech.s03;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Example 10 — Don't confuse the two kinds of "stream"!
 * java.io streams move BYTES/CHARACTERS in and out of files (this session).
 * java.util.stream (the Stream API) processes the ELEMENTS of a collection in a pipeline.
 */
public class E10_StreamApiTaste {
    public static void main(String[] args) {
        List<Integer> scores = List.of(95, 42, 78, 61, 88, 30);

        List<Integer> passed = scores.stream()          // 1) turn the list into a Stream
                .filter(s -> s >= 50)                   // 2) keep only the passes
                .sorted()                               // 3) sort them
                .collect(Collectors.toList());          // 4) gather the result into a new List
        System.out.println("Passed, sorted: " + passed);

        double average = scores.stream()
                .mapToInt(Integer::intValue)            // Integer -> int so we can do maths
                .average()
                .orElse(0);
        System.out.println("Average: " + average);

        String report = scores.stream()
                .map(s -> s >= 50 ? "P" : "F")          // turn each score into a letter
                .collect(Collectors.joining(""));
        System.out.println("Pass/fail pattern: " + report);
    }
}
