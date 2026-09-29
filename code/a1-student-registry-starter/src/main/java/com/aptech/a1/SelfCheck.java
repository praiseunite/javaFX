package com.aptech.a1;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A1 SELF-CHECK — run this class (right-click ▶ Run 'SelfCheck.main()') to test your StudentRegistry.
 * Each requirement prints PASS or FAIL with a hint. You need all PASS before you submit.
 * (You will learn the professional way to do this — JUnit — in Session 10.)
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== A1 Self-Check ===\n");

        run("R1 register() accepts a new student", () -> {
            StudentRegistry r = new StudentRegistry();
            check(r.register(new Student("S1", "Ada", "JAVA", 80)), "register() should return true for a new ID");
            check(r.size() == 1, "size() should be 1 after one registration");
        });
        run("R1 register() rejects a duplicate ID", () -> {
            StudentRegistry r = sample();
            check(!r.register(new Student("S2", "Imposter", "SQL", 10)), "register() should return false when the ID exists");
            check(r.findById("S2") != null && r.findById("S2").getName().equals("Musa"), "the original S2 must not be replaced");
            check(r.size() == 5, "size() should still be 5");
        });
        run("R2 allInOrder() keeps registration order", () -> {
            check(names(sample().allInOrder()).equals("Zara,Musa,Ada,Kemi,Ben"), "expected Zara,Musa,Ada,Kemi,Ben but got " + names(sample().allInOrder()));
        });
        run("R3 sortedByName() is A-Z and does not change the stored order", () -> {
            StudentRegistry r = sample();
            check(names(r.sortedByName()).equals("Ada,Ben,Kemi,Musa,Zara"), "expected Ada,Ben,Kemi,Musa,Zara but got " + names(r.sortedByName()));
            check(names(r.allInOrder()).equals("Zara,Musa,Ada,Kemi,Ben"), "sorting must work on a COPY - allInOrder() changed");
        });
        run("R4 findById() finds and misses correctly", () -> {
            StudentRegistry r = sample();
            check(r.findById("S3") != null && r.findById("S3").getName().equals("Ada"), "findById(\"S3\") should return Ada");
            check(r.findById("NOPE") == null, "findById of an unknown ID should return null");
        });
        run("R5 remove() removes, and returns false for unknown IDs", () -> {
            StudentRegistry r = sample();
            check(r.remove("S1"), "remove(\"S1\") should return true");
            check(r.findById("S1") == null, "S1 should be gone after remove");
            check(!r.remove("S1"), "removing S1 a second time should return false");
            check(r.size() == 4, "size() should be 4");
        });
        run("R6 courses() is unique and sorted", () -> {
            Set<String> c = sample().courses();
            check(c != null && c.toString().equals("[JAVA, PYTHON, SQL]"), "expected [JAVA, PYTHON, SQL] but got " + c);
        });
        run("R7 countPerCourse() counts and sorts by course", () -> {
            Map<String, Integer> m = sample().countPerCourse();
            check(m != null && m.toString().equals("{JAVA=2, PYTHON=1, SQL=2}"), "expected {JAVA=2, PYTHON=1, SQL=2} but got " + m);
        });
        run("R8 joinWaitingList() is FIFO, rejects unknown IDs and repeats", () -> {
            StudentRegistry r = sample();
            check(r.joinWaitingList("S4"), "S4 should be able to join");
            check(r.joinWaitingList("S1"), "S1 should be able to join");
            check(!r.joinWaitingList("S4"), "S4 is already waiting - should return false");
            check(!r.joinWaitingList("XX"), "unknown ID - should return false");
            check(r.waitingList().toString().equals("[S4, S1]"), "expected [S4, S1] but got " + r.waitingList());
        });
        run("R9 admitNext() serves the FRONT of the line, null when empty", () -> {
            StudentRegistry r = sample();
            r.joinWaitingList("S4");
            r.joinWaitingList("S1");
            Student first = r.admitNext();
            check(first != null && first.getId().equals("S4"), "first admitted should be S4 (Kemi)");
            Student second = r.admitNext();
            check(second != null && second.getId().equals("S1"), "second admitted should be S1 (Zara)");
            check(r.admitNext() == null, "admitNext() on an empty list should return null");
        });
        run("R5+R9 removing a student also removes them from the waiting list", () -> {
            StudentRegistry r = sample();
            r.joinWaitingList("S2");
            r.joinWaitingList("S3");
            r.remove("S2");
            check(r.waitingList().toString().equals("[S3]"), "expected [S3] but got " + r.waitingList());
        });
        run("R10 topScores() returns the highest scores, highest first", () -> {
            StudentRegistry r = sample();
            check(Arrays.equals(r.topScores(3), new int[]{95, 88, 72}), "expected [95, 88, 72] but got " + Arrays.toString(r.topScores(3)));
            check(r.topScores(10).length == 5, "asking for more than exist should return all 5");
            check(new StudentRegistry().topScores(3).length == 0, "an empty registry should return an empty array");
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0 ? "All requirements met - well done! Now check the style rubric before submitting."
                                       : "Fix the FAILs above, then run SelfCheck again.");
    }

    // ---------------------------------------------------------------- helpers
    private static StudentRegistry sample() {
        StudentRegistry r = new StudentRegistry();
        r.register(new Student("S1", "Zara", "JAVA", 72));
        r.register(new Student("S2", "Musa", "SQL", 64));
        r.register(new Student("S3", "Ada", "JAVA", 95));
        r.register(new Student("S4", "Kemi", "PYTHON", 88));
        r.register(new Student("S5", "Ben", "SQL", 51));
        return r;
    }

    private static String names(List<Student> list) {
        if (list == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (Student s : list) {
            if (sb.length() > 0) sb.append(',');
            sb.append(s.getName());
        }
        return sb.toString();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (RuntimeException e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
