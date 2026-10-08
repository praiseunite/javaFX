package com.aptech.s06.practice;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PRACTICE 2 (Medium) — the same job, two correct fixes.
 *
 * A shared counter is incremented by four threads. Write it twice:
 *
 *   - SynchronizedCounter: an int, every access guarded by synchronized. Use this shape
 *     when several fields have to change together, or when the update depends on the
 *     current value (a transfer that takes from one account and adds to another).
 *   - AtomicCounter: an AtomicLong and incrementAndGet(). Use this when a single variable
 *     is all there is. It is lock-free, and it is faster precisely because of that.
 *
 * Both must print the exact expected total. If either one is short by even a few thousand,
 * the fix is not a fix — it is a different race.
 */
public class P2_SafeCounter {

    static final int THREADS = 4;
    static final int INCREMENTS_EACH = 100_000;

    /** Fix 1: one lock around every access to the int. */
    static class SynchronizedCounter {
        private int value = 0;

        synchronized void increment() {
            value++;
        }

        synchronized int value() {
            return value;
        }
    }

    /** Fix 2: a single variable, updated indivisibly with no lock written by hand. */
    static class AtomicCounter {
        private final AtomicLong value = new AtomicLong();

        void increment() {
            value.incrementAndGet();
        }

        long value() {
            return value.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long expected = (long) THREADS * INCREMENTS_EACH;

        SynchronizedCounter sync = new SynchronizedCounter();
        AtomicCounter atomic = new AtomicCounter();

        run(sync::increment);
        run(atomic::increment);

        System.out.println("Practice 2 (Medium) - two correct fixes, same exact answer");
        System.out.println(THREADS + " threads x " + String.format("%,d", INCREMENTS_EACH)
                + " increments = " + String.format("%,d", expected) + " expected");
        System.out.println();
        System.out.printf("  synchronized int : %,d   %s%n", (long) sync.value(), verdict(sync.value(), expected));
        System.out.printf("  AtomicLong       : %,d   %s%n", atomic.value(), verdict(atomic.value(), expected));
        System.out.println();
        System.out.println("Both are correct. Choose by what you are protecting:");
        System.out.println("  one variable           -> AtomicLong / LongAdder (no lock, fastest)");
        System.out.println("  several, must agree    -> synchronized on one shared lock object");
        System.out.println("  never, in any case     -> a bare int with count++");
    }

    private static String verdict(long actual, long expected) {
        return actual == expected ? "PASS - nothing lost" : "FAIL - lost " + (expected - actual);
    }

    /** Starts THREADS threads that all sit on a starting gate, then each runs the job. */
    private static void run(Runnable job) throws InterruptedException {
        CountDownLatch gate = new CountDownLatch(1);
        Thread[] workers = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    gate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int n = 0; n < INCREMENTS_EACH; n++) {
                    job.run();
                }
            }, "worker-" + (i + 1));
            workers[i].start();
        }

        gate.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
    }
}
