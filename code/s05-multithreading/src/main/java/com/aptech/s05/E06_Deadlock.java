package com.aptech.s05;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * A deadlock, caught in the act.
 *
 * The JVM itself knows when threads have stopped forever, and this is the same
 * report you will see in IntelliJ's console or in a .jfr recording. Here a
 * watchdog asks the JVM for the deadlocked threads and then stops the program,
 * so we are not left staring at a frozen window.
 */
public class E06_Deadlock {

    private static final Object POT = new Object();
    private static final Object SPOON = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread ada = new Thread(() -> cook("Chef Ada", "pot", POT, "spoon", SPOON), "Chef Ada");
        Thread ben = new Thread(() -> cook("Chef Ben", "spoon", SPOON, "pot", POT), "Chef Ben");

        // The watchdog. Without it this program would hang forever.
        Thread watchdog = new Thread(() -> {
            sleep(1500);

            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] stuck = bean.findDeadlockedThreads();
            if (stuck == null) {
                System.out.println("Watchdog: no deadlock found after 1500 ms.");
                return;
            }

            System.out.println();
            System.out.println("Watchdog: DEADLOCK DETECTED - " + stuck.length + " threads will never move again:");
            for (ThreadInfo info : bean.getThreadInfo(stuck)) {
                if (info != null) {
                    System.out.println("  " + info.getThreadName() + " is " + info.getThreadState()
                            + ", waiting for the lock held by " + info.getLockOwnerName());
                }
            }
            System.out.println("The kitchen is frozen; nothing can unlock it from inside.");
            System.out.println("The watchdog stops the program instead.");
            System.exit(0);
        }, "watchdog");
        watchdog.setDaemon(true);

        ada.start();
        ben.start();
        watchdog.start();

        ada.join();                 // these two never return, so only the watchdog can end the run
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
