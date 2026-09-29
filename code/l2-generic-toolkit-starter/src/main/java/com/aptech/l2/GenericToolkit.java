package com.aptech.l2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Lab Task L2 — Generic Toolkit.   <<< YOU COMPLETE THIS FILE (and Shelf.java) >>>
 *
 * Every method below has a TODO. The method HEADERS (with their generics) are already written for you:
 * read each header carefully - it tells you what the method accepts and returns.
 * Run SelfCheck after each method.
 *
 * Author: YOUR NAME HERE
 */
public final class GenericToolkit {

    private GenericToolkit() { }   // a toolbox of static methods - nobody needs to create one

    /**
     * R1 — Swaps the elements at positions i and j of any list.
     * Hint: remember the element at i in a variable of type T, then use list.set(...) twice.
     */
    public static <T> void swap(List<T> list, int i, int j) {
        // TODO R1
    }

    /**
     * R2 — Returns the last element, or 'fallback' if the list is empty.
     * Hint: the last index is list.size() - 1.
     */
    public static <T> T lastOrDefault(List<T> list, T fallback) {
        // TODO R2
        return null;
    }

    /**
     * R3 — Returns the largest element using compareTo, or null if the list is empty.
     * Why can we call compareTo here? Look at the bound: <T extends Comparable<T>>.
     */
    public static <T extends Comparable<T>> T largest(List<T> list) {
        // TODO R3
        return null;
    }

    /**
     * R4 — Returns the sum of any list of numbers (List<Integer>, List<Double>, List<Long>...).
     * Hint: loop with "for (Number n : numbers)" and use n.doubleValue().
     */
    public static double total(List<? extends Number> numbers) {
        // TODO R4
        return -1;
    }

    /**
     * R5 — Adds 'value' to 'dest' exactly 'times' times.
     * dest is List<? super T>, so you may ADD a T to it (e.g. Integers into a List<Number>).
     */
    public static <T> void fill(List<? super T> dest, T value, int times) {
        // TODO R5
    }

    /**
     * R6 — Counts how many elements are equal to target.
     * Use Objects.equals(a, b) instead of a.equals(b), so that null elements don't crash.
     */
    public static <T> int countMatches(List<T> list, T target) {
        // TODO R6
        return -1;
    }

    /**
     * R8 — Returns a NEW map with keys and values swapped: {NG=234} becomes {234=NG}.
     * Keep the original order by using a LinkedHashMap. Notice the return type: Map<V, K>.
     */
    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        // TODO R8
        return new LinkedHashMap<>();
    }
}
