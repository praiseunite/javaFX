package com.aptech.s01;

import java.util.TreeSet;

/**
 * Example 7 — A TreeSet is sorted, so it can answer "nearest" questions.
 */
public class E07_TreeSetNavigation {
    public static void main(String[] args) {
        TreeSet<Integer> busTimes = new TreeSet<>();   // minutes past the hour
        busTimes.add(45);
        busTimes.add(5);
        busTimes.add(30);
        busTimes.add(15);
        System.out.println("Timetable:        " + busTimes);

        System.out.println("First bus:        " + busTimes.first());
        System.out.println("Last bus:         " + busTimes.last());
        System.out.println("Next bus at/after :20 -> " + busTimes.ceiling(20));
        System.out.println("Last bus at/before :20 -> " + busTimes.floor(20));
        System.out.println("Buses before :30: " + busTimes.headSet(30));
        System.out.println("Buses from :30:   " + busTimes.tailSet(30));
    }
}
