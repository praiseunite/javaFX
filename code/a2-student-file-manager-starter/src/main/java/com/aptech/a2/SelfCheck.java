package com.aptech.a2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * A2 SELF-CHECK — run this class to test your StudentFileManager. You need all PASS before you submit.
 * It works in a folder called "selfcheck-tmp" which it deletes at the end.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;
    private static final File DIR = new File("selfcheck-tmp");

    interface Test { void run() throws Exception; }

    private static final List<Student> SAMPLE = List.of(
            new Student("S1", "Zara", "JAVA", 72),
            new Student("S2", "Musa", "SQL", 64),
            new Student("S3", "Ada", "JAVA", 95));

    public static void main(String[] args) {
        System.out.println("=== A2 Self-Check ===\n");
        deleteAll(DIR);
        DIR.mkdirs();
        StudentFileManager m = new StudentFileManager();

        run("R1 saveCsv() writes a header and one line per student", () -> {
            File f = new File(DIR, "r1.csv");
            m.saveCsv(SAMPLE, f);
            List<String> lines = readLines(f);
            check(lines.size() == 4, "expected 4 lines (header + 3) but found " + lines.size());
            check(lines.get(0).equals("id,name,course,score"), "first line should be the header, found: " + lines.get(0));
            check(lines.get(1).equals("S1,Zara,JAVA,72"), "second line should be S1,Zara,JAVA,72, found: " + lines.get(1));
        });
        run("R1 saveCsv() creates missing folders", () -> {
            File f = new File(DIR, "new/folder/r1.csv");
            m.saveCsv(SAMPLE, f);
            check(f.exists(), "the file (and its folders) should have been created");
        });
        run("R2 loadCsv() reads back exactly what saveCsv() wrote", () -> {
            File f = new File(DIR, "r2.csv");
            m.saveCsv(SAMPLE, f);
            List<Student> loaded = m.loadCsv(f);
            check(SAMPLE.equals(loaded), "expected " + SAMPLE + " but got " + loaded);
        });
        run("R2+R3 loadCsv() skips blank and bad lines, and counts the bad ones", () -> {
            File f = new File(DIR, "messy.csv");
            try (PrintWriter out = new PrintWriter(new FileWriter(f))) {
                out.println("id,name,course,score");
                out.println("S1,Zara,JAVA,72");
                out.println("");
                out.println("S2,Musa,SQL");                 // only 3 fields -> bad
                out.println("S3,Ada,JAVA,ninety");          // not a number -> bad
                out.println(" S4 , Kemi , PYTHON , 88 ");   // spaces should be trimmed
            }
            List<Student> loaded = m.loadCsv(f);
            check(loaded.size() == 2, "expected 2 good students but got " + loaded.size());
            check(loaded.get(1).equals(new Student("S4", "Kemi", "PYTHON", 88)), "spaces around values should be trimmed: " + loaded.get(1));
            check(m.getLastSkipped() == 2, "expected 2 skipped bad lines but got " + m.getLastSkipped());
        });
        run("R2 loadCsv() of a missing file throws an IOException", () -> {
            try {
                m.loadCsv(new File(DIR, "does-not-exist.csv"));
                check(false, "no exception was thrown");
            } catch (IOException expected) {
                // correct
            }
        });
        run("R4+R5 saveBinary()/loadBinary() round trip", () -> {
            File f = new File(DIR, "r4.dat");
            m.saveBinary(SAMPLE, f);
            check(f.length() > 0, "the binary file is empty");
            List<Student> loaded = m.loadBinary(f);
            check(SAMPLE.equals(loaded), "expected " + SAMPLE + " but got " + loaded);
        });
        run("R4 binary format is: count, then id, name, course (writeUTF) and score (writeInt)", () -> {
            File f = new File(DIR, "r4b.dat");
            m.saveBinary(List.of(new Student("A", "B", "C", 7)), f);
            // 4 (count) + 3 x (2 + 1) (three one-letter writeUTF) + 4 (score) = 17 bytes
            check(f.length() == 17, "expected 17 bytes for one tiny student but the file has " + f.length());
        });
        run("R6+R7 saveObjects()/loadObjects() round trip", () -> {
            File f = new File(DIR, "r6.ser");
            m.saveObjects(SAMPLE, f);
            List<Student> loaded = m.loadObjects(f);
            check(SAMPLE.equals(loaded), "expected " + SAMPLE + " but got " + loaded);
        });
        run("R8 copyFile() copies every byte and returns the count", () -> {
            File src = new File(DIR, "r8.csv");
            m.saveCsv(SAMPLE, src);
            File dest = new File(DIR, "backup/r8-copy.csv");
            long n = m.copyFile(src, dest);
            check(n == src.length(), "returned " + n + " but the source has " + src.length() + " bytes");
            check(dest.length() == src.length(), "the copy has a different size");
            check(readLines(dest).equals(readLines(src)), "the copy's contents differ");
        });
        run("R9 listFiles() filters by extension and sorts", () -> {
            File folder = new File(DIR, "r9");
            folder.mkdirs();
            for (String name : new String[]{"b.csv", "a.csv", "c.dat", "notes.txt"}) {
                new File(folder, name).createNewFile();
            }
            new File(folder, "sub.csv").mkdirs();                 // a FOLDER named like a csv - must be ignored
            List<String> csv = m.listFiles(folder, ".csv");
            check(csv.equals(List.of("a.csv", "b.csv")), "expected [a.csv, b.csv] but got " + csv);
            check(m.listFiles(new File(DIR, "no-such-folder"), ".csv").isEmpty(), "a missing folder should give an empty list");
        });

        deleteAll(DIR);
        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0 ? "All requirements met - now check the style rubric before submitting."
                                       : "Fix the FAILs above, then run SelfCheck again.");
    }

    // ---------------------------------------------------------------- helpers
    private static List<String> readLines(File f) throws IOException {
        return java.nio.file.Files.readAllLines(f.toPath());
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

    private static void deleteAll(File f) {
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) deleteAll(c);
        }
        f.delete();
    }
}
