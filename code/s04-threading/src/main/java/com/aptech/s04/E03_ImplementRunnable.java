package com.aptech.s04;

/**
 * Example 3 — Way 2 (preferred): IMPLEMENT Runnable (the job) and hand it to a Thread (the worker).
 * Way 3: the same thing written as a lambda.
 */
public class E03_ImplementRunnable {

    /** The JOB: what should be done. It is not a thread by itself. */
    static class Greeting implements Runnable {
        private final String message;

        Greeting(String message) { this.message = message; }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ": " + message);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Greeting("Good morning!"), "worker-1");   // job + name
        t1.start();
        t1.join();                                     // wait, so the output stays in order

        Runnable job = () -> System.out.println(Thread.currentThread().getName() + ": Hello from a lambda!");
        Thread t2 = new Thread(job, "worker-2");
        t2.start();
        t2.join();

        // One job can be given to several workers
        Greeting shared = new Greeting("same job, different worker");
        Thread t3 = new Thread(shared, "worker-3");
        Thread t4 = new Thread(shared, "worker-4");
        t3.start();
        t3.join();
        t4.start();
        t4.join();

        System.out.println(Thread.currentThread().getName() + ": all done");
    }
}
