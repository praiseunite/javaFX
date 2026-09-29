package com.aptech.s01;

import java.util.Arrays;
import java.util.List;

/**
 * Example 13 — java.util.Arrays: a toolbox of static helper methods for plain arrays.
 */
public class E13_ArraysClass {
    public static void main(String[] args) {
        int[] marks = {67, 92, 45, 81, 58};

        System.out.println("Original:  " + Arrays.toString(marks));

        Arrays.sort(marks);
        System.out.println("Sorted:    " + Arrays.toString(marks));

        // binarySearch ONLY works on a sorted array
        System.out.println("Index of 81:  " + Arrays.binarySearch(marks, 81));
        System.out.println("Search 70:    " + Arrays.binarySearch(marks, 70));   // negative = not found

        int[] top3 = Arrays.copyOfRange(marks, 2, 5);   // from index 2 up to (not incl.) 5
        System.out.println("Top three: " + Arrays.toString(top3));

        int[] bigger = Arrays.copyOf(marks, 7);         // extra slots are filled with 0
        System.out.println("copyOf 7:  " + Arrays.toString(bigger));

        int[] zeros = new int[4];
        Arrays.fill(zeros, 100);
        System.out.println("fill:      " + Arrays.toString(zeros));

        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        System.out.println("a == b ?          " + (a == b));            // compares addresses
        System.out.println("Arrays.equals ?   " + Arrays.equals(a, b)); // compares contents

        List<String> days = Arrays.asList("Mon", "Tue", "Wed");    // array -> fixed-size List
        System.out.println("asList:    " + days);

        int[][] grid = {{1, 2}, {3, 4}};
        System.out.println("2D:        " + Arrays.deepToString(grid));
    }
}
