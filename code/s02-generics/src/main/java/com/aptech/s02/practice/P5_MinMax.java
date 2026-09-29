package com.aptech.s02.practice;

import com.aptech.s02.Pair;

import java.util.List;

/** Practice 5 (challenge) — find the smallest AND largest in ONE pass, returned as a Pair. */
public class P5_MinMax {

    static <T extends Comparable<T>> Pair<T, T> minMax(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        T min = items.get(0);
        T max = items.get(0);
        for (T item : items) {
            if (item.compareTo(min) < 0) min = item;
            if (item.compareTo(max) > 0) max = item;
        }
        return new Pair<>(min, max);
    }

    public static void main(String[] args) {
        Pair<Integer, Integer> nums = minMax(List.of(42, 7, 19, 88, 3));
        System.out.println("numbers -> min " + nums.getFirst() + ", max " + nums.getSecond());

        Pair<String, String> cities = minMax(List.of("Lagos", "Abuja", "Kano", "Port Harcourt"));
        System.out.println("cities  -> first " + cities.getFirst() + ", last " + cities.getSecond());
    }
}
