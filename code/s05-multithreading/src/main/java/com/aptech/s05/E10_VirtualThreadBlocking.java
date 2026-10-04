package com.aptech.s05;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI-driven detection of thread blocking in virtual threads.
 *
 * Many virtual threads share a small pool of carrier threads. A virtual thread
 * that waits normally releases its carrier, so tens of thousands can be in
 * flight at once. But a virtual thread that is stuck - in a long synchronized
 * block, or burning CPU in a tight loop - keeps its carrier, and the jobs
 * behind it starve.
 *
 * Any tool that finds the culprit - including an AI assistant asked to
 * diagnose a hung application - works from evidence: each job reports what it
 * is doing, and a watchdog samples that evidence and prints a report. This
 * program is that watchdog in miniature.
 */
public class E10_VirtualThreadBlocking {

    /** Every job writes its current state here, so the watchdog can see the whole kitchen. */
    static final Map<String, String> state = new ConcurrentHashMap<>();

    static final List<String> JOBS = List.of("Ada (holding the oven)", "Ben", "Chi", "Dayo");

    public static void main(String[] args) throws Exception {
        Object oven = new Object();
        CountDownLatch adaIsInTheOven = new CountDownLatch(1);

        System.out.println("The AI was asked: \"which virtual threads are stuck, and on what?\"");
        System.out.println("Here is the evidence a watchdog collects to answer it:");
        System.out.println();

        try (ExecutorService kitchen = Executors.newVirtualThreadPerTaskExecutor()) {
            // Ada goes first and holds the oven for 700 ms. We only send the others in
            // once she is really inside, so the queue below is guaranteed, not a coin toss.
            kitchen.submit(() -> {
                state.put(JOBS.get(0), "blocked - waiting for the oven");
                synchronized (oven) {
                    state.put(JOBS.get(0), "WORKING - in the oven (700 ms)");
                    adaIsInTheOven.countDown();
                    sleep(700);
                }
                state.put(JOBS.get(0), "done");
            });

            adaIsInTheOven.await();                 // Ada has the oven; now the queue forms

            for (String name : JOBS.subList(1, JOBS.size())) {
                kitchen.submit(() -> useOven(name, oven, 50));
            }

            Thread.sleep(200);
            report("200 ms in - Ada is still in the oven");
            Thread.sleep(900);
            report("1100 ms in - everyone is finished");
        }

        System.out.println("Every job finished. Nothing is stuck any more.");
        System.out.println();
        System.out.println("Read the report the way a tool does: one thread is WORKING and holding the");
        System.out.println("lock, the rest are BLOCKED on that same lock. That is the answer to \"what is");
        System.out.println("blocking what?\" - and it is the claim you check an AI's answer against");
        System.out.println("before you change anything. Real tools print exactly the same facts: the");
        System.out.println("IntelliJ thread dump, jcmd Thread.dump_to_file, and Java Flight Recorder.");
    }

    /** Try to take the oven, report what we are doing, work, then report that we are done. */
    static void useOven(String name, Object oven, long ms) {
        state.put(name, "blocked - waiting for the oven");
        synchronized (oven) {                       // a monitor: one holder at a time
            state.put(name, "WORKING - in the oven (" + ms + " ms)");
            sleep(ms);
        }
        state.put(name, "done");
    }

    /** The watchdog's sample: print every job's latest report, sorted so it reads the same each time. */
    static void report(String when) {
        System.out.println("--- " + when + " ---");
        TreeMap<String, String> sorted = new TreeMap<>(state);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            System.out.printf("  %-22s : %s%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
