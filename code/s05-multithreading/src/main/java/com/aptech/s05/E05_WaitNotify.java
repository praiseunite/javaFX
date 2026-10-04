package com.aptech.s05;

/**
 * wait() and notifyAll(): two threads taking turns.
 *
 * The counter holds at most ONE loaf. The customer cannot eat bread that does
 * not exist, and the baker cannot bake onto a counter that is still full - so
 * each one waits for the other. wait() gives the lock back and sleeps;
 * notifyAll() wakes the other side up.
 */
public class E05_WaitNotify {

    /** A kitchen pass that holds at most one loaf. */
    static class Bakery {
        private String bread = null;            // null means the counter is empty

        synchronized void bake(String type) throws InterruptedException {
            while (bread != null) {             // still bread on the counter: we cannot bake
                System.out.println("  Baker waits: " + bread + " is still on the counter.");
                wait();                         // give up the lock and sleep until told otherwise
            }
            bread = type;
            System.out.println("  Baker baked " + type + ".");
            notifyAll();                        // tell the customer the bread is ready
        }

        synchronized void eat() throws InterruptedException {
            while (bread == null) {             // nothing to eat yet
                System.out.println("  Customer waits: the counter is empty.");
                wait();
            }
            System.out.println("  Customer ate " + bread + ".");
            bread = null;
            notifyAll();                        // tell the baker the counter is free again
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Bakery bakery = new Bakery();

        Thread customer = new Thread(() -> {
            try {
                bakery.eat();
                bakery.eat();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "customer");

        Thread baker = new Thread(() -> {
            try {
                bakery.bake("Croissant");
                bakery.bake("Baguette");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "baker");

        customer.start();
        Thread.sleep(50);                       // let the customer reach wait() first
        baker.start();

        customer.join();
        baker.join();

        System.out.println();
        System.out.println("Both finished. Neither thread was busy-waiting: while they waited");
        System.out.println("they were asleep and used no CPU. The exact order of the lines can");
        System.out.println("differ between runs - that is the scheduler choosing, not a bug.");
    }
}
