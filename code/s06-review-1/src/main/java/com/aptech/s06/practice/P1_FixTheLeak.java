package com.aptech.s06.practice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * PRACTICE 1 (Easy) — fix the leaky reader.
 *
 * The bug below is the one that survives every review, because it produces the RIGHT answer.
 * An unclosed BufferedReader still reads the file correctly; it just never releases the
 * operating-system file handle. One handle per call, and after a few thousand calls the
 * program dies with "Too many open files" — at which point the cause is nowhere near the line
 * that failed.
 *
 * Look closely at the difference between the two methods: the fix is the same number of
 * lines, and it is the one that is still correct when readLine() throws halfway through.
 */
public class P1_FixTheLeak {

    private static final Path SANDBOX = Path.of("sandbox");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(SANDBOX);
        Path orders = SANDBOX.resolve("p1-orders.log");
        Files.write(orders, List.of("order-1", "order-2", "order-3", "order-4"));

        System.out.println("Practice 1 (Easy) - the leaky reader");
        System.out.println();
        System.out.println("BROKEN - the reader is opened and never closed:");
        System.out.println("  lines read = " + countLinesBroken(orders));
        System.out.println("  -> correct, and leaking one file handle every time it is called.");
        System.out.println();
        System.out.println("FIXED - try-with-resources:");
        System.out.println("  lines read = " + countLines(orders));
        System.out.println("  -> same answer, and the handle is released on every path out.");
        System.out.println();
        System.out.println("The leak never changes the answer, which is why a code review misses it.");
        System.out.println("It changes whether the program still runs tomorrow morning.");
    }

    /** BROKEN: no close(). Works perfectly, until the handles run out. */
    static int countLinesBroken(Path file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
        int lines = 0;
        while (reader.readLine() != null) {
            lines++;
        }
        return lines;
    }

    /** FIXED: the reader is closed on the way out, even if readLine() throws. */
    static int countLines(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }
}
