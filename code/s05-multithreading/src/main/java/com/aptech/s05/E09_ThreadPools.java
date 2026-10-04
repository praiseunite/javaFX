package com.aptech.s05;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Thread pools: stop creating a new Thread for every little job.
 *
 * A pool keeps a few threads alive and reuses them, so you pay the cost of
 * creating threads once. Then the modern twist: for jobs that mostly WAIT
 * (network calls, file reads, database queries) one virtual thread per job is
 * both simpler and far more scalable.
 */
public class E09_ThreadPools {

    public static void main(String[] args) throws Exception {
        System.out.println("A fixed pool of 3 workers handles 10 jobs");
        ExecutorService pool = Executors.newFixedThreadPool(3);

        List<Future<Integer>> pending = new ArrayList<>();
        for (int job = 1; job <= 10; job++) {
            int n = job;
            pending.add(pool.submit(() -> {
                Thread.sleep(50);                 // pretend each job takes a little time
                return n * n;
            }));
        }

        pool.shutdown();                          // accept no new jobs; finish the running ones
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            pool.shutdownNow();                   // last resort if a job refuses to finish
        }

        List<Integer> squares = new ArrayList<>();
        for (Future<Integer> f : pending) {
            squares.add(f.get());                 // f.get() waits for that job's result
        }
        System.out.println("   results: " + squares + "   (in submission order, whatever order they ran in)");
        System.out.println("   pool is shut down: " + pool.isShutdown());
        System.out.println();

        System.out.println("One virtual thread per job (JDK 21+) - for jobs that mostly WAIT");
        List<Future<Boolean>> ranOnVirtualThread = new ArrayList<>();
        try (ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int job = 1; job <= 5; job++) {
                ranOnVirtualThread.add(virtual.submit(() -> {
                    Thread.sleep(20);
                    return Thread.currentThread().isVirtual();
                }));
            }
        }                                         // close() waits for every job, like shutdown() + awaitTermination()
        boolean allVirtual = true;
        for (Future<Boolean> f : ranOnVirtualThread) {
            allVirtual = allVirtual && f.get();
        }
        System.out.println("   5 jobs, every one on a virtual thread: " + allVirtual);
        System.out.println();

        System.out.println("CompletableFuture - a promise of a result that arrives later");
        CompletableFuture<String> order =
                CompletableFuture.supplyAsync(() -> "croissant")     // runs on a pool thread
                        .thenApply(String::toUpperCase)              // change the result when it arrives
                        .thenApply(dish -> dish + " + coffee");      // chain the next step
        System.out.println("   chained result: " + order.get());
        System.out.println();
        System.out.println("Ask for a pool, or one virtual thread per task, and let Java keep the");
        System.out.println("books. New code should almost never call new Thread(...) directly.");
    }
}
