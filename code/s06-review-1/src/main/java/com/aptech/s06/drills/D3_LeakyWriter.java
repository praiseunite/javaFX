package com.aptech.s06.drills;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * DRILL 3 — the leaky file.
 *
 * The bug: a method writes a log line and returns. The FileWriter was never closed, so
 * the text is still sitting in the writer's internal buffer and has never reached the disk.
 * Nothing throws. The file is simply empty — and the file handle stays open until the JVM
 * exits, which is the leak.
 *
 * Why it is easy to miss: FileWriter is buffered. write() means "I have taken your text",
 * not "your text is on disk". Only close() (or flush()) forces it out.
 *
 * The fix: try-with-resources. It calls close() on the way out of the block, including
 * when an exception is thrown, which is the part a hand-written close() in a happy path
 * always gets wrong.
 *
 * Both halves run in the same JVM, so you are watching the real behaviour and not a
 * guess about it. The file is written into sandbox/, which never reaches GitHub.
 */
public class D3_LeakyWriter {

    private static final Path SANDBOX = Path.of("sandbox");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(SANDBOX);

        System.out.println("Drill 3 - the leaky file");
        System.out.println();

        broken();
        System.out.println();
        fixed();
    }

    private static void broken() throws Exception {
        Path file = SANDBOX.resolve("drill3_leaky.log");

        FileWriter writer = new FileWriter(file.toFile());
        writer.write("first line of the log");
        writer.write(System.lineSeparator());
        // ...and the method ends here. No close(). No flush(). No try-with-resources.

        String readBack = Files.readString(file);

        System.out.println("BROKEN - FileWriter written to, never closed");
        System.out.println("  the file was created: " + Files.exists(file));
        System.out.println("  size on disk now:     " + Files.size(file) + " bytes");
        System.out.println("  reading it back gives \"" + readBack + "\"");
        System.out.println("  characters we thought we had written: "
                + "first line of the log".length());
        System.out.println("  -> the text is in the writer's buffer, not on the disk.");
        System.out.println("  -> and the handle is still open: that is the resource leak.");
    }

    private static void fixed() throws Exception {
        Path file = SANDBOX.resolve("drill3_fixed.log");

        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write("first line of the log");
            writer.write(System.lineSeparator());
        }   // close() runs here - automatically, even if the block throws

        String readBack = Files.readString(file);

        System.out.println("FIXED - try-with-resources closes it for you");
        System.out.println("  size on disk now:     " + Files.size(file) + " bytes");
        System.out.println("  reading it back gives \"" + readBack.replace("\n", "").replace("\r", "") + "\"");
        System.out.println("  -> close() flushed the buffer and released the handle.");
    }
}
