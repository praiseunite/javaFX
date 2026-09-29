package com.aptech.s01;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Example 12 — PriorityQueue: the most urgent item is always served first.
 */
public class E12_PriorityQueueTriage {

    static class Patient {
        String name;
        int severity;                      // 1 = critical ... 5 = minor

        Patient(String name, int severity) {
            this.name = name;
            this.severity = severity;
        }

        int getSeverity() { return severity; }

        @Override
        public String toString() { return name + "(" + severity + ")"; }
    }

    public static void main(String[] args) {
        // Numbers: smallest number comes out first (natural order)
        PriorityQueue<Integer> numbers = new PriorityQueue<>();
        numbers.offer(50);
        numbers.offer(40);
        numbers.offer(30);
        numbers.offer(20);
        numbers.offer(10);
        System.out.println("Printed:  " + numbers);  // NOT fully sorted - internal heap order
        System.out.print("Polled:   ");
        while (!numbers.isEmpty()) {
            System.out.print(numbers.poll() + " "); // always sorted when you poll
        }
        System.out.println();

        // Objects: tell the queue HOW to compare using a Comparator
        PriorityQueue<Patient> er = new PriorityQueue<>(Comparator.comparingInt(Patient::getSeverity));
        er.offer(new Patient("Ada", 4));
        er.offer(new Patient("Ben", 1));
        er.offer(new Patient("Chi", 3));
        er.offer(new Patient("Dan", 2));
        while (!er.isEmpty()) {
            System.out.println("Doctor sees: " + er.poll());
        }
    }
}
