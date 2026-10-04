package com.aptech.s05;

import java.util.concurrent.CountDownLatch;

/**
 * A race condition you can see with your own eyes.
 *
 * Eight chefs each add 100,000 orders to the SAME tally. The answer should be
 * 800,000 every single time. It never is - and the wrong number is different on
 * every run.
 *
 * WHY THE THREE STEPS ARE WRITTEN OUT
 * -----------------------------------
 * Real code says  count++  and looks like one step. It is really three:
 *
 *     int current = count;      // 1. READ the shared value into the processor
 *     current = current + 1;    // 2. ADD one
 *     count = current;          // 3. WRITE it back to memory
 *
 * Below, those three steps are spelled out, with a tiny pause in the middle.
 * The pause is NOT a fix and NOT something you need in real code: it only holds
 * the window between READ and WRITE open long enough for you to watch another
 * chef walk in and overwrite the value. Without it the processor is so quick
 * that the three steps look like one - and a hot JIT-compiled loop can hide the
 * problem completely, which is exactly why this bug reaches production.
 *
 * The CountDownLatch is only a starting gate: it makes all eight chefs begin at
 * the same instant so they really do collide. (We meet the rest of
 * java.util.concurrent in Part 7.)
 */
public class E02_RaceCondition {

    /** A shared tally. Nothing here protects it. */
    static class Tally {
        int count = 0;

        void addOne() {
            int current = count;        // READ  - every chef may read the same value
            Thread.yield();             // (demonstration aid: hold the window open)
            count = current + 1;        // WRITE - and now they overwrite each other
        }
    }

    static final int CHEFS = 8;
    static final int ORDERS_EACH = 100_000;

    public static void main(String[] args) throws InterruptedException {
        Tally tally = new Tally();
        CountDownLatch startGate = new CountDownLatch(1);

        Runnable job = () -> {
            try {
                startGate.await();                 // stand still until the starting signal
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            for (int i = 0; i < ORDERS_EACH; i++) {
                tally.addOne();
            }
        };

        Thread[] chefs = new Thread[CHEFS];
        for (int i = 0; i < CHEFS; i++) {
            chefs[i] = new Thread(job, "Chef " + (char) ('A' + i));
            chefs[i].start();
        }

        startGate.countDown();                     // GO! all eight run at the same moment
        for (Thread chef : chefs) {
            chef.join();                           // wait for every chef to finish
        }

        int expected = CHEFS * ORDERS_EACH;
        int lost = expected - tally.count;

        System.out.println(CHEFS + " chefs x " + String.format("%,d", ORDERS_EACH) + " orders each");
        System.out.println("Expected:     " + String.format("%,d", expected));
        System.out.println("Actual:       " + String.format("%,d", tally.count));
        System.out.println("Lost updates: " + String.format("%,d", lost));
        System.out.println();
        System.out.println("Same code. Same " + String.format("%,d", expected) + " orders. A different answer.");
        System.out.println("A race condition does not fail every time; it fails SOME time.");
        System.out.println("That is why it survives testing and then breaks in front of a customer.");
    }
}
