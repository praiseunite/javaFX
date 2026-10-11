package com.aptech.s10.repository;

import com.aptech.s10.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Store number one: a list in memory. COMPLETE — this is the code under test.
 *
 * <p>This is the implementation from Session 9, finished. It is here so that Session 10 has
 * something real to test: no database, no schema, no seed file, nothing to clean up between
 * tests. A test can build the whole registry in four lines and throw it away afterwards.
 *
 * <p>The methods deliberately behave the way a careful implementation behaves — {@code findAll}
 * copies its list, {@code save} is insert-or-update, {@code deleteById} reports whether it
 * actually removed anything. Some of those choices are exactly what
 * {@link BrokenRepository} gets wrong, and Part 6 uses the contrast to show a suite going red.
 */
public final class InMemoryStudentRepository implements StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private int nextId = 1;

    /** An empty store. */
    public InMemoryStudentRepository() {
    }

    /** A store that starts with these students in it. Handy for tests with a known starting point. */
    public InMemoryStudentRepository(List<Student> seed) {
        students.addAll(seed);
        nextId = students.stream().mapToInt(Student::id).max().orElse(0) + 1;
    }

    @Override
    public Optional<Student> findById(int id) {
        return students.stream().filter(s -> s.id() == id).findFirst();
    }

    @Override
    public List<Student> findAll() {
        // A copy. Handing out the internal list would let a caller edit the store from outside.
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
        // Locale.ROOT, not the default locale: in Turkish, "I".toLowerCase() is not "i".
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
            return stored;
        }
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id() == student.id()) {
                students.set(i, student);
                return student;
            }
        }
        throw new IllegalArgumentException("no student with id " + student.id());
    }

    @Override
    public boolean deleteById(int id) {
        return students.removeIf(s -> s.id() == id);
    }

    @Override
    public int count() {
        return students.size();
    }

    @Override
    public String storeName() {
        return "in-memory";
    }
}
