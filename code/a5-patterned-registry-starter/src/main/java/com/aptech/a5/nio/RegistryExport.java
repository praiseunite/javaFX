package com.aptech.a5.nio;

import com.aptech.a5.model.Student;

import java.nio.file.Path;
import java.util.List;

/**
 * R8 — the registry, written to disk and read back.   <<< WRITE THIS CLASS >>>
 *
 * <p>Everything here goes through {@link Path} and {@link java.nio.file.Files}. No
 * {@code java.io.File} appears in this file at all, and none is needed:
 *
 * <ul>
 *   <li>{@link #export} — create the folders (with their parents), write {@code all.txt} with a
 *       header line and one line per student, and write one file per student under
 *       {@code students/}. Return the path of {@code all.txt}.</li>
 *   <li>{@link #read} — read {@code all.txt} back, skip the header, and rebuild the students.
 *       {@code list.equals(read())} must be {@code true}: it is the only proof that the format
 *       is a round trip and not just a write.</li>
 *   <li>{@link #tree} — every file under the export folder, at any depth, sorted. Use
 *       {@code Files.walk} and <b>close the stream</b> — on Windows an unclosed walk holds the
 *       folder and the next run cannot delete it.</li>
 *   <li>{@link #txtFiles} — the {@code .txt} files directly in {@code students/}. Use a
 *       {@code DirectoryStream} with a glob; it does not build the whole list in memory, which is
 *       the point of the class.</li>
 *   <li>{@link #deleteAll} — remove everything, deepest first, then the folder itself. Deepest
 *       first is not optional: a folder that still has files in it cannot be deleted.</li>
 * </ul>
 *
 * <p>Encoding is always explicit: {@code StandardCharsets.UTF_8}. Never let the machine choose —
 * the default charset depends on the machine, and a file written on one and read on another is
 * how accents turn into {@code Ã©}.
 *
 * <p>The two format methods are static and public because they are the format, not the storage:
 * {@link #line} and {@link #parse} are the only places that know what a student looks like on
 * disk, so the format can be changed in one place.
 */
public final class RegistryExport {

    /** The first line of all.txt. Skipped on the way back in, and worth having when you open the file. */
    public static final String HEADER = "id|name|email|courseId|mark|active";

    /** The file holding every student, one per line. */
    public static final String ALL_FILE = "all.txt";

    /** The folder holding one file per student. */
    public static final String STUDENTS_DIR = "students";

    /** The field separator. A pipe, because a name may contain a comma and this is not CSV. */
    public static final String SEPARATOR = "|";

    private final Path dir;

    public RegistryExport(Path dir) {
        this.dir = dir;
    }

    /** The folder being written to. */
    public Path directory() {
        return dir;
    }

    /** TODO R8 — write everything, and return the path of {@link #ALL_FILE}. */
    public Path export(List<Student> students) {
        throw new UnsupportedOperationException("R8: RegistryExport.export() not implemented");
    }

    /** TODO R8 — read {@link #ALL_FILE} back. An empty list if the file is not there yet. */
    public List<Student> read() {
        throw new UnsupportedOperationException("R8: RegistryExport.read() not implemented");
    }

    /** TODO R8 — every file under the export folder, any depth, sorted by full path. */
    public List<Path> tree() {
        throw new UnsupportedOperationException("R8: RegistryExport.tree() not implemented");
    }

    /** TODO R8 — the .txt files directly inside {@link #STUDENTS_DIR}. */
    public List<Path> txtFiles() {
        throw new UnsupportedOperationException("R8: RegistryExport.txtFiles() not implemented");
    }

    /** TODO R8 — delete the whole export folder. Doing nothing when it is absent is correct. */
    public void deleteAll() {
        throw new UnsupportedOperationException("R8: RegistryExport.deleteAll() not implemented");
    }

    /**
     * TODO R8 — one student as one line: the six fields joined with {@value #SEPARATOR}, in the
     * order the header lists them.
     *
     * <p>A null field is an EMPTY field, not the text {@code "null"} — otherwise a student with no
     * mark comes back with a mark that is the four-letter word. {@code String.valueOf(null)}
     * gives you {@code "null"}, so this is a trap worth knowing about.
     */
    public static String line(Student student) {
        throw new UnsupportedOperationException("R8: RegistryExport.line() not implemented");
    }

    /**
     * TODO R8 — one line back into a {@link Student}. The exact inverse of {@link #line}.
     *
     * <p>An empty field is null again for the nullable columns ({@code courseId}, {@code mark}).
     * Split with a limit so a trailing empty field is kept:
     * {@code text.split("\\|", -1)}.
     */
    public static Student parse(String text) {
        throw new UnsupportedOperationException("R8: RegistryExport.parse() not implemented");
    }

    /**
     * GIVEN — a file-safe version of a student's name: lowercase, and every run of anything that
     * is not a letter or a digit becomes one hyphen. "Ada Lovelace" becomes "ada-lovelace".
     */
    public static String slug(String name) {
        return name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
