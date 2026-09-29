package com.aptech.s04;

/**
 * Example 4 — The six thread states, observed one by one with getState().
 * (Generous pauses make each state easy to catch.)
 */
public class E04_ThreadStates {

    private static final Object LOCK = new Object();
    private static volatile boolean keepSpinning = true;

    public static void main(String[] args) throws InterruptedException {
        // 1) NEW: created, but start() not called yet
        Thread spinner = new Thread(() -> {
            while (keepSpinning) { /* busy working */ }
        });
        System.out.println("After new Thread():      " + spinner.getState());

        // 2) RUNNABLE: started and working (or ready to work)
        spinner.start();
        Thread.sleep(100);
        System.out.println("While working:           " + spinner.getState());
        keepSpinning = false;
        spinner.join();

        // 3) TIMED_WAITING: sleeping for a fixed time
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { /* woken early */ }
        });
        sleeper.start();
        Thread.sleep(100);
        System.out.println("During sleep(1000):      " + sleeper.getState());
        sleeper.interrupt();                           // wake it up early
        sleeper.join();

        // 4) WAITING: waiting with NO time limit (here: join on a thread that sleeps)
        Thread slow = new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { /* ignore */ }
        });
        Thread waiter = new Thread(() -> {
            try { slow.join(); } catch (InterruptedException e) { /* ignore */ }
        });
        slow.start();
        waiter.start();
        Thread.sleep(100);
        System.out.println("During join() (no limit): " + waiter.getState());
        waiter.join();

        // 5) BLOCKED: waiting to enter a synchronized block that another thread holds
        Thread blocked = new Thread(() -> {
            synchronized (LOCK) { /* can only get in when main lets go */ }
        });
        synchronized (LOCK) {                          // main holds the lock...
            blocked.start();
            Thread.sleep(100);
            System.out.println("Waiting for a lock:      " + blocked.getState());
        }                                              // ...and lets go here
        blocked.join();

        // 6) TERMINATED: run() has finished
        System.out.println("After run() ends:        " + blocked.getState());
    }
}
