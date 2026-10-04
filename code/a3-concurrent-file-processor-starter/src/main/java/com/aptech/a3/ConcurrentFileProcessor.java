package com.aptech.a3;

import java.util.List;
import java.util.Map;

/**
 * Assignment A3 — Concurrent File Processor.   <<< THIS IS THE FILE YOU COMPLETE >>>
 *
 * Scenario: the office receives a folder of large log files every night. Counting the
 * lines in them one after another takes far too long, so the work is split across
 * threads. Every worker adds its result to ONE shared tally, and the tally must be
 * exactly right when the last worker stops — no lines lost, nothing double counted.
 *
 * Replace every TODO with real code. Run SelfCheck after each requirement (aim: all PASS).
 * Then run Main to watch the files being processed at the same time.
 *
 * Rules for this assignment:
 *   - Never write the tally by hand (no `count++` on a shared int). Use a thread-safe
 *     class from java.util.concurrent.
 *   - New code should not create threads by hand where a pool or a virtual thread fits.
 *   - Nothing may be left running when a method returns; the program must always exit.
 *
 * Author: YOUR NAME HERE
 */
public final class ConcurrentFileProcessor {

    private ConcurrentFileProcessor() { }

    /**
     * R1 — Returns a NEW worker thread (do NOT start it) named  "proc-" + fileName.
     * Its job: for i = 1..lines, sleep delayMs, then add ONE line to the shared tally
     * for this file:  tally.merge(fileName, 1, Integer::sum).
     * If the sleep is interrupted, stop immediately (do not add anything more).
     *
     * Hint: build the job as a lambda, then  return new Thread(job, "proc-" + fileName);
     */
    public static Thread createWorker(String fileName, int lines, int delayMs, Map<String, Integer> tally) {
        // TODO R1
        return null;
    }

    /**
     * R2 — Starts every worker in the list.
     * Careful: start(), never run() — run() would do the work on the current thread.
     */
    public static void startAll(List<Thread> workers) {
        // TODO R2
    }

    /**
     * R3 — Waits until EVERY worker in the list has finished.
     * Hint: join()
     */
    public static void waitForAll(List<Thread> workers) throws InterruptedException {
        // TODO R3
    }

    /**
     * R4 — Returns how many workers in the list are still alive.
     * Hint: isAlive()
     */
    public static int countAlive(List<Thread> workers) {
        // TODO R4
        return -1;
    }

    /**
     * R5 — Returns the total number of lines across every entry in the tally.
     * Hint: a stream, or a loop over tally.values().
     */
    public static int totalLines(Map<String, Integer> tally) {
        // TODO R5
        return -1;
    }

    /**
     * R6 — Processes every file using a POOL of poolSize threads, and returns the finished tally.
     * Every file gets linesEach lines of work, each line taking delayMs.
     * The pool must be shut down before returning, and every result must be in the tally.
     *
     * Hint: Executors.newFixedThreadPool(poolSize); submit one task per file; then
     * shutdown() + awaitTermination(...) — and remember that submit() returns a Future
     * whose get() waits for that task.
     */
    public static Map<String, Integer> processAll(List<String> files, int linesEach,
                                                  int delayMs, int poolSize) throws Exception {
        // TODO R6
        return null;
    }

    /**
     * R7 — The same job, but one VIRTUAL thread per file instead of a pool.
     * Hint: Executors.newVirtualThreadPerTaskExecutor() in a try-with-resources block.
     */
    public static Map<String, Integer> processWithVirtualThreads(List<String> files, int linesEach,
                                                                int delayMs) throws Exception {
        // TODO R7
        return null;
    }

    /**
     * R8 — Returns the name of the file that FINISHES FIRST, using CompletableFuture.
     * File at position i works for (i + 1) * delayMs, so the first file in the list is
     * the quickest one. Nobody waits for the others: anyOf tells you who arrived first.
     *
     * Hint: start one CompletableFuture.supplyAsync per file (each sleeps its own time and
     * returns the file name), then CompletableFuture.anyOf(...).get() gives you the first
     * result to arrive.
     */
    public static String firstFinished(List<String> files, int delayMs) throws Exception {
        // TODO R8
        return null;
    }
}
