package com.aptech.s06.tyi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

/**
 * TRY IT YOURSELF 5 — Session 5: Multithreading and concurrency.
 *
 * The task: take a tally that several threads update, make it exactly right, and prove it
 * by running it. Both halves use the same number of chefs and the same number of orders, so
 * the ONLY difference is the tally itself.
 *
 * WHY THE THREE STEPS ARE WRITTEN OUT IN THE BROKEN VERSION
 * ---------------------------------------------------------
 * `count++` looks like one step and is really three: READ the value, ADD one, WRITE it back.
 * The broken tally below spells them out with a yield in the middle — that yield is a
 * demonstration aid that holds the window between READ and WRITE open long enough to watch a
 * second chef walk in. It is NOT a fix and NOT something real code needs. Without it the
 * processor is fast enough that a hot loop can hide the bug completely, which is precisely
 * why this defect reaches production.
 *
 * The CountDownLatch is only a starting gate, so all four chefs really do collide at once.
 */
public class T5_Tally {

    static final int CHEFS = 4;
    static final int ORDERS_EACH = 100_000;

    /** The broken tally: a plain int, with the three steps exposed. */
    static class PlainTally {
        int count = 0;

        void addOne() {
            int current = count;        // READ  - every chef may read the same value
            Thread.yield();             // (demonstration aid: hold the window open)
            count = current + 1;        // WRITE - and now they overwrite each other
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int expected = CHEFS * ORDERS_EACH;

        int broken = runPlain();
        long fixed = runAdder();

        System.out.println("Try It Yourself 5 - a racy tally, then the right fix");
        System.out.println(CHEFS + " chefs x " + String.format("%,d", ORDERS_EACH)
                + " orders = " + String.format("%,d", expected) + " expected");
        System.out.println();
        System.out.println("BEFORE - a plain int with count++ spelled out:");
        System.out.println("  actual       : " + String.format("%,d", broken));
        System.out.println("  lost updates : " + String.format("%,d", expected - broken));
        System.out.println("  -> wrong, and it will be a different wrong number next run.");
        System.out.println();
        System.out.println("AFTER - LongAdder, one indivisible increment:");
        System.out.println("  actual       : " + String.format("%,d", fixed));
        System.out.println("  lost updates : " + String.format("%,d", expected - fixed));
        System.out.println("  -> exactly right, and it stays exactly right however often you run it.");
        System.out.println();
        System.out.println("Both real fixes work: LongAdder when one hot counter is all there is,");
        System.out.println("synchronized when several fields must change together. What never works");
        System.out.println("is a bare count++ on data that more than one thread can touch.");
    }

    private static int runPlain() throws InterruptedException {
        PlainTally tally = new PlainTally();
        CountDownLatch gate = new CountDownLatch(1);
        Thread[] chefs = startChefs(gate, () -> {
            for (int i = 0; i < ORDERS_EACH; i++) {
                tally.addOne();
            }
        });
        gate.countDown();
        for (Thread chef : chefs) {
            chef.join();
        }
        return tally.count;
    }

    private static long runAdder() throws InterruptedException {
        LongAdder tally = new LongAdder();
        CountDownLatch gate = new CountDownLatch(1);
        Thread[] chefs = startChefs(gate, () -> {
            for (int i = 0; i < ORDERS_EACH; i++) {
                tally.increment();
            }
        });
        gate.countDown();
        for (Thread chef : chefs) {
            chef.join();
        }
        return tally.sum();
    }

    /** Starts CHEFS threads that all stand on the same starting gate. */
    private static Thread[] startChefs(CountDownLatch gate, Runnable job) {
        Thread[] chefs = new Thread[CHEFS];
        for (int i = 0; i < CHEFS; i++) {
            chefs[i] = new Thread(() -> {
                try {
                    gate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                job.run();
            }, "chef-" + (i + 1));
            chefs[i].start();
        }
        return chefs;
    }
}
