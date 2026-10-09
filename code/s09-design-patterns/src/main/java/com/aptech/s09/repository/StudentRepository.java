package com.aptech.s09.repository;

import com.aptech.s09.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Everything the registry needs from "a place that keeps students" — and nothing about how
 * any particular place does it.
 *
 * <p>Read the import list above: there is no {@code java.sql} in it. That is deliberate, and
 * it is the entire point of this interface. A caller holding a {@code StudentRepository} cannot
 * write SQL even if it wants to, because SQL is not part of the type.
 *
 * <p>Look at what is <em>not</em> declared either: no method throws {@code SQLException}.
 * If it did, every caller would have to handle a database failure even when the store is a
 * list in memory, and {@link InMemoryStudentRepository} would be forced to invent one. The
 * implementations that <em>can</em> fail wrap the failure in {@link RepositoryException},
 * which is unchecked — so it travels without being declared, and still stops the program.
 */
public interface StudentRepository {

    /** The student with this id, or an empty Optional — never null. */
    Optional<Student> findById(int id);

    /** Every student, in id order. */
    List<Student> findAll();

    /** The students on one course, in id order. */
    List<Student> findByCourse(int courseId);

    /** A case-insensitive search on the name. An empty fragment matches everyone. */
    List<Student> findByNameContaining(String fragment);

    /**
     * Inserts when the student's id is 0, updates when it is not. The returned student is
     * the stored one — for an insert that means the id the store chose.
     */
    Student save(Student student);

    /** True when a row was actually removed. */
    boolean deleteById(int id);

    int count();

    /**
     * The name printed by the examples, so the output says which store answered. A real
     * project would put this in a log line instead.
     */
    String storeName();
}
