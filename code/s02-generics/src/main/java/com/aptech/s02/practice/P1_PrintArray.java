package com.aptech.s02.practice;

/** Practice 1 (easy) — ONE generic method that prints ANY array, with its length. */
public class P1_PrintArray {

    static <T> void printArray(String label, T[] array) {
        System.out.print(label + " (" + array.length + "): ");
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        printArray("Names", new String[]{"Ada", "Ben"});
        printArray("Scores", new Integer[]{90, 75, 60});
        printArray("Prices", new Double[]{9.99, 4.5});
        printArray("Flags", new Character[]{'N', 'G'});
    }
}
