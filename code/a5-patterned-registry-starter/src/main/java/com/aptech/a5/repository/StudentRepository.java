package com.aptech.a5.repository;

import com.aptech.a5.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * The seam. GIVEN — do not change it, and do not add a method to it until R1–R8 all pass.
 *
 * <p>Notice what is missing. There is no {@code java.sql} import. No method declares a checked
 * exception. Nothing here says "database", "table", "row" or "SQL" — because the classes above
 * this interface are not supposed to know any of that. Everything that DOES know it lives in the
 * classes that {@code implements} this file.
 *
 * <p>That is the whole point of the exercise. If you find yourself wanting to add
 * {@code throws SQLException} here to make R2 compile, the problem is not this interface.
 */
public interface StudentRepository {

    /** The student with this id, or an empty Optional. Absence is normal: no null, no throw. */
    Optional<Student> findById(int id);

    /** Every student, in a stable order, so two runs print the same thing. */
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

    /** True when a row/file/entry was actually removed. */
    boolean deleteById(int id);

    int count();

    /** Which store this is, in words, for the report. e.g. "in-memory", "H2 (file)". */
    String storeName();
}
