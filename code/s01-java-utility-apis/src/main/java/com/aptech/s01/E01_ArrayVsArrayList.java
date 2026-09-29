package com.aptech.s01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example 1 — Why do we need collections at all?
 * An array has a FIXED size. An ArrayList grows and shrinks for you.
 */
public class E01_ArrayVsArrayList {
    public static void main(String[] args) {
        // ---- The old way: an array with a fixed size of 3 ----
        String[] seats = new String[3];
        seats[0] = "Ada";
        seats[1] = "Ben";
        seats[2] = "Chi";
        // seats[3] = "Dan";   // would crash: ArrayIndexOutOfBoundsException
        System.out.println("Array:     " + Arrays.toString(seats));

        // ---- The collection way: an ArrayList grows automatically ----
        List<String> names = new ArrayList<>();
        names.add("Ada");
        names.add("Ben");
        names.add("Chi");
        names.add("Dan");                  // no problem — it just grows
        System.out.println("ArrayList: " + names);
        System.out.println("Size now:  " + names.size());

        names.remove("Ben");               // removing is one line, the gap closes by itself
        System.out.println("After remove: " + names);
    }
}
