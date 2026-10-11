package com.aptech.s10.repository;

import com.aptech.s10.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * The seam. GIVEN.
 *
 * <p>This interface is the reason Session 10 can test two different stores with one set of
 * tests. Notice what is <em>not</em> here: no {@code java.sql}, no checked exception, no
 * mention of files or tables. Any class that can answer these six questions and accept a
 * {@code save} is a student repository, and the tests below cannot tell — and must not care —
 * which one they were handed.
 *
 * <p>That is what {@link StudentRepository} buys: <strong>a test written against the interface
 * tests every implementation of it</strong>. Write the suite once, run it twice.
 */
public interface StudentRepository {

    /** The student with this id, or an empty Optional. Absence is normal: no null, no throw. */
    Optional<Student> findById(int id);

    /** Every student, in a stable order, so two runs give the same answer. */
    List<Student> findAll();

    /** Every student on one course. */
    List<Student> findByCourse(int courseId);

    /** Every student whose name contains the fragment, case-insensitively. */
    List<Student> findByNameContaining(String fragment);

    /**
     * Inserts when {@code student.id() == 0} and returns the stored student with the id the
     * store chose. Updates when the id is already set. The caller never invents an id.
     */
    Student save(Student student);

    /** True when something was actually removed. */
    boolean deleteById(int id);

    int count();

    /** Which store this is, in words — "in-memory", "file". It ends up in test names. */
    String storeName();
}
