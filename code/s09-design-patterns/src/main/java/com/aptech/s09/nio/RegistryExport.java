package com.aptech.s09.nio;

import com.aptech.s09.model.Student;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Reads and writes the registry as plain text through {@code java.nio.file}.
 *
 * <p>This is the "export a report" feature that every registry eventually grows, and it is
 * here because it is the honest place to meet {@link Path} and {@link Files}: real files, in
 * a real folder, with real names.
 *
 * <p>The pieces of {@code java.nio.file} this class uses, and what each one is for:
 * <ul>
 *   <li>{@link Path} — a <em>path</em>, not a file. It does not have to exist. This is the
 *       replacement for {@code java.io.File}, and it is a value, not a handle.</li>
 *   <li>{@link Files} — the operations. {@code createDirectories}, {@code writeString},
 *       {@code readAllLines}, {@code walk}, {@code newDirectoryStream}, {@code size}.</li>
 *   <li>{@link FileSystems#getDefault()} — the file system those paths belong to. Ask it for
 *       a {@link PathMatcher} and you get glob patterns: {@code "glob:**&#47;*.txt"}.</li>
 * </ul>
 *
 * <p>Every method throws {@link IOException} rather than swallowing it. A report that failed
 * to save is something the caller has to know about; a repository can hide a database failure
 * behind an unchecked exception, but a file operation a user asked for cannot.
 */
public final class RegistryExport {

    /** The header line of the export, and the format every line follows. */
    public static final String HEADER = "id|name|email|courseId|mark|active";

    /** The one file holding every student. */
    public static final String ALL_FILE = "all.txt";

    /** The sub-folder holding one file per student. */
    public static final String STUDENTS_DIR = "students";

    private static final String FIELD_SEPARATOR = "|";
    private static final String GLOB = "glob:**/*.txt";

    private final Path folder;

    public RegistryExport(Path folder) {
        this.folder = folder;
    }

    public Path folder() {
        return folder;
    }

    // ------------------------------------------------------------------- writing

    /** Writes every student to {@code all.txt} and returns the file it wrote. */
    public Path export(List<Student> students) throws IOException {
        Files.createDirectories(folder);
        Path file = folder.resolve(ALL_FILE);
        Files.writeString(file, toText(students), StandardCharsets.UTF_8);
        return file;
    }

    /** Writes one student to {@code students/&lt;name&gt;.txt}. */
    public Path writeOne(Student student) throws IOException {
        Path dir = folder.resolve(STUDENTS_DIR);
        Files.createDirectories(dir);
        Path file = dir.resolve(slug(student.name()) + ".txt");
        Files.writeString(file, line(student) + "\n", StandardCharsets.UTF_8);
        return file;
    }

    /** The whole export as one string, header included. */
    public String toText(List<Student> students) {
        StringBuilder out = new StringBuilder(HEADER).append('\n');
        for (Student s : students) {
            out.append(line(s)).append('\n');
        }
        return out.toString();
    }

    // ------------------------------------------------------------------- reading

    /** Reads {@code all.txt} back into students, skipping the header. */
    public List<Student> read() throws IOException {
        Path file = folder.resolve(ALL_FILE);
        if (!Files.exists(file)) {
            throw new IOException("nothing to read: " + file + " does not exist");
        }
        List<Student> out = new ArrayList<>();
        for (String text : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (text.isBlank() || text.equals(HEADER)) continue;
            out.add(parse(text));
        }
        return out;
    }

    // ------------------------------------------------------------------- listing

    /** Every file in the folder, one level deep, sorted. */
    public List<Path> files() throws IOException {
        try (Stream<Path> stream = Files.list(folder)) {
            return sorted(stream.filter(Files::isRegularFile).toList());
        }
    }

    /** The {@code *.txt} files in the folder, using a DirectoryStream glob. */
    public List<Path> txtFiles() throws IOException {
        List<Path> out = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path p : stream) {
                out.add(p);
            }
        }
        return sorted(out);
    }

    /** Every {@code *.txt} anywhere under the folder, using a PathMatcher glob. */
    public List<Path> everythingTxt() throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(GLOB);
        try (Stream<Path> tree = Files.walk(folder)) {
            return sorted(tree.filter(matcher::matches).toList());
        }
    }

    /** The whole tree, folders and files, deepest last. */
    public List<Path> tree() throws IOException {
        try (Stream<Path> tree = Files.walk(folder)) {
            return sorted(tree.toList());
        }
    }

    // ------------------------------------------------------------------- the format

    /** One student as one line of the export. */
    public static String line(Student s) {
        return s.id() + FIELD_SEPARATOR + s.name() + FIELD_SEPARATOR + s.email()
                + FIELD_SEPARATOR + (s.courseId() == null ? "" : s.courseId())
                + FIELD_SEPARATOR + (s.mark() == null ? "" : s.mark().toPlainString())
                + FIELD_SEPARATOR + s.active();
    }

    /**
     * One line back into a student. It fails loudly on a line that is not six fields wide,
     * because a report file that has been edited by hand is the normal case, not the
     * exception, and "silently skipped row 7" is how a registry loses a student.
     */
    public static Student parse(String text) {
        String[] fields = text.split("\\" + FIELD_SEPARATOR, -1);
        if (fields.length != 6) {
            throw new IllegalArgumentException(
                    "expected 6 fields, found " + fields.length + " in: " + text);
        }
        return new Student(
                Integer.parseInt(fields[0].trim()),
                fields[1].trim(),
                fields[2].trim(),
                fields[3].isBlank() ? null : Integer.valueOf(fields[3].trim()),
                fields[4].isBlank() ? null : new BigDecimal(fields[4].trim()),
                Boolean.parseBoolean(fields[5].trim()));
    }

    /** {@code "Ada Lovelace"} to {@code "ada-lovelace"} — a safe file name. */
    public static String slug(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Deletes the whole export folder and everything under it, so a run starts from nothing
     * and prints the same thing on the tenth run as it did on the first.
     *
     * <p>Note the sort: a directory has to be deleted after its contents, and the depth of a
     * path is the one ordering that guarantees it.
     */
    public void deleteAll() throws IOException {
        if (!Files.exists(folder)) {
            return;
        }
        try (Stream<Path> tree = Files.walk(folder)) {
            for (Path p : tree.sorted(Comparator.comparingInt(Path::getNameCount).reversed()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }

    /**
     * Sorted by path text with the separators normalised, so the order is the same on
     * Windows and on Linux. {@code Files.walk} returns whatever order the file system
     * hands out — which is a real bug waiting to happen in a report anyone has to diff.
     */
    private static List<Path> sorted(List<Path> paths) {
        return paths.stream()
                .sorted(Comparator.comparing(p -> p.toString().replace('\\', '/')))
                .toList();
    }
}
