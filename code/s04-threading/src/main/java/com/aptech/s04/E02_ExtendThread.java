package com.aptech.s04;

/**
 * Example 2 — Way 1 to create a thread: EXTEND the Thread class and override run().
 */
public class E02_ExtendThread {

    /** A thread that counts to 3. run() holds the job it will do. */
    static class Counter extends Thread {
        Counter(String name) {
            super(name);                               // give the thread a name
        }

        @Override
        public void run() {
            for (int i = 1; i <= 3; i++) {
                System.out.println(getName() + " says " + i);
                try {
                    Thread.sleep(100);                 // pause 100 ms so the threads take turns
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Counter a = new Counter("Ada");
        Counter b = new Counter("Ben");

        a.start();                                     // start() = run() in a NEW thread
        b.start();
        System.out.println("main started both threads and carries on");

        a.join();                                      // wait for Ada to finish
        b.join();                                      // wait for Ben to finish
        System.out.println("Both threads finished");

        // The classic mistake: run() instead of start() does NOT create a thread
        Counter c = new Counter("Chi");
        System.out.println("Calling run() directly...");
        c.run();                                       // runs in the MAIN thread, like any method call
        System.out.println("...main had to wait for Chi, because no new thread was created");
    }
}
