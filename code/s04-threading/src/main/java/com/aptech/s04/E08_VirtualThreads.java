package com.aptech.s04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example 8 — Virtual threads (Java 21+): cheap, lightweight threads for work that WAITS a lot.
 */
public class E08_VirtualThreads {

    /** A task that mostly waits, like calling a web service or a database. */
    static void callSlowService() {
        try {
            Thread.sleep(1000);                         // 1 second of waiting
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1) Creating ONE virtual thread
        Thread v = Thread.ofVirtual().name("virtual-1").start(
                () -> System.out.println("Hello from " + Thread.currentThread().getName()
                        + " (virtual? " + Thread.currentThread().isVirtual() + ")"));
        v.join();

        // 2) 10,000 tasks, each waiting 1 second, all at the same time
        long start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            threads.add(Thread.ofVirtual().start(E08_VirtualThreads::callSlowService));
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("10,000 virtual threads finished in about "
                + Math.round((System.currentTimeMillis() - start) / 1000.0) + " second(s)");

        // 3) The same with an ExecutorService (the style you will use most often)
        start = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                executor.submit(E08_VirtualThreads::callSlowService);
            }
        }                                               // close() waits for every task to finish
        System.out.println("Executor with virtual threads: about "
                + Math.round((System.currentTimeMillis() - start) / 1000.0) + " second(s)");

        // 4) For comparison: a pool of 100 ordinary (platform) threads needs 100 rounds
        start = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < 1_000; i++) {           // only 1,000 tasks, otherwise we'd wait 100 s!
                pool.submit(E08_VirtualThreads::callSlowService);
            }
        }
        System.out.println("1,000 tasks on 100 platform threads: about "
                + Math.round((System.currentTimeMillis() - start) / 1000.0) + " second(s)");
    }
}
