package com.aptech.l4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * L4 SELF-CHECK — run this class to test your DownloadManager. Aim for all PASS.
 * (The log lists are made thread-safe with Collections.synchronizedList — you'll learn why in Session 5.)
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    interface Test { void run() throws Exception; }

    public static void main(String[] args) {
        System.out.println("=== L4 Self-Check ===\n");

        run("R1 createDownload() returns a NEW thread with the right name", () -> {
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            Thread t = DownloadManager.createDownload("song.mp3", 2, 10, log);
            check(t != null, "createDownload returned null");
            check(t.getState() == Thread.State.NEW, "the thread must not be started yet (state was " + t.getState() + ")");
            check("dl-song.mp3".equals(t.getName()), "name should be dl-song.mp3 but was " + t.getName());
        });
        run("R1 a download logs every chunk in order", () -> {
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            Thread t = DownloadManager.createDownload("a.zip", 3, 10, log);
            t.start();
            t.join(2000);
            check(log.equals(List.of("a.zip chunk 1/3", "a.zip chunk 2/3", "a.zip chunk 3/3")), "log was " + log);
        });
        run("R2+R3 startAll() and waitForAll() run several downloads at the same time", () -> {
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            List<Thread> list = new ArrayList<>();
            for (String f : new String[]{"x", "y", "z"}) {
                list.add(DownloadManager.createDownload(f, 4, 100, log));
            }
            long start = System.currentTimeMillis();
            DownloadManager.startAll(list);
            DownloadManager.waitForAll(list);
            long took = System.currentTimeMillis() - start;
            check(log.size() == 12, "expected 12 log entries after waitForAll, found " + log.size());
            check(took < 1000, "3 downloads of 400 ms took " + took + " ms - are they really running at the same time?");
        });
        run("R4 countAlive() counts running threads", () -> {
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            List<Thread> list = List.of(DownloadManager.createDownload("p", 3, 100, log), DownloadManager.createDownload("q", 3, 100, log));
            check(DownloadManager.countAlive(list) == 0, "threads that were never started are not alive");
            DownloadManager.startAll(list);
            check(DownloadManager.countAlive(list) == 2, "both threads should be alive right after starting");
            DownloadManager.waitForAll(list);
            check(DownloadManager.countAlive(list) == 0, "no thread should be alive after waitForAll");
        });
        run("R5 cancel() stops a download early", () -> {
            List<String> log = Collections.synchronizedList(new ArrayList<>());
            Thread t = DownloadManager.createDownload("big.iso", 50, 100, log);
            t.start();
            Thread.sleep(250);
            boolean stopped = DownloadManager.cancel(t, 1000);
            check(stopped, "cancel() should return true once the thread has stopped");
            check(!t.isAlive(), "the thread is still alive after cancel()");
            check(log.contains("big.iso cancelled"), "the log should end with 'big.iso cancelled', it was " + log);
            check(log.size() < 10, "the download should have stopped early, but the log has " + log.size() + " entries");
        });
        run("R6 startReporter() makes a started daemon thread that repeats the report", () -> {
            AtomicInteger calls = new AtomicInteger();
            Thread r = DownloadManager.startReporter(calls::incrementAndGet, 50);
            check(r.isDaemon(), "the reporter must be a daemon thread");
            check("reporter".equals(r.getName()), "name should be 'reporter' but was " + r.getName());
            Thread.sleep(400);
            check(calls.get() >= 3, "the report should have run several times in 400 ms, it ran " + calls.get());
            r.interrupt();
            r.join(1000);
            check(!r.isAlive(), "the reporter should stop when interrupted");
        });
        run("R7 startVirtual() starts a named virtual thread", () -> {
            AtomicInteger ran = new AtomicInteger();
            Thread v = DownloadManager.startVirtual("vt-1", ran::incrementAndGet);
            check(v != null, "startVirtual returned null");
            v.join(1000);
            check(v.isVirtual(), "the thread must be a virtual thread");
            check("vt-1".equals(v.getName()), "name should be vt-1 but was " + v.getName());
            check(ran.get() == 1, "the job should have run exactly once");
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0 ? "All requirements met - great work!" : "Fix the FAILs above, then run SelfCheck again.");
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
