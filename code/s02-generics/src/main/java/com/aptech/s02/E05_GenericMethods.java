package com.aptech.s02;

import java.util.Arrays;
import java.util.List;

/**
 * Example 5 — Generic METHODS: the method has its own type parameter, written before the return type.
 */
public class E05_GenericMethods {

    /** Prints any array, whatever the element type. */
    public static <T> void printAll(T[] items) {
        for (T item : items) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    /** Returns the first element, or null if the list is empty. The return type follows T. */
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    /** Swaps two positions in any array. */
    public static <T> void swap(T[] items, int i, int j) {
        T temp = items[i];
        items[i] = items[j];
        items[j] = temp;
    }

    public static void main(String[] args) {
        String[] names = {"Ada", "Ben", "Chi"};
        Integer[] scores = {72, 95, 60};

        printAll(names);                               // T is inferred as String
        printAll(scores);                              // T is inferred as Integer

        swap(names, 0, 2);
        System.out.println("After swap: " + Arrays.toString(names));

        String first = firstOrNull(List.of("x", "y")); // T = String, so it returns a String
        Integer none = firstOrNull(List.<Integer>of()); // explicit type witness: List.<Integer>of()
        System.out.println("first = " + first + ", from empty list = " + none);

        E05_GenericMethods.<Double>printAll(new Double[]{1.5, 2.5});   // you CAN name T yourself
    }
}
