package com.aptech.s02.practice;

import java.util.ArrayList;
import java.util.List;

/** Practice 6 (challenge) — "Producer extends, Consumer super": a flexible copy method. */
public class P6_CopyPecs {

    /** Copies every element from src (a PRODUCER of T) into dest (a CONSUMER of T). */
    static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (T item : src) {
            dest.add(item);
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(4.5, 5.5);

        List<Number> numbers = new ArrayList<>();
        copy(numbers, ints);          // T = Integer: Integer list -> Number list
        copy(numbers, doubles);       // T = Double:  Double list  -> Number list
        System.out.println("numbers = " + numbers);

        List<Object> everything = new ArrayList<>();
        copy(everything, numbers);    // T = Number:  Number list  -> Object list
        copy(everything, List.of("done"));
        System.out.println("everything = " + everything);
        // copy(ints, numbers);       // COMPILE ERROR: can't put any Number into a List<Integer>
    }
}
