package com.aptech.s06.tyi;

import java.util.ArrayList;
import java.util.List;

/**
 * TRY IT YOURSELF 2 — Session 2: Generics and wildcards.
 *
 * Write one method that only READS from a list of numbers (so it must accept any subtype of
 * Number) and one that only WRITES integers into a list (so it must accept any supertype).
 *
 *   List&lt;? extends Number&gt;  ->  PRODUCER. You may read. Total them, print them, copy them out.
 *   List&lt;? super Integer&gt;   ->  CONSUMER. You may add. The compiler can prove the add is safe.
 *
 * The trap to avoid: writing `List&lt;Number&gt;` for the first one. That rejects a
 * List&lt;Integer&gt;, which is the whole reason wildcards exist.
 */
public class T2_Generics {

    /** PRODUCER: accepts List<Integer>, List<Double>, List<Long>, List<Number> ... */
    static double total(List<? extends Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();     // reading is always safe: every element IS a Number
        }
        return sum;
    }

    /** CONSUMER: accepts List<Integer>, List<Number>, List<Object> ... */
    static void addAll(List<? super Integer> target, int... values) {
        for (int value : values) {
            target.add(value);          // writing is always safe: everything here IS an Integer
        }
    }

    public static void main(String[] args) {
        List<Integer> marks = List.of(70, 85, 62);
        List<Double> prices = List.of(19.99, 4.50);
        List<Long> views = List.of(1_200L, 340L);

        System.out.println("Try It Yourself 2 - PECS in two method signatures");
        System.out.println();
        System.out.println("Reading - one method, three different list types:");
        System.out.printf("  List<Integer> marks  total = %.2f%n", total(marks));
        System.out.printf("  List<Double>  prices total = %.2f%n", total(prices));
        System.out.printf("  List<Long>    views  total = %.2f%n", total(views));
        System.out.println("  a List<Number> parameter would have refused all three.");
        System.out.println();

        List<Number> sink = new ArrayList<>();
        addAll(sink, 10, 20, 30);
        System.out.println("Writing - one method, fills a List<Number>:");
        System.out.println("  sink is now " + sink);

        List<Object> objects = new ArrayList<>();
        addAll(objects, 7);
        System.out.println("  the same method also fills a List<Object>: " + objects);
        System.out.println();
        System.out.println("PECS: Producer Extends, Consumer Super. If your method reads, use extends.");
        System.out.println("If it writes, use super. If it does both, do not use a wildcard at all.");
    }
}
