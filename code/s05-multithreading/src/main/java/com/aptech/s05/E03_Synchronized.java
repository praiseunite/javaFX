package com.aptech.s05;

import java.util.function.Consumer;

/**
 * The cure for the race condition: the intrinsic lock.
 *
 * Every Java object has a built-in lock. synchronized says "only one thread
 * at a time may hold this object's lock", which makes the READ-ADD-WRITE
 * of count++ happen as one whole step again.
 *
 * The same 20,000 is reached three ways below.
 */
public class E03_Synchronized {

    static class Tally {
        int count = 0;
        private final Object lock = new Object();     // our own private lock object

        // 1. synchronized on the method: locks the object you called it on (this)
        synchronized void addWithMethod() {
            count++;
        }

        // 2. synchronized on a block: lock only the few lines that need it
        void addWithBlock() {
            synchronized (this) {
                count++;
            }
        }

        // 3. synchronized on a private lock object - the style experts recommend,
        //    because nobody outside this class can accidentally lock it
        void addWithLockObject() {
            synchronized (lock) {
                count++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("synchronized method       -> " + race(Tally::addWithMethod));
        System.out.println("synchronized (this) block -> " + race(Tally::addWithBlock));
        System.out.println("private lock object       -> " + race(Tally::addWithLockObject));
        System.out.println("Expected every time:        20000");
        System.out.println();
        System.out.println("Same two threads, same 20,000 increments - but now no update is ever lost.");
    }

    /** Runs the given action 10,000 times on each of two threads and returns the tally. */
    static int race(Consumer<Tally> action) throws InterruptedException {
        Tally tally = new Tally();
        Runnable job = () -> {
            for (int i = 0; i < 10_000; i++) {
                action.accept(tally);
            }
        };
        Thread ada = new Thread(job, "Chef Ada");
        Thread ben = new Thread(job, "Chef Ben");
        ada.start();
        ben.start();
        ada.join();
        ben.join();
        return tally.count;
    }
}
