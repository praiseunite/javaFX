package com.aptech.l4;

import java.util.List;

/**
 * Lab Task L4 — Download Manager.   <<< THIS IS THE FILE YOU COMPLETE >>>
 *
 * Replace each TODO with real code. Run SelfCheck after each method (aim: 7 passed).
 * Then run Main to watch your downloads run at the same time.
 *
 * Author: YOUR NAME HERE
 */
public final class DownloadManager {

    private DownloadManager() { }

    /**
     * R1 — Returns a NEW thread (do NOT start it) named  "dl-" + fileName.
     * Its job: for i = 1..chunks, sleep msPerChunk, then add  fileName + " chunk " + i + "/" + chunks  to the log.
     * If the sleep is interrupted: add  fileName + " cancelled"  to the log and return (stop).
     * Hint: build a Runnable with a lambda, then  return new Thread(job, "dl-" + fileName);
     */
    public static Thread createDownload(String fileName, int chunks, int msPerChunk, List<String> log) {
        // TODO R1
        return null;
    }

    /** R2 — Starts every thread in the list. (Remember: start(), never run().) */
    public static void startAll(List<Thread> threads) {
        // TODO R2
    }

    /** R3 — Waits until EVERY thread in the list has finished. Hint: join() */
    public static void waitForAll(List<Thread> threads) throws InterruptedException {
        // TODO R3
    }

    /** R4 — Returns how many threads in the list are still alive. Hint: isAlive() */
    public static int countAlive(List<Thread> threads) {
        // TODO R4
        return -1;
    }

    /**
     * R5 — Politely stops a download: interrupt it, wait AT MOST waitMs for it to finish (join with a time limit),
     * then return true if it is no longer alive.
     */
    public static boolean cancel(Thread t, long waitMs) throws InterruptedException {
        // TODO R5
        return false;
    }

    /**
     * R6 — Creates, configures and STARTS a thread named "reporter" that repeats:
     *      report.run();  then sleep periodMs   ... until it is interrupted.
     * It must be a DAEMON thread, so it never keeps the program alive. Return the thread.
     * Careful: setDaemon(true) must be called BEFORE start().
     */
    public static Thread startReporter(Runnable report, long periodMs) {
        // TODO R6
        return null;
    }

    /**
     * R7 — Starts 'job' on a new VIRTUAL thread with the given name and returns it.
     * Hint: Thread.ofVirtual().name(name).start(job)
     */
    public static Thread startVirtual(String name, Runnable job) {
        // TODO R7
        return null;
    }
}
