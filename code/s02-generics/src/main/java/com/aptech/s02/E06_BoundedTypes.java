package com.aptech.s02;

import java.util.List;

/**
 * Example 6 — Bounded type parameters: "T can be any type, AS LONG AS it is a ..."
 */
public class E06_BoundedTypes {

    /** T must be a Number (Integer, Double, Long...), so we are allowed to call doubleValue(). */
    public static <T extends Number> double sum(List<T> numbers) {
        double total = 0;
        for (T n : numbers) {
            total += n.doubleValue();              // allowed ONLY because T extends Number
        }
        return total;
    }

    /** T must be comparable with itself, so we are allowed to call compareTo(). */
    public static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    /** A generic CLASS can have a bound too. */
    static class Stats<N extends Number> {
        private final List<N> values;

        Stats(List<N> values) { this.values = values; }

        double average() {
            return sum(values) / values.size();
        }
    }

    public static void main(String[] args) {
        System.out.println("sum ints    = " + sum(List.of(1, 2, 3)));
        System.out.println("sum doubles = " + sum(List.of(1.5, 2.5)));
        // sum(List.of("a", "b"));                // COMPILE ERROR: String is not a Number

        System.out.println("max word    = " + max(List.of("pear", "apple", "zebra", "fig")));
        System.out.println("max number  = " + max(List.of(7, 42, 3)));

        Stats<Integer> ages = new Stats<>(List.of(18, 21, 30));
        System.out.println("average age = " + ages.average());
        // Stats<String> bad;                     // COMPILE ERROR: String is not within bound Number
    }
}
