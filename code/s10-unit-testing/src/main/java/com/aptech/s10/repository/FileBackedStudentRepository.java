package com.aptech.s10.repository;

import com.aptech.s10.model.Student;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Store number two: the same registry, kept in a text file. COMPLETE — this is code under test.
 *
 * <p>It exists for one reason. A test written against {@link StudentRepository} is supposed to
 * say "any store must do this". Store number two is how you find out whether that is true. If
 * the suite passes against {@link InMemoryStudentRepository} and fails against this one, then
 * the tests were describing the in-memory store rather than the interface — and the bug is in
 * the test, not in this file.
 *
 * <p>It is also a different <em>kind</em> of store: it can fail in ways a list cannot. The file
 * may not exist, the folder may not exist, the disk may refuse. Those failures arrive as
 * {@link UncheckedIOException}, which is why the file work is wrapped — a caller of
 * {@code findById} should not have to catch an I/O exception to ask a question about a student.
 *
 * <p><strong>Where the file lives is the test's business.</strong> In a test you pass a
 * temporary folder (see Example 3), so the store never touches anything real and two tests can
 * never collide. The format is one student per line, fields separated by {@code |}, with an
 * empty field meaning null — which is how a student with no mark survives the round trip.
 */
public final class FileBackedStudentRepository implements StudentRepository {

    /** One student per line. An empty field is a null. */
    private static final String SEP = "|";

    private final Path file;
    private final List<Student> students = new ArrayList<>();
    private int nextId = 1;

    /**
     * Opens (or creates) a store in this folder. The folder is created if it is not there, and
     * an existing file is read back into memory once — after that the memory copy is the truth
     * and every change is written straight through.
     */
    public FileBackedStudentRepository(Path dir) {
        this.file = dir.resolve("students.txt");
        try {
            Files.createDirectories(dir);
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    if (!line.isBlank()) students.add(parse(line));
                }
            }
            nextId = students.stream().mapToInt(Student::id).max().orElse(0) + 1;
        } catch (IOException e) {
            throw new UncheckedIOException("could not open the student store at " + dir, e);
        }
    }

    @Override
    public Optional<Student> findById(int id) {
        return students.stream().filter(s -> s.id() == id).findFirst();
    }

    @Override
    public List<Student> findAll() {
        return List.copyOf(students);
    }

    @Override
    public List<Student> findByCourse(int courseId) {
        return students.stream()
                .filter(s -> s.courseId() != null && s.courseId() == courseId)
                .toList();
    }

    @Override
    public List<Student> findByNameContaining(String fragment) {
        String needle = fragment.toLowerCase(Locale.ROOT);
        return students.stream()
                .filter(s -> s.name().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    @Override
    public Student save(Student student) {
        if (student.id() == 0) {
            Student stored = new Student(nextId++, student.name(), student.email(),
                    student.courseId(), student.mark(), student.active());
            students.add(stored);
            flush();
            return stored;
        }
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id() == student.id()) {
                students.set(i, student);
                flush();
                return student;
            }
        }
        throw new IllegalArgumentException("no student with id " + student.id());
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = students.removeIf(s -> s.id() == id);
        if (removed) flush();
        return removed;
    }

    @Override
    public int count() {
        return students.size();
    }

    @Override
    public String storeName() {
        return "file";
    }

    // ---------------------------------------------------------------- the file itself

    private void flush() {
        List<String> lines = students.stream().map(FileBackedStudentRepository::render).toList();
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("could not write the student store at " + file, e);
        }
    }

    /** A student as one line. A null becomes an empty field, so it can come back as null. */
    private static String render(Student s) {
        return s.id() + SEP + s.name() + SEP + s.email() + SEP
                + (s.courseId() == null ? "" : s.courseId()) + SEP
                + (s.mark() == null ? "" : s.mark().toPlainString()) + SEP
                + s.active();
    }

    private static Student parse(String line) {
        String[] f = line.split("\\" + SEP, -1);
        return new Student(
                Integer.parseInt(f[0]),
                f[1],
                f[2],
                f[3].isEmpty() ? null : Integer.valueOf(f[3]),
                f[4].isEmpty() ? null : new BigDecimal(f[4]),
                Boolean.parseBoolean(f[5]));
    }
}
