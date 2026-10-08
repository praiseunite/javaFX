package com.aptech.s06.drills;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * DRILL 5a — the standoff. Two threads take the same two locks in opposite orders,
 * so each one ends up holding what the other needs. Neither can ever move again.
 *
 * This is a real deadlock, and a real deadlock never ends by itself — so the JVM's own
 * watchdog reports it and then stops the program. Without the watchdog this class would
 * hang your IntelliJ window forever, which is exactly what happens in production.
 *
 * The fix is in D5_FixedLockOrder.java: agree on ONE order and take the locks that way
 * everywhere. Nothing else is required — no timeouts, no retries, no sleeping.
 */
public class D5_LockOrderDeadlock {

    private static final Object POT = new Object();
    private static final Object SPOON = new Object();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Drill 5a - the standoff (this one really does deadlock)");
        System.out.println();

        Thread ada = new Thread(() -> cook("Chef Ada", "pot", POT, "spoon", SPOON), "Chef Ada");
        Thread ben = new Thread(() -> cook("Chef Ben", "spoon", SPOON, "pot", POT), "Chef Ben");

        // The watchdog. Without it this program hangs forever.
        Thread watchdog = new Thread(() -> {
            sleep(1500);

            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] stuck = bean.findDeadlockedThreads();
            if (stuck == null) {
                System.out.println("Watchdog: no deadlock found after 1500 ms.");
                return;
            }

            System.out.println();
            System.out.println("Watchdog: DEADLOCK - " + stuck.length + " threads will never move again:");
            for (ThreadInfo info : bean.getThreadInfo(stuck)) {
                if (info != null) {
                    System.out.println("  " + info.getThreadName() + " is " + info.getThreadState()
                            + ", waiting for the lock held by " + info.getLockOwnerName());
                }
            }
            System.out.println();
            System.out.println("Ada waits for the spoon, Ben waits for the pot. Neither will let go.");
            System.out.println("The JVM can see it, but it cannot break the cycle - so we stop here.");
            System.exit(0);
        }, "watchdog");
        watchdog.setDaemon(true);

        ada.start();
        ben.start();
        watchdog.start();

        ada.join();                 // these never return, so only the watchdog can end the run
        ben.join();
        System.out.println("Both chefs finished - no deadlock this run.");
    }

    /** Reach for the first utensil, then the second, then cook. */
    private static void cook(String chef, String first, Object firstLock,
                             String second, Object secondLock) {
        System.out.println(chef + " picks up the " + first + ".");
        synchronized (firstLock) {
            sleep(300);             // gives the other chef time to pick up the other utensil
            System.out.println(chef + " now needs the " + second + " ...");
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
