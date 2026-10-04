package com.aptech.a3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A3 — Concurrent File Processor: the demo program. GIVEN complete — the threading work
 * is in ConcurrentFileProcessor.
 *
 * Run this after SelfCheck passes, to watch four log files being processed at the same
 * time and to compare a pool against a virtual thread per file.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        List<String> files = List.of("access.log", "error.log", "app.log", "audit.log");
        int linesEach = 4;
        int delayMs = 150;

        System.out.println("=== Concurrent File Processor ===");
        System.out.println("Files: " + files);
        System.out.println("Each file: " + linesEach + " lines, " + delayMs + " ms per line");
        System.out.println();

        System.out.println("1) Workers on hand-made threads:");
        Map<String, Integer> byHand = new ConcurrentHashMap<>();
        List<Thread> workers = new ArrayList<>();
        for (String file : files) {
            workers.add(ConcurrentFileProcessor.createWorker(file, linesEach, delayMs, byHand));
        }
        long start = System.currentTimeMillis();
        ConcurrentFileProcessor.startAll(workers);
        System.out.println("   right after startAll(), alive workers = "
                + ConcurrentFileProcessor.countAlive(workers));
        ConcurrentFileProcessor.waitForAll(workers);
        long handTook = System.currentTimeMillis() - start;
        System.out.println("   " + byHand);
        System.out.println("   total lines = " + ConcurrentFileProcessor.totalLines(byHand)
                + " in " + handTook + " ms   (one after another would take "
                + (files.size() * linesEach * delayMs) + " ms)");
        System.out.println();

        System.out.println("2) The same work on a pool of 3 threads:");
        start = System.currentTimeMillis();
        Map<String, Integer> pooled = ConcurrentFileProcessor.processAll(files, linesEach, delayMs, 3);
        System.out.println("   " + pooled);
        System.out.println("   total lines = " + ConcurrentFileProcessor.totalLines(pooled)
                + " in " + (System.currentTimeMillis() - start) + " ms");
        System.out.println("   (4 files on 3 workers: 3 run at once, the 4th waits for a free worker,");
        System.out.println("    so this is slower than 4 hand-made threads - that is a pool doing its job.)");
        System.out.println();

        System.out.println("3) One virtual thread per file:");
        start = System.currentTimeMillis();
        Map<String, Integer> virtual = ConcurrentFileProcessor.processWithVirtualThreads(files, linesEach, delayMs);
        System.out.println("   " + virtual);
        System.out.println("   total lines = " + ConcurrentFileProcessor.totalLines(virtual)
                + " in " + (System.currentTimeMillis() - start) + " ms");
        System.out.println();

        System.out.println("4) Who finished first?");
        System.out.println("   " + ConcurrentFileProcessor.firstFinished(files, 100));
        System.out.println();
        System.out.println("Every count above must match, and the program must exit on its own:");
        System.out.println("nothing may be left running.");
    }
}
