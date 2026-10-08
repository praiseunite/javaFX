package com.aptech.s06.tyi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TRY IT YOURSELF 3 — Session 3: File handling, streams and serialization.
 *
 * Copy a small CSV file line by line. The skills being tested are the two that break most
 * often in student code:
 *
 *   1. Reader/Writer for TEXT (characters). InputStream/OutputStream are for raw bytes.
 *      Use the wrong family on a text file and you get mojibake, not an error.
 *   2. Both resources opened inside ONE try-with-resources, so both are closed on every
 *      path out of the method — including the exception path.
 *
 * The files are written into sandbox/, which is never published to GitHub.
 */
public class T3_FileCopy {

    private static final Path SANDBOX = Path.of("sandbox");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(SANDBOX);

        Path source = SANDBOX.resolve("t3-attendance.csv");
        Path copy = SANDBOX.resolve("t3-attendance-copy.csv");

        Files.write(source, List.of(
                "S-1001,Ada,90",
                "S-1002,Ben,75",
                "S-1003,Chioma,88"));

        copyText(source, copy);

        List<String> lines = Files.readAllLines(copy);

        System.out.println("Try It Yourself 3 - copy a text file, line by line");
        System.out.println("  source : " + source + "  (" + Files.size(source) + " bytes)");
        System.out.println("  copy   : " + copy + "  (" + Files.size(copy) + " bytes)");
        System.out.println("  lines copied: " + lines.size());
        for (String line : lines) {
            System.out.println("    " + line);
        }
        System.out.println();
        System.out.println("Copy closed both resources itself - there is no finally block anywhere,");
        System.out.println("and the file handles are released even if readLine() throws.");
    }

    /** Copies text with BufferedReader -> BufferedWriter, both closed automatically. */
    static void copyText(Path from, Path to) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(from);
             BufferedWriter writer = Files.newBufferedWriter(to)) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();       // newLine() writes the correct separator for this OS
            }
        }
    }
}
