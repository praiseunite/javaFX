package com.aptech.s11b.practice;

import com.aptech.s11b.ds.IntList;

import java.util.Locale;

/**
 * P1 - Easy. Growth, counted.
 *
 * The task: give two lists the same job - add the numbers 1 to 1000 - and count the element
 * copies each one performs. One doubles when it fills; the other just adds ten more slots.
 * Explain the difference with a number, not an adjective.
 *
 * Write your prediction down before you run it. A prediction you got wrong teaches more than an
 * output you nodded at.
 */
public class P1_GrowByDoublingOrByTen {

    private static final int ADDS = 1000;

    public static void main(String[] args) {
        System.out.println("The same job, twice: add the numbers 1 to " + ADDS + ".");
        System.out.println();
        System.out.printf(Locale.ROOT, "   %-16s %-10s %-10s %s%n",
                "list", "capacity", "copies", "copies per add");

        IntList doubling = new IntList();
        for (int i = 1; i <= ADDS; i++) {
            doubling.add(i);
        }
        report("doubling", doubling.capacity(), doubling.copies());

        GrowByTen byTen = new GrowByTen();
        for (int i = 1; i <= ADDS; i++) {
            byTen.add(i);
        }
        report("+10 each time", byTen.capacity(), byTen.copies());

        System.out.println();
        System.out.println("Both lists hold the same thousand numbers, and both answers are right.");
        System.out.println("One of them did about fifty times the work to get there.");
        System.out.println();
        System.out.println("Why doubling wins, in one sentence: each growth copies everything that is");
        System.out.println("already stored, so what matters is how many times you grow. Doubling makes");
        System.out.println("the gaps between growths double too - after 4, 8, 16, 32 ... the array is");
        System.out.println("replaced only about log2(n) times, so the copies add up to less than 2n.");
        System.out.println();
        System.out.println("Growing by a fixed ten makes the gaps the same size forever, so you grow");
        System.out.println("about n/10 times - a hundred growths here - and each of them copies");
        System.out.println("everything stored so far. The last one alone copies nearly a thousand");
        System.out.println("elements. That is the five-figure number in the table above. Both lists");
        System.out.println("are correct. Only one is usable, and the difference is one line.");
        System.out.println();
        System.out.println("Copies per add under doubling: " + copiesPerAdd(doubling.copies())
                + ". The number you will see quoted as \"O(1) amortised\" - 'amortised' is");
        System.out.println("the word that is doing the work, and it means exactly this average.");
    }

    private static void report(String label, int capacity, int copies) {
        System.out.printf(Locale.ROOT, "   %-16s %-10d %-10d %s%n",
                label, capacity, copies, copiesPerAdd(copies));
    }

    private static String copiesPerAdd(int copies) {
        return String.format(Locale.ROOT, "%.2f", copies / (double) ADDS);
    }

    /**
     * The other half of the experiment: the same list, with the one line changed. Everything
     * else - the array, the size counter, the copy - is identical.
     */
    private static final class GrowByTen {

        private static final int STEP = 10;

        private int[] data = new int[4];
        private int size;
        private int copies;

        void add(int value) {
            if (size == data.length) {
                int[] bigger = new int[data.length + STEP];
                System.arraycopy(data, 0, bigger, 0, size);
                copies += size;
                data = bigger;
            }
            data[size++] = value;
        }

        int capacity() {
            return data.length;
        }

        int copies() {
            return copies;
        }
    }
}
