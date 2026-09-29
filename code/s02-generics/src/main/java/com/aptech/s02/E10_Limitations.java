package com.aptech.s02;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 10 — The limits of generics, and why they exist (type erasure).
 */
public class E10_Limitations {

    static class Shelf<T> {
        private final List<T> items = new ArrayList<>();   // use a List instead of a T[] array
        // private T[] slots = new T[10];                   // COMPILE ERROR: generic array creation
        // private static T favourite;                      // COMPILE ERROR: static + T not allowed

        void add(T item) { items.add(item); }

        T make() {
            // return new T();                              // COMPILE ERROR: Java cannot create a T
            return null;
        }
    }

    public static void main(String[] args) {
        // 1) Type erasure: at run time the <String> / <Integer> part is gone.
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        System.out.println("Same class at run time? " + (strings.getClass() == numbers.getClass()));
        System.out.println("Class name: " + strings.getClass().getName());

        // 2) So you can check "is it a List?" but NOT "is it a List<String>?"
        Object thing = strings;
        System.out.println("thing instanceof List<?> : " + (thing instanceof List<?>));
        // thing instanceof List<String>                    // COMPILE ERROR: cannot check at run time

        // 3) No primitives as type arguments - use the wrapper class
        // List<int> bad;                                   // COMPILE ERROR: unexpected type
        List<Integer> ok = List.of(1, 2, 3);
        System.out.println("List<Integer> works: " + ok);

        Shelf<String> shelf = new Shelf<>();
        shelf.add("book");
        System.out.println("make() returned " + shelf.make());
    }
}
