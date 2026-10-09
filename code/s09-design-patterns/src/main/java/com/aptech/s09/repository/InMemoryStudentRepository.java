package com.aptech.s09.repository;

import com.aptech.s09.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A repository that keeps students in a list, in memory, and forgets them when the program
 * ends.
 *
 * <p>It looks like a toy. It is not: it is the reason a test can exercise the whole registry
 * — enrolling, fee policies, listeners, reports — in a few milliseconds, with no database,
 * no file to clean up, and no chance that one test sees another test's data.
 *
 * <p>Notice there is no {@code try}, no {@code catch} and no {@code throws} anywhere in this
 * class. That is what {@link StudentRepository} bought us: the happy implementation has
 * nothing to be sorry about.
 */
public final class InMemoryStudentRepository implements StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private int nextId = 1;

    /** An empty store. */
    public InMemoryStudentRepository() {
    }

    /**
     * A store pre-loaded from anywhere — most usefully from a real repository, which is how
     * Example 2 puts the same eight students in both stores.
     *
     * <p>Notice what this does <em>not</em> do: call {@link #save}. That method's contract is
     * "insert when the id is 0, update when it is not", so seeding through it would demand the
     * students already be here. Copied students arrive with their ids, so they go straight in —
     * and {@code nextId} moves past the highest of them, or the next enrolment would be handed
     * an id already in use.
     */
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
