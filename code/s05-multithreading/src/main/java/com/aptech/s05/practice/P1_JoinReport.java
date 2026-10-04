package com.aptech.s05.practice;

/**
 * PRACTICE 1 (Easy) — join() and isAlive().
 *
 * Start three cooks, ask whether each one is still working, then wait for all of them
 * before the waiter serves. Expected: three "still cooking? true" lines, then the
 * three dishes in the order the cooks finish, then the waiter's line last.
 */
public class P1_JoinReport {

    public static void main(String[] args) throws InterruptedException {
        String[] dishes = {"Jollof rice", "Pepper soup", "Puff-puff"};
        Thread[] cooks = new Thread[dishes.length];

        for (int i = 0; i < dishes.length; i++) {
            String dish = dishes[i];
            int cookNumber = i + 1;
            int cookMs = 150 * (dishes.length - i + 1); // Cook 1: 600 ms, Cook 2: 450 ms, Cook 3: 300 ms
            cooks[i] = new Thread(() -> {
                sleep(cookMs);
                System.out.println("  " + dish + " is ready (Cook " + cookNumber + ")");
            }, "Cook " + cookNumber);
            cooks[i].start();
        }

        // Ask each cook "are you still working?" — all three are, because none has slept long enough to finish.
        for (Thread cook : cooks) {
            System.out.println("  " + cook.getName() + " still cooking? " + cook.isAlive());
        }

        // The waiter cannot serve until EVERY dish is ready.
        for (Thread cook : cooks) {
            cook.join();
        }
        System.out.println("Waiter: all dishes ready - serving now.");
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
