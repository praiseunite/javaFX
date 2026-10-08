package com.aptech.s06.drills;

/**
 * DRILL 4 — the frozen app.
 *
 * The bug: a synchronized block is used to protect a shared counter, and then
 * Thread.sleep() is called INSIDE it. sleep() does not release the lock, so every other
 * cook queues behind the sleeper and four independent jobs run one after another.
 *
 * This drill is the whole point of "lock the data, not the time": the lock was doing its
 * job perfectly. It was asked to protect 300 ms of doing nothing.
 *
 * Both halves do exactly the same amount of work and take exactly the same 300 ms each.
 * The only difference is where the sleep sits relative to the lock. The two timings below
 * are measured on this machine, so your numbers will differ — the gap will not.
 */
public class D4_SleepUnderLock {

    private static final int COOKS = 4;
    private static final int BAKE_MS = 300;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Drill 4 - the frozen app");
        System.out.println(COOKS + " cooks, each needing " + BAKE_MS + " ms of work.");
        System.out.println("Doing it at the same time should cost about " + BAKE_MS + " ms, not "
                + (BAKE_MS * COOKS) + " ms.");
        System.out.println();

        long locked = bakeAll(true);
        long free = bakeAll(false);

        System.out.println("BROKEN - the sleep is INSIDE the lock");
        System.out.println("  four cooks finished in " + locked + " ms");
        System.out.println("  nobody crashed and the answer is correct - it is just serialised");
        System.out.println();
        System.out.println("FIXED - the sleep is OUTSIDE the lock");
        System.out.println("  four cooks finished in " + free + " ms");
        System.out.println();
        System.out.println("Same work, same four cooks. A lock protects data, not time:");
        System.out.println("do the slow work first, then lock only the few lines that touch");
        System.out.println("the shared value.");
    }

    private static long bakeAll(boolean sleepInsideLock) throws InterruptedException {
        Thread[] cooks = new Thread[COOKS];
        long start = System.currentTimeMillis();

        for (int i = 0; i < COOKS; i++) {
            String name = "Cook " + (i + 1);
            boolean locked = sleepInsideLock;
            cooks[i] = new Thread(() -> {
                try {
                    if (locked) {
                        Bake.slow();
                    } else {
                        Bake.fast();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, name);
        }
        for (Thread cook : cooks) {
            cook.start();
        }
        for (Thread cook : cooks) {
            cook.join();
        }
        return System.currentTimeMillis() - start;
    }

    /** One shared oven: a lock to guard the count of finished bakes, and 300 ms of work. */
    static final class Bake {

        private static final Object OVEN = new Object();
        private static int finished = 0;

        /** BROKEN: takes the lock, then sleeps while still holding it. */
        static void slow() throws InterruptedException {
            synchronized (OVEN) {
                Thread.sleep(BAKE_MS);      // the lock is held for the whole 300 ms
                finished++;
            }
        }

        /** FIXED: does the slow work first, then locks only to update the shared count. */
        static void fast() throws InterruptedException {
            Thread.sleep(BAKE_MS);          // out here nobody is blocked by it
            synchronized (OVEN) {
                finished++;                 // locked for microseconds, which is all it needs
            }
        }
    }
}
