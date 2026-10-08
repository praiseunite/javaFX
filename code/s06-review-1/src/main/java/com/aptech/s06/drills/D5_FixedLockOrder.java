package com.aptech.s06.drills;

/**
 * DRILL 5b — the same two chefs, the same two locks, and one agreed order.
 *
 * D5_LockOrderDeadlock deadlocks because Ada takes pot-then-spoon while Ben takes
 * spoon-then-pot. A cycle is possible: Ada holds the pot and wants the spoon, Ben holds
 * the spoon and wants the pot.
 *
 * Here, both chefs take the pot first. That single rule makes a cycle impossible: whoever
 * gets the pot simply carries on and finishes, and the other one waits its turn at the
 * first lock instead of being stuck at the second. No timeout, no retry, no luck.
 *
 * Rule to keep: if a program takes more than one lock, write the order down and follow it
 * in every single thread. It only takes one method that ignores it to bring the whole
 * program down.
 */
public class D5_FixedLockOrder {

    private static final Object POT = new Object();
    private static final Object SPOON = new Object();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Drill 5b - one agreed lock order (pot first, always)");
        System.out.println();

        // Both chefs name the pot first, even though Ben wants the spoon.
        Thread ada = new Thread(() -> cook("Chef Ada", "pot", POT, "spoon", SPOON), "Chef Ada");
        Thread ben = new Thread(() -> cook("Chef Ben", "pot", POT, "spoon", SPOON), "Chef Ben");

        ada.start();
        ben.start();
        ada.join();
        ben.join();

        System.out.println();
        System.out.println("Both chefs finished and the program ended on its own.");
        System.out.println("One order, followed everywhere, and a circular wait cannot form.");
    }

    private static void cook(String chef, String first, Object firstLock,
                             String second, Object secondLock) {
        System.out.println(chef + " wants the " + first + ".");
        synchronized (firstLock) {
            sleep(300);             // the other chef queues here, politely
            System.out.println(chef + " holds the " + first + ", now needs the " + second + " ...");
            synchronized (secondLock) {
                System.out.println(chef + " is cooking.");
            }
        }
        System.out.println(chef + " puts everything down.");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
