package com.aptech.s02;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 7 — Wildcards: ?  |  ? extends X  |  ? super X
 */
public class E07_Wildcards {

    /** Unbounded wildcard: a list of ANYTHING. We can read items (as Object) but not add. */
    static void printList(List<?> list) {
        for (Object o : list) {
            System.out.print(o + " ");
        }
        System.out.println("(" + list.size() + " items)");
    }

    /** Upper bound: a list of Number OR any subtype. Safe to READ as Number (a "producer"). */
    static double total(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        // list.add(1);                          // COMPILE ERROR: might be a List<Double>!
        return sum;
    }

    /** Lower bound: a list of Integer OR any supertype. Safe to ADD Integers (a "consumer"). */
    static void addOneToFive(List<? super Integer> list) {
        for (int i = 1; i <= 5; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(0.5, 0.25);
        List<String> words = List.of("hi", "there");

        printList(ints);                           // List<?> accepts every kind of list
        printList(words);

        System.out.println("total(ints)    = " + total(ints));
        System.out.println("total(doubles) = " + total(doubles));
        // total(words);                           // COMPILE ERROR: String is not a Number

        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        addOneToFive(numbers);                     // Number is a supertype of Integer - OK
        addOneToFive(objects);                     // Object is a supertype of Integer - OK
        System.out.println("numbers = " + numbers + ", objects = " + objects);

        // THE surprising rule: Integer IS-A Number, but List<Integer> is NOT a List<Number>
        // List<Number> bad = ints;                // COMPILE ERROR: incompatible types
        List<? extends Number> ok = ints;          // this is how you say "a list of some kind of Number"
        System.out.println("first via wildcard = " + ok.get(0));
    }
}
