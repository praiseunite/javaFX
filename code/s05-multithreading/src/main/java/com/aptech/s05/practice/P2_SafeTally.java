package com.aptech.s05.practice;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PRACTICE 2 (Easy) — fix the race condition.
 *
 * The counter below is written three ways: broken on purpose, fixed with synchronized,
 * and fixed with an atomic class. Print all three totals and explain (to yourself, out
 * loud) why the first is wrong and the other two are right.
 */
public class P2_SafeTally {

    static final int THREADS = 4;
    static final int INCREMENTS = 100_000;
    static final int EXPECTED = THREADS * INCREMENTS;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("broken int (on purpose) -> " + broken()
                + "   (expected " + EXPECTED + " - watch it come out short)");
        System.out.println("synchronized method     -> " + safeSynchronized()
                + "   (expected " + EXPECTED + ")");
        System.out.println("AtomicInteger           -> " + safeAtomic()
                + "   (expected " + EXPECTED + ")");
        System.out.println();
        System.out.println("Rule of thumb: one variable -> atomic class. Several that must change");
        System.out.println("together -> synchronized, so no other thread ever sees a half-finished change.");
    }

    // ---- 1. the broken version: nothing protects the field -------------------
    static class Plain {
        int count = 0;

        void add() {
            // The same three steps as count++, written out. The Thread.yield() is NOT a fix
            // and NOT needed in real code: it only holds the window between READ and WRITE
            // open so that the lost update shows up on every run instead of one run in ten.
            int current = count;        // READ
            Thread.yield();             // (demonstration aid)
            count = current + 1;        // WRITE
        }
    }

    static int broken() throws InterruptedException {
        Plain tally = new Plain();
        run(THREADS, () -> tally.add());
        return tally.count;
    }

    // ---- 2. the synchronized version -----------------------------------------
    static class Guarded {
        int count = 0;
        synchronized void add() { count++; }
    }

    static int safeSynchronized() throws InterruptedException {
        Guarded tally = new Guarded();
        run(THREADS, () -> tally.add());
        return tally.count;
    }

    // ---- 3. the atomic version -----------------------------------------------
    static int safeAtomic() throws InterruptedException {
        AtomicInteger tally = new AtomicInteger();
        run(THREADS, () -> tally.incrementAndGet());
        return tally.get();
    }

    /** Starts the given thread count at the same instant, each performing INCREMENTS additions. */
    static void run(int threads, Runnable addOne) throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);      // so they really do collide
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int n = 0; n < INCREMENTS; n++) {
                    addOne.run();
                }
            });
            ts[i].start();
        }
        startGate.countDown();
        for (Thread t : ts) {
            t.join();
        }
    }
}
