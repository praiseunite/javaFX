package com.aptech.s05.lab;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * GUIDED LAB — The Lunch Rush.
 *
 * The restaurant from Session 4 is open, and now it is 12:30. Four waiters take orders at
 * the same time, two cooks share the grill and the fryer, and the pass holds only two plates.
 * Everything that used to be "just threads" is now a problem of SHARED DATA.
 *
 * Build this program step by step (the step checklist is on the lesson page):
 *   STEP 1  the shared order tally — watch it lose orders
 *   STEP 2  fix the tally with an atomic class
 *   STEP 3  hand the tally to the cooks and prove nothing is lost
 *   STEP 4  the pass: a BlockingQueue does the waiting for you
 *   STEP 5  close the restaurant cleanly
 */
public class LunchRush {

    /** The tally every waiter adds to at the same time. */
    static final LongAdder ORDERS_TAKEN = new LongAdder();

    /** The pass between the kitchen and the floor: at most 2 plates waiting at once. */
    static final BlockingQueue<String> PASS = new ArrayBlockingQueue<>(2);

    static final AtomicInteger SERVED = new AtomicInteger();
    static final int WAITERS = 4;
    static final int ORDERS_PER_WAITER = 50_000;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        // STEP 1 & 2 — four waiters, one shared tally. LongAdder is atomic, so nothing is lost.
        Thread[] waiters = new Thread[WAITERS];
        for (int i = 0; i < WAITERS; i++) {
            waiters[i] = new Thread(() -> {
                for (int order = 0; order < ORDERS_PER_WAITER; order++) {
                    ORDERS_TAKEN.increment();          // ONE indivisible step: no race condition
                }
            }, "Waiter " + (i + 1));
            waiters[i].start();
        }

        // STEP 3 — the kitchen takes plates off the pass until we tell it to stop.
        Thread kitchen = new Thread(() -> {
            try {
                while (true) {
                    String plate = PASS.take();     // sleeps here all by itself when the pass is empty
                    if (plate.equals("CLOSED")) {
                        return;
                    }
                    SERVED.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Kitchen");

        Thread[] cooks = new Thread[2];
        for (int i = 0; i < cooks.length; i++) {
            cooks[i] = new Thread(() -> {
                try {
                    for (String dish : List.of("Jollof rice", "Pepper soup", "Puff-puff")) {
                        PASS.put(dish);             // waits by itself when the pass is full
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Cook " + (i + 1));
            cooks[i].start();
        }
        kitchen.start();

        for (Thread waiter : waiters) {
            waiter.join();                          // the manager waits for the last order
        }
        for (Thread cook : cooks) {
            cook.join();
        }

        PASS.put("CLOSED");                         // STEP 5 — tell the kitchen to stop
        kitchen.join();

        int expected = WAITERS * ORDERS_PER_WAITER;
        System.out.println("Orders taken:  " + ORDERS_TAKEN.sum() + "   (expected " + expected + ")");
        System.out.println("Orders lost:   " + (expected - ORDERS_TAKEN.sum()));
        System.out.println("Plates served: " + SERVED.get() + "   (6 dishes, 2 cooks, pass of 2)");
        System.out.println("Lunch rush finished in " + (System.currentTimeMillis() - start) + " ms.");
        System.out.println();
        System.out.println("Every shared thing in this program has one owner:");
        System.out.println("  the tally  -> LongAdder        (atomic, no lock written by hand)");
        System.out.println("  the pass   -> ArrayBlockingQueue (put/take do all the waiting)");
        System.out.println("  the count  -> AtomicInteger      (safe for one variable)");
    }
}
