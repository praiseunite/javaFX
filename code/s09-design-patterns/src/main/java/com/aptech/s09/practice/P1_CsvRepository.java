package com.aptech.s09.practice;

import com.aptech.s09.Db;
import com.aptech.s09.model.Student;
import com.aptech.s09.nio.RegistryExport;
import com.aptech.s09.repository.H2StudentRepository;
import com.aptech.s09.repository.RepositoryException;
import com.aptech.s09.repository.StudentRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Practice 1 — a third store, written without touching anything that already exists.
 *
 * <p>The claim to test here is not "CSV files are useful". It is that adding a completely new
 * kind of storage costs one class, because everything above {@link StudentRepository} was
 * written against the interface.
 *
 * <p>Watch how short this file is: the CSV format is not written twice, because it is
 * {@link RegistryExport}'s format and this class reuses it.
 */
public final class P1_CsvRepository {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        Path file = Path.of("sandbox", "students.csv");
        Files.createDirectories(file.getParent());
        Files.deleteIfExists(file);

        System.out.println(Db.rule("1. Fill the CSV store from the database"));
        StudentRepository database = new H2StudentRepository();
        StudentRepository csv = new CsvStudentRepository(file, database.findAll());
        System.out.println("  " + csv.count() + " students written to " + file);

        System.out.println(Db.rule("2. Open the file again, as a new object"));
        StudentRepository reopened = new CsvStudentRepository(file);
        System.out.println("  a brand new CsvStudentRepository holds " + reopened.count() + " students");
        System.out.println("  findById(4)  : " + reopened.findById(4).orElseThrow());
        System.out.println("  on course 2  : " + reopened.findByCourse(2).size() + " students");
        System.out.println("  name has 'ar': " + reopened.findByNameContaining("ar").size() + " students");

        System.out.println(Db.rule("3. Write through it"));
        Student saved = reopened.save(Student.unsaved("Nia Okoro", "nia@example.com", 1, null));
        System.out.println("  saved with id " + saved.id() + " (the file decided the next id)");
        System.out.println("  now holds " + new CsvStudentRepository(file).count() + " students");

        System.out.println(Db.rule("4. Nothing above the interface changed"));
        System.out.println("  StudentRepository, InMemoryStudentRepository, H2StudentRepository,");
        System.out.println("  RepositoryFactory, EnrolmentService and RegistryController were not");
        System.out.println("  edited to make this file exist. That is the seam working.");
    }

    /**
     * A store that keeps the whole registry in one CSV file, re-read and re-written on every
     * call.
     *
     * <p>It is honest about being slow — this is the worst possible implementation of the
     * interface, and it still satisfies every caller, because the interface never promised
     * anything about speed. A repository that <em>had</em> promised speed would have made this
     * class a lie instead of a lesson.
     */
    static final class CsvStudentRepository implements StudentRepository {

        private final Path file;

        CsvStudentRepository(Path file) {
            this.file = file;
        }

        /**
         * A store seeded from another store's contents.
         *
         * <p>The students arrive with their ids already set, so they are written straight to the
         * file rather than through {@link #save}. That method's contract is the same one every
         * implementation honours — insert when the id is 0, update when it is not — and by that
         * contract a student the file has never seen has no business being "updated" into it.
         */
        CsvStudentRepository(Path file, List<Student> seed) {
            this.file = file;
            store(seed);
        }

        @Override
        public Optional<Student> findById(int id) {
            return load().stream().filter(s -> s.id() == id).findFirst();
        }

        @Override
        public List<Student> findAll() {
            return load();
        }

        @Override
        public List<Student> findByCourse(int courseId) {
            return load().stream()
                    .filter(s -> s.courseId() != null && s.courseId() == courseId)
                    .toList();
        }

        @Override
        public List<Student> findByNameContaining(String fragment) {
            String needle = fragment.toLowerCase(Locale.ROOT);
            return load().stream()
                    .filter(s -> s.name().toLowerCase(Locale.ROOT).contains(needle))
                    .toList();
        }

        @Override
        public Student save(Student student) {
            List<Student> students = load();
            if (student.id() == 0) {
                int nextId = students.stream().mapToInt(Student::id).max().orElse(0) + 1;
                Student stored = new Student(nextId, student.name(), student.email(),
                        student.courseId(), student.mark(), student.active());
                students.add(stored);
                store(students);
                return stored;
            }
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).id() == student.id()) {
                    students.set(i, student);
                    store(students);
                    return student;
                }
            }
            throw new IllegalArgumentException("no student with id " + student.id());
        }

        @Override
        public boolean deleteById(int id) {
            List<Student> students = load();
            boolean removed = students.removeIf(s -> s.id() == id);
            if (removed) {
                store(students);
            }
            return removed;
        }

        @Override
        public int count() {
            return load().size();
        }

        @Override
        public String storeName() {
            return "csv (" + file + ")";
        }

        /** The whole file, as students. A missing file is an empty registry, not an error. */
        private List<Student> load() {
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                return lines.filter(line -> !line.isBlank())
                        .map(RegistryExport::parse)
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                throw new RepositoryException("cannot read " + file, e);
            }
        }

        /** Writes the whole registry back. Reusing RegistryExport's format, not copying it. */
        private void store(List<Student> students) {
            try {
                Files.write(file, students.stream().map(RegistryExport::line).toList(),
                        StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RepositoryException("cannot write " + file, e);
            }
        }
    }
}
