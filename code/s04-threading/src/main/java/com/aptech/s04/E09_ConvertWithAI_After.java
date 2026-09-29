package com.aptech.s04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example 9b — The SAME program after converting to virtual threads (the AI's suggestion, reviewed by us).
 * Only the lines marked CHANGED are different from Example 9a.
 */
public class E09_ConvertWithAI_After {

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
        // CHANGED: one cheap virtual thread per task, instead of a fixed pool of 20 platform threads.
        // CHANGED: try-with-resources - close() waits for all tasks, so no finally/shutdown is needed.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> results = new ArrayList<>();
            for (String url : urls) {
                results.add(executor.submit(() -> check(url)));
            }
            int ok = 0;
            for (Future<String> f : results) {
                if (f.get().endsWith("OK")) ok++;
            }
            System.out.println(ok + " sites checked with virtual threads in about "
                    + (System.currentTimeMillis() - start) / 100 * 100 + " ms");
        }
    }
}
