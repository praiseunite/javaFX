package com.aptech.s05;

/**
 * The same two chefs, the same two utensils, the same locks - and no deadlock.
 *
 * Nothing about the locking changed except the ORDER: every chef now reaches
 * for the pot first. Because a thread can never be holding the spoon and
 * waiting for the pot at the same time, the circle that causes a deadlock
 * cannot close.
 */
public class E07_DeadlockFixed {

    private static final Object POT = new Object();
    private static final Object SPOON = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread ada = new Thread(() -> cook("Chef Ada"), "Chef Ada");
        Thread ben = new Thread(() -> cook("Chef Ben"), "Chef Ben");

        ada.start();
        ben.start();

        ada.join();
        ben.join();

        System.out.println();
        System.out.println("Both chefs finished and the program exited on its own.");
        System.out.println("The rule: agree on one order for taking locks, and always follow it.");
    }

    /** Both chefs use the SAME order: pot first, spoon second. */
    private static void cook(String chef) {
        System.out.println(chef + " reaches for the pot.");
        synchronized (POT) {
            System.out.println(chef + ": I have the pot, now I need the spoon.");
            sleep(300);
            synchronized (SPOON) {
                System.out.println(chef + " is cooking.");
            }
            System.out.println(chef + " puts everything down.");
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
