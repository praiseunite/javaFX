package com.aptech.s01;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;

/**
 * Example 16 — A quick tour of other useful java.util classes (not collections).
 */
public class E16_UtilityTour {
    public static void main(String[] args) {
        // Random: pseudo-random numbers. A fixed "seed" gives the same numbers every run.
        Random dice = new Random(42);
        System.out.print("Dice rolls: ");
        for (int i = 0; i < 5; i++) {
            System.out.print((dice.nextInt(6) + 1) + " ");   // nextInt(6) gives 0..5
        }
        System.out.println();

        // StringJoiner: build "a, b, c" without fiddly comma logic
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        sj.add("Java").add("SQL").add("JavaFX");
        System.out.println("Skills: " + sj);

        // Objects: null-safe helpers
        String nickname = null;
        System.out.println("Equal? " + Objects.equals(nickname, "Ace"));        // no NullPointerException
        System.out.println("Name:  " + Objects.requireNonNullElse(nickname, "Guest"));

        // Optional: a box that may or may not contain a value
        Optional<String> found = Optional.of("Ada");
        Optional<String> missing = Optional.empty();
        System.out.println("Found:   " + found.orElse("nobody"));
        System.out.println("Missing: " + missing.orElse("nobody"));
    }
}
