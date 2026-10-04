package com.aptech.s05.practice;

/**
 * PRACTICE 4 (Medium) — find and fix a deadlock.
 *
 * The broken program had one cook take the GRILL then the FRYER, and the other take
 * the FRYER then the GRILL. Both could freeze forever. The fix is not more locks and
 * not a longer sleep: it is ONE agreed order that everybody follows.
 *
 * The broken version is kept as a comment so you can see exactly what changed.
 */
public class P4_FixTheDeadlock {

    private static final Object GRILL = new Object();
    private static final Object FRYER = new Object();

    public static void main(String[] args) throws InterruptedException {
        // BROKEN — opposite orders. Each thread holds what the other one needs.
        //   Thread ada = new Thread(() -> cookWrong("Ada", GRILL, FRYER));
        //   Thread ben = new Thread(() -> cookWrong("Ben", FRYER, GRILL));

        // FIXED — both cooks reach for the GRILL first.
        Thread ada = new Thread(() -> cook("Ada"), "Ada");
        Thread ben = new Thread(() -> cook("Ben"), "Ben");

        ada.start();
        ben.start();

        ada.join();
        ben.join();

        System.out.println();
        System.out.println("Both cooks finished and the program exited by itself.");
        System.out.println("One rule removed the deadlock: always take the GRILL before the FRYER.");
    }

    /** Everyone takes the GRILL first, then the FRYER. The same order, every time. */
    static void cook(String name) {
        synchronized (GRILL) {
            System.out.println(name + " has the grill.");
            sleep(200);
            synchronized (FRYER) {
                System.out.println(name + " has the fryer too - cooking.");
            }
            System.out.println(name + " put both utensils down.");
        }
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
