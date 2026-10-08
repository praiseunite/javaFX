package com.aptech.s06lab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * A short demo of the clinic, so you can watch all five faults with your own eyes.
 * Every step is wrapped, so one broken method cannot hide the other four.
 *
 * Run SelfCheck first — this is what the office would actually see.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Path dir = Path.of("selfcheck-tmp");
        Files.createDirectories(dir);

        System.out.println("The Repair Clinic - demo run");
        System.out.println();

        step("1. the sign-in register  (Session 1)", () -> {
            List<String> signIns = List.of("S-1003", "S-1001", "S-1003", "S-1002");
            System.out.println("   sign-ins today  : " + signIns);
            System.out.println("   distinct        : " + RepairClinic.distinctStudents(signIns));
        });

        step("2. sum the marks         (Session 2)", () -> {
            System.out.println("   whole numbers   : " + RepairClinic.sumMarks(List.of(70, 85)));
            System.out.println("   decimals        : " + RepairClinic.sumMarks(List.of(70.5, 85.5)));
        });

        step("3. write the report      (Session 3)", () -> {
            Path report = dir.resolve("clinic-report.txt");
            Files.deleteIfExists(report);
            RepairClinic.saveReport(report, List.of("S-1001,Ada", "S-1002,Ben"));
            System.out.println("   lines on disk   : " + Files.readAllLines(report).size());
        });

        step("4. process the files     (Session 5)", () -> {
            long start = System.currentTimeMillis();
            Map<String, Integer> tally =
                    RepairClinic.processAll(List.of("a.log", "b.log", "c.log"), 20, 20);
            long took = System.currentTimeMillis() - start;
            System.out.println("   tally           : " + tally);
            System.out.println("   took            : " + took + " ms   (one at a time is about 1200 ms)");
        });
    }

    private static void step(String name, Step body) {
        System.out.println(name);
        try {
            body.run();
        } catch (Exception e) {
            System.out.println("   FAILED: " + e);
        }
        System.out.println();
    }

    interface Step {
        void run() throws Exception;
    }
}
