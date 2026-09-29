package com.aptech.s01.practice;

import java.util.ArrayDeque;
import java.util.Queue;

/** Practice 4 (medium) — Simulate a bank with one cashier. */
public class P4_BankQueue {
    public static void main(String[] args) {
        Queue<String> line = new ArrayDeque<>();
        String[] events = {"Ada", "Ben", "SERVE", "Chi", "SERVE", "SERVE", "SERVE", "Dan"};

        int served = 0;
        for (String event : events) {
            if (event.equals("SERVE")) {
                String customer = line.poll();                  // null if nobody is waiting
                if (customer == null) {
                    System.out.println("Cashier: nobody to serve");
                } else {
                    served++;
                    System.out.println("Cashier serves " + customer + "   line now " + line);
                }
            } else {
                line.offer(event);
                System.out.println(event + " joins          line now " + line);
            }
        }
        System.out.println("Served " + served + ", still waiting " + line.size());
    }
}
