package com.aptech.s01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example 14 — java.util.Collections: static helpers for Lists and other collections.
 * (Arrays helps arrays; Collections helps collections.)
 */
public class E14_CollectionsClass {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>(List.of(5, 3, 9, 1, 3));

        Collections.sort(nums);
        System.out.println("sort:      " + nums);

        Collections.reverse(nums);
        System.out.println("reverse:   " + nums);

        System.out.println("max / min: " + Collections.max(nums) + " / " + Collections.min(nums));
        System.out.println("count 3s:  " + Collections.frequency(nums, 3));

        Collections.swap(nums, 0, 4);
        System.out.println("swap 0,4:  " + nums);

        List<Integer> locked = Collections.unmodifiableList(nums);
        try {
            locked.add(100);
        } catch (UnsupportedOperationException e) {
            System.out.println("Can't change an unmodifiable list!");
        }
    }
}
