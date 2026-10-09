package com.aptech.a5.repository;

import com.aptech.a5.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * R1 — the store that is just a list.   <<< WRITE THIS CLASS >>>
 *
 * <p>It has no database, no file, no driver and no connection string. It is also completely
 * correct, because {@link StudentRepository} never promised persistence — it promised six
 * questions and a save. That is what makes this class the most useful one in the assignment:
 * it is the store Session 10 will test against, with no database to install and no file to clean
 * up between tests.
 *
 * <p>Notes:
 * <ul>
 *   <li>{@link #save} is where the id is decided. {@code id == 0} means "insert"; the store picks
 *       the next number and returns the stored student. Any other id means "replace that one",
 *       and an id that is not there is a mistake, not a silent insert.</li>
 *   <li>{@link #findAll} must return a stable order (the order they were added will do) or two
 *       runs of the same program print different reports.</li>
 *   <li>Do not hand out the internal list itself. {@code List.copyOf(...)} or a new
 *       {@code ArrayList<>(...)} — otherwise a caller can reach in and change your store.</li>
 * </ul>
 */
public class InMemoryStudentRepository implements StudentRepository {

    protected final List<Student> students = new ArrayList<>();
    protected int nextId = 1;

    /** An empty store. */
    public InMemoryStudentRepository() {
    }

    /** A store that starts with these students already in it — handy for tests and demos. */
    public InMemoryStudentRepository(List<Student> seed) {
        students.addAll(seed);
        nextId = students.stream().mapToInt(Student::id).max().orElse(0) + 1;
    }

    @Override
    public Optional<Student> findById(int id) {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.findById() not implemented");
    }

    @Override
    public List<Student> findAll() {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.findAll() not implemented");
    }

    @Override
    public List<Student> findByCourse(int courseId) {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.findByCourse() not implemented");
    }

    @Override
    public List<Student> findByNameContaining(String fragment) {
        // TODO R1 — case-insensitive. Use Locale.ROOT when you lowercase, not the default locale.
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.findByNameContaining() not implemented");
    }

    @Override
    public Student save(Student student) {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.save() not implemented");
    }

    @Override
    public boolean deleteById(int id) {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.deleteById() not implemented");
    }

    @Override
    public int count() {
        // TODO R1
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.count() not implemented");
    }

    @Override
    public String storeName() {
        // TODO R1 — "in-memory" is enough. It ends up in the report.
        throw new UnsupportedOperationException("R1: InMemoryStudentRepository.storeName() not implemented");
    }
}
