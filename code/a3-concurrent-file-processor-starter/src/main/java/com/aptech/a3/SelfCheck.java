package com.aptech.a3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A3 SELF-CHECK — run this class to test your ConcurrentFileProcessor. Aim for all PASS.
 *
 * It runs your code the way the office would: several files at once, one shared tally,
 * and a check that nothing is left running afterwards.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    interface Test { void run() throws Exception; }

    public static void main(String[] args) {
        System.out.println("=== A3 Self-Check ===\n");

        run("R1 createWorker() returns a NEW thread named proc-<file>", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>();
            Thread t = ConcurrentFileProcessor.createWorker("app.log", 3, 10, tally);
            check(t != null, "createWorker returned null");
            check(t.getState() == Thread.State.NEW,
                    "the thread must not be started yet (state was " + t.getState() + ")");
            check("proc-app.log".equals(t.getName()),
                    "name should be proc-app.log but was " + t.getName());
        });

        run("R1 one worker counts every line of its file", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>();
            Thread t = ConcurrentFileProcessor.createWorker("app.log", 4, 10, tally);
            t.start();
            t.join(2000);
            check(Integer.valueOf(4).equals(tally.get("app.log")),
                    "expected 4 lines for app.log, tally was " + tally);
        });

        run("R2+R3 startAll() then waitForAll() processes three files", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>();
            List<Thread> workers = new ArrayList<>();
            for (String file : new String[]{"a.log", "b.log", "c.log"}) {
                workers.add(ConcurrentFileProcessor.createWorker(file, 5, 20, tally));
            }
            ConcurrentFileProcessor.startAll(workers);
            ConcurrentFileProcessor.waitForAll(workers);
            check(tally.size() == 3, "three files should be in the tally, found " + tally.size());
            check(ConcurrentFileProcessor.totalLines(tally) == 15,
                    "total should be 15 lines, found " + ConcurrentFileProcessor.totalLines(tally));
        });

        run("R3 the work really overlapped", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>();
            List<Thread> workers = new ArrayList<>();
            for (String file : new String[]{"a.log", "b.log", "c.log"}) {
                workers.add(ConcurrentFileProcessor.createWorker(file, 2, 100, tally));
            }
            long start = System.currentTimeMillis();
            ConcurrentFileProcessor.startAll(workers);
            ConcurrentFileProcessor.waitForAll(workers);
            long took = System.currentTimeMillis() - start;
            check(ConcurrentFileProcessor.totalLines(tally) == 6,
                    "the six lines were never counted (tally " + tally + ") - nothing ran");
            check(took < 500, "3 workers of 200 ms took " + took + " ms - are they running at the same time?");
        });

        run("R4 countAlive() follows the workers' life", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>();
            List<Thread> workers = List.of(
                    ConcurrentFileProcessor.createWorker("p.log", 3, 100, tally),
                    ConcurrentFileProcessor.createWorker("q.log", 3, 100, tally));
            check(ConcurrentFileProcessor.countAlive(workers) == 0,
                    "threads that were never started are not alive");
            ConcurrentFileProcessor.startAll(workers);
            check(ConcurrentFileProcessor.countAlive(workers) == 2,
                    "both workers should be alive right after startAll(), found "
                            + ConcurrentFileProcessor.countAlive(workers));
            ConcurrentFileProcessor.waitForAll(workers);
            check(ConcurrentFileProcessor.countAlive(workers) == 0,
                    "no worker should be alive after waitForAll(), found "
                            + ConcurrentFileProcessor.countAlive(workers));
        });

        run("R5 totalLines() adds up the tally", () -> {
            Map<String, Integer> tally = new ConcurrentHashMap<>(Map.of("a", 3, "b", 7, "c", 10));
            check(ConcurrentFileProcessor.totalLines(tally) == 20,
                    "3 + 7 + 10 = 20, got " + ConcurrentFileProcessor.totalLines(tally));
            check(ConcurrentFileProcessor.totalLines(new ConcurrentHashMap<>()) == 0,
                    "an empty tally totals 0");
        });

        run("R6 processAll() on a pool of 3 gives an exact tally", () -> {
            List<String> files = List.of("one.log", "two.log", "three.log", "four.log", "five.log");
            Map<String, Integer> tally = ConcurrentFileProcessor.processAll(files, 100, 1, 3);
            check(tally != null, "processAll returned null");
            check(tally.size() == 5, "expected 5 files in the tally, found " + tally.size());
            for (String file : files) {
                check(Integer.valueOf(100).equals(tally.get(file)),
                        file + " should have 100 lines but has " + tally.get(file));
            }
            check(ConcurrentFileProcessor.totalLines(tally) == 500,
                    "expected 500 lines, found " + ConcurrentFileProcessor.totalLines(tally));
        });

        run("R6 processAll() finishes the work and shuts its pool down", () -> {
            long start = System.currentTimeMillis();
            Map<String, Integer> tally = ConcurrentFileProcessor.processAll(List.of("x.log", "y.log"), 5, 10, 2);
            long took = System.currentTimeMillis() - start;
            check(ConcurrentFileProcessor.totalLines(tally) == 10,
                    "the two files were not processed (tally " + tally + ")");
            check(took < 2000, "processAll took " + took + " ms - is the pool being shut down?");
            check(noPoolThreadsAlive(), "pool threads are still running after processAll returned");
        });

        run("R7 processWithVirtualThreads() gives the same exact tally", () -> {
            List<String> files = List.of("a.log", "b.log", "c.log");
            Map<String, Integer> tally = ConcurrentFileProcessor.processWithVirtualThreads(files, 50, 1);
            check(tally != null, "processWithVirtualThreads returned null");
            check(tally.size() == 3, "expected 3 files, found " + tally.size());
            check(ConcurrentFileProcessor.totalLines(tally) == 150,
                    "expected 150 lines, found " + ConcurrentFileProcessor.totalLines(tally));
        });

        run("R8 firstFinished() returns the file that finished first", () -> {
            String first = ConcurrentFileProcessor.firstFinished(
                    List.of("quick.log", "slow.log", "slowest.log"), 100);
            check("quick.log".equals(first), "expected quick.log but got " + first);
        });

        run("R9 the tally survives heavy contention (no lost updates)", () -> {
            List<String> files = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                files.add("f" + i + ".log");
            }
            Map<String, Integer> tally = ConcurrentFileProcessor.processAll(files, 50_000, 0, 4);
            check(ConcurrentFileProcessor.totalLines(tally) == 300_000,
                    "expected 300000 lines with nothing lost, found "
                            + ConcurrentFileProcessor.totalLines(tally));
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0
                ? "All requirements met - great work!"
                : "Fix the FAILs above, then run SelfCheck again.");
    }

    /** True when no worker-pool thread is still alive (an un-closed pool keeps the program running). */
    private static boolean noPoolThreadsAlive() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.isAlive() && !t.isDaemon() && t.getName().startsWith("pool-")) {
                return false;
            }
        }
        return true;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void run(String name, Test test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
