package com.aptech.s04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example 9a — "Legacy" code using a fixed pool of PLATFORM threads to check 200 web addresses.
 * This is the code we will ask an AI assistant to convert to virtual threads (see Example 9b).
 */
public class E09_ConvertWithAI_Before {

    /** Pretend to check a web address: it takes 200 ms of waiting. */
    static String check(String url) throws InterruptedException {
        Thread.sleep(200);
        return url + " OK";
    }

    public static void main(String[] args) throws Exception {
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            urls.add("https://site" + i + ".example");
        }

        long start = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(20);     // only 20 workers
        try {
            List<Future<String>> results = new ArrayList<>();
            for (String url : urls) {
                results.add(pool.submit(() -> check(url)));
            }
            int ok = 0;
            for (Future<String> f : results) {
                if (f.get().endsWith("OK")) ok++;
            }
            System.out.println(ok + " sites checked with 20 platform threads in about "
                    + (System.currentTimeMillis() - start) / 100 * 100 + " ms");
        } finally {
            pool.shutdown();
        }
    }
}
