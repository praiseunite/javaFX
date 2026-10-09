package com.aptech.s09;

import com.aptech.s09.model.Student;
import com.aptech.s09.repository.H2StudentRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Example 9 — {@code java.io} beside {@code java.nio}, doing the same job.
 *
 * <p>Both halves write the same text and read it back. The difference is what you are holding
 * while it happens: a <em>stream</em>, which you read forwards and cannot go back in, or a
 * <em>channel</em> and a <em>buffer</em>, which you can position, share, and fill without
 * blocking.
 *
 * <p>The last section is the one that saves the most debugging time in a career: two charsets,
 * two files, and a character that one of them cannot represent at all.
 */
public final class E09_IoAndNio {

    private static final Path SANDBOX = Path.of("sandbox");

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();
        Files.createDirectories(SANDBOX);

        List<Student> students = new H2StudentRepository().findAll();
        String csv = toCsv(students);

        System.out.println(Db.rule("1. java.io - a stream, wrapped twice"));
        Path ioFile = SANDBOX.resolve("io-demo.txt");
        writeWithJavaIo(ioFile, students);
        readWithJavaIo(ioFile);

        System.out.println(Db.rule("2. java.nio - a channel and a buffer"));
        Path nioFile = SANDBOX.resolve("nio-demo.txt");
        writeWithChannel(nioFile, csv);
        readWithChannel(nioFile);

        System.out.println(Db.rule("3. Where the old API still wins"));
        System.out.println("  The channel reading above is a dozen lines and still ends in a String.");
        System.out.println("  The same result, one call:");
        System.out.println("      Files.readAllLines(file, StandardCharsets.UTF_8)");
        List<String> lines = Files.readAllLines(nioFile, StandardCharsets.UTF_8);
        System.out.println("  -> " + lines.size() + " lines, first is \"" + lines.get(0) + "\"");
        System.out.println();
        System.out.println("  Reach for a channel when you need one of these:");
        System.out.println("    a position you can move       channel.position(1024)");
        System.out.println("    memory-mapped access          channel.map(READ_ONLY, 0, size)");
        System.out.println("    a lock held across processes  channel.lock()");
        System.out.println("    non-blocking I/O              SocketChannel + Selector");
        System.out.println("  Otherwise a Reader is shorter - and shorter is fewer bugs.");

        System.out.println(Db.rule("4. Charset: the same characters, two different files"));
        String sample = "Café étudiants";
        byte[] utf8 = sample.getBytes(StandardCharsets.UTF_8);
        byte[] latin1 = sample.getBytes(StandardCharsets.ISO_8859_1);
        System.out.println("  text              : " + sample);
        System.out.println("  characters        : " + sample.length());
        System.out.println("  UTF-8 bytes       : " + utf8.length);
        System.out.println("  ISO-8859-1 bytes  : " + latin1.length);
        System.out.println();
        System.out.println("  those UTF-8 bytes, read as ISO-8859-1:");
        System.out.println("    " + new String(utf8, StandardCharsets.ISO_8859_1));
        System.out.println("  those UTF-8 bytes, read as UTF-8:");
        System.out.println("    " + new String(utf8, StandardCharsets.UTF_8));
        System.out.println();
        byte[] dash = "—".getBytes(StandardCharsets.ISO_8859_1);
        System.out.println("  a character ISO-8859-1 has no room for:");
        System.out.println("    em dash -> " + dash.length + " byte, value 0x"
                + String.format("%02X", dash[0]) + " ('?') - the character is simply gone.");
        System.out.println("    No exception was thrown. That is what makes a charset bug expensive.");

        System.out.println(Db.rule("5. Never let the machine choose the charset"));
        System.out.println("  StandardCharsets.UTF_8.name()   : " + StandardCharsets.UTF_8.name());
        System.out.println("  StandardCharsets.ISO_8859_1.name(): " + StandardCharsets.ISO_8859_1.name());
        System.out.println("  Charset.isSupported(\"UTF-8\")     : " + Charset.isSupported("UTF-8"));
        System.out.println();
        System.out.println("  Charset.defaultCharset() exists, and is a property of the machine");
        System.out.println("  and the JVM flags - not of your program. Every file in this example");
        System.out.println("  named its charset, so none of them depends on where they run.");
    }

    // ------------------------------------------------------------------ the report

    private static String toCsv(List<Student> students) {
        StringBuilder out = new StringBuilder();
        for (Student s : students) {
            out.append(s.name()).append(',').append(s.email()).append(',').append(s.grade()).append('\n');
        }
        return out.toString();
    }

    // ------------------------------------------------------------------ java.io

    /**
     * Three objects to write one line of text: a {@code FileOutputStream}, an
     * {@code OutputStreamWriter} that knows the charset, and a {@code BufferedWriter} that
     * stops it being one system call per character.
     */
    private static void writeWithJavaIo(Path file, List<Student> students) throws Exception {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file.toFile()), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                out.write(s.name() + "," + s.email() + "," + s.grade());
                out.write("\n");
            }
        }
        System.out.println("  wrote " + Files.size(file) + " bytes through OutputStreamWriter");
    }

    /** And three to read it back. {@code readLine()} removes the line ending for you. */
    private static void readWithJavaIo(Path file) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                count++;
                if (count <= 2) {
                    System.out.println("  line " + count + ": " + line);
                }
            }
            System.out.println("  readLine() returned " + count + " lines, endings stripped");
        }
    }

    // ------------------------------------------------------------------ java.nio

    /**
     * A channel writes from a buffer, and the buffer remembers where it got to: the
     * {@code while (buffer.hasRemaining())} loop is not politeness, it is how a channel
     * reports a partial write.
     */
    private static void writeWithChannel(Path file, String text) throws Exception {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(text);
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            int written = 0;
            while (buffer.hasRemaining()) {
                written += channel.write(buffer);
            }
            System.out.println("  encoded to " + written + " bytes by Charset.encode()");
            System.out.println("  written by FileChannel.write()");
            System.out.println("  file on disk   : " + channel.size() + " bytes");
        }
    }

    /**
     * Reading is the same in reverse, plus {@code flip()} — the one step everybody forgets.
     * A buffer you have written into is ready to be written <em>from</em>; flip() turns it
     * round so it can be read from.
     */
    private static void readWithChannel(Path file) throws Exception {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            while (buffer.hasRemaining()) {
                if (channel.read(buffer) < 0) {
                    break;
                }
            }
            int bytes = buffer.position();
            buffer.flip();
            String text = StandardCharsets.UTF_8.decode(buffer).toString();
            String[] lines = text.split("\n");
            System.out.println("  read " + bytes + " bytes into a " + buffer.capacity() + "-byte buffer");
            System.out.println("  decoded to " + text.length() + " characters");
            System.out.println("  first line : " + lines[0]);
            System.out.println("  last line  : " + lines[lines.length - 1]);
        }
    }
}
