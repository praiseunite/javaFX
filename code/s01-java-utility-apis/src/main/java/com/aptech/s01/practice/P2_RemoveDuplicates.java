package com.aptech.s01.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Practice 2 (easy) — Remove duplicates but keep the original order. */
public class P2_RemoveDuplicates {
    public static void main(String[] args) {
        List<String> signIns = Arrays.asList("Ada", "Ben", "Ada", "Chi", "Ben", "Dayo");

        Set<String> unique = new LinkedHashSet<>(signIns);     // the constructor adds everything
        List<String> cleaned = new ArrayList<>(unique);        // back to a List if you need indexes

        System.out.println("Before: " + signIns);
        System.out.println("After:  " + cleaned);
        System.out.println("Removed " + (signIns.size() - cleaned.size()) + " duplicates");
    }
}
