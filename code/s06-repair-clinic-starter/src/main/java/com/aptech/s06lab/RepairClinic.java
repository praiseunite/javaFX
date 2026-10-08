package com.aptech.s06lab;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE REPAIR CLINIC — this is the file you repair.   <<< EDIT ONLY THIS FILE >>>
 *
 * Somebody left this working code behind, and it compiles, runs and produces output that
 * looks almost right. It has FIVE faults, one from each session you have done so far:
 *
 *   F1  Session 1 — the wrong collection for the job
 *   F2  Session 2 — a wildcard that is not really a wildcard, plus a blind cast
 *   F3  Session 3 — a resource that is never closed
 *   F4  Session 4 — a question about threads answered the wrong way
 *   F5  Session 5 — work that is not actually happening at the same time
 *
 * The method before each fault is a comment naming the session, so you know where to look.
 * Run SelfCheck now: it will report 0 passed, 5 failed. Repair ONE fault, run SelfCheck
 * again, and keep going until it says 5 passed, 0 failed.
 *
 * Rules: do not change SelfCheck.java or Main.java. Do not change the method signatures.
 * If SelfCheck stands still for more than a few minutes, read the FAIL line — it names the
 * method and tells you what it expected.
 */
public final class RepairClinic {

    private RepairClinic() { }

    /**
     * F1 — Session 1. Returns the students on the register, each one listed ONCE, in the
     * order they were first seen. SelfCheck passes in a list with repeats in it.
     */
    public static List<String> distinctStudents(List<String> ids) {
        // FAULT: an ArrayList keeps every duplicate, so the register double-counts the
        // students who signed in more than once.
        List<String> result = new ArrayList<>();
        for (String id : ids) {
            result.add(id);
        }
        return result;
    }

    /**
     * F2 — Session 2. Adds up the marks, whatever kind of number they are.
     * SelfCheck passes in a List of Integer AND a List of Double.
     */
    public static long sumMarks(List<? extends Number> marks) {
        // FAULT: the signature is right and the body throws it away - this cast only
        // works while every caller happens to send integers.
        long total = 0;
        for (Number mark : marks) {
            total += (Integer) mark;
        }
        return total;
    }

    /**
     * F3 — Session 3. Writes the report lines to a file.
     * SelfCheck reads the file straight back and counts the lines.
     */
    public static void saveReport(Path file, List<String> lines) throws IOException {
        // FAULT: the writer is never closed, so the text is still in its buffer when the
        // method returns and the file on disk is empty.
        FileWriter writer = new FileWriter(file.toFile());
        for (String line : lines) {
            writer.write(line);
            writer.write(System.lineSeparator());
        }
    }

    /**
     * F4 — Session 4. Returns how many workers are running RIGHT NOW.
     * SelfCheck asks before they start, just after they start, and after they finish.
     */
    public static int countAlive(List<Thread> workers) {
        // FAULT: "is in the list" is not the same as "is running right now".
        return workers.size();
    }

    /**
     * F5 — Session 5. Processes every file and returns a tally of file name to line count.
     * The SelfCheck gives 3 files, 20 lines each and 20 ms per line: done one after another
     * that is 1200 ms, and done at the same time about 400 ms.
     */
    public static Map<String, Integer> processAll(List<String> files, int linesEach, long lineMs)
            throws Exception {
        // FAULT: everything happens on the calling thread, one file after another.
        // There is no pool, no workers and no overlap - so nothing is concurrent at all.
        Map<String, Integer> tally = new HashMap<>();
        for (String file : files) {
            int lines = 0;
            for (int i = 0; i < linesEach; i++) {
                Thread.sleep(lineMs);
                lines++;
            }
            tally.put(file, lines);
        }
        return tally;
    }
}
