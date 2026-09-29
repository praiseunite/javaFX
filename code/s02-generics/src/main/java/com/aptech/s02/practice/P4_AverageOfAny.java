package com.aptech.s02.practice;

import java.util.List;

/** Practice 4 (medium) — ONE method that averages a List<Integer>, List<Double> or List<Long>. */
public class P4_AverageOfAny {

    static double average(List<? extends Number> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        return sum / numbers.size();
    }

    public static void main(String[] args) {
        List<Integer> ages = List.of(18, 22, 35);
        List<Double> temps = List.of(30.5, 28.0, 31.5, 29.0);
        List<Long> views = List.of(1_000_000L, 3_000_000L);

        System.out.println("Average age:   " + average(ages));
        System.out.println("Average temp:  " + average(temps));
        System.out.println("Average views: " + average(views));
        System.out.println("Empty list:    " + average(List.of()));
    }
}
