package com.aptech.s04.practice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Practice 6 (challenge) — "fetch" 1,000 prices with virtual threads, each result in its own array slot. */
public class P6_PriceFetcher {

    /** Pretend network call: waits 300 ms, then returns a price based on the product number. */
    static int fetchPrice(int productId) throws InterruptedException {
        Thread.sleep(300);
        return 100 + productId % 50;
    }

    public static void main(String[] args) {
        int count = 1_000;
        int[] prices = new int[count];

        long start = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int id = 0; id < count; id++) {
                final int product = id;
                executor.submit(() -> {
                    prices[product] = fetchPrice(product);   // each task writes only its own slot
                    return null;                              // return makes this a Callable, which may throw
                });
            }
        }                                                     // waits for all 1,000 tasks
        long ms = (System.currentTimeMillis() - start) / 100 * 100;       // rounded down to 100 ms

        long total = 0;
        for (int p : prices) {
            total += p;
        }
        System.out.println("Fetched " + count + " prices, total N" + total);
        System.out.println("Took about " + ms + " ms (one after another it would take about " + count * 300 / 1000 + " s)");
    }
}
