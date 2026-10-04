package com.aptech.s05.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * PRACTICE 6 (Challenge) — a pool and a promise working together.
 *
 * Part A prepares five dishes on a pool of three cooks and collects the results.
 * Part B does one order as a chain of CompletableFuture steps.
 *
 * Nothing creates a Thread by hand, and the pool is shut down when the work is done.
 */
public class P6_OrderPipeline {

    public static void main(String[] args) throws Exception {
        List<String> menu = List.of("Jollof rice", "Pepper soup", "Puff-puff", "Suya", "Moi moi");

        System.out.println("A pool of 3 cooks prepares 5 dishes:");
        ExecutorService kitchen = Executors.newFixedThreadPool(3);
        List<Future<String>> preparing = new ArrayList<>();
        for (String dish : menu) {
            preparing.add(kitchen.submit(() -> {
                Thread.sleep(100);                       // pretend each dish takes a moment
                return dish + " is ready";
            }));
        }
        for (Future<String> f : preparing) {
            System.out.println("  " + f.get());          // f.get() waits for that dish
        }
        kitchen.shutdown();                              // no new work; the running dishes finish
        if (!kitchen.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
            kitchen.shutdownNow();
        }

        System.out.println();
        System.out.println("The same kitchen as one promise chain (CompletableFuture):");
        CompletableFuture<String> order = CompletableFuture
                .supplyAsync(() -> "Jollof rice")        // a pool thread starts the dish
                .thenApply(String::toLowerCase)          // the pass labels it
                .thenApply(dish -> dish + " -> table 7"); // the waiter delivers it
        System.out.println("  " + order.get());

        System.out.println();
        System.out.println("Pool shut down: " + kitchen.isShutdown()
                + ". No thread was created by hand, and nothing was left running.");
    }
}
