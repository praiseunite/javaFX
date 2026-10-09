package com.aptech.a5.repository;

import com.aptech.a5.Db;
import com.aptech.a5.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * R2 — the store that is a database.   <<< WRITE THIS CLASS >>>
 *
 * <p>This is your A4 work moved behind {@link StudentRepository}. Two things to notice while you
 * move it:
 *
 * <ul>
 *   <li><b>Every method opens its own connection and closes it before returning.</b> No
 *       {@code Connection} field, no {@code Connection} parameter, no {@code Connection} handed
 *       out. That is not a style preference — a shared Connection is what makes a program that
 *       only works when one thing happens at a time.</li>
 *   <li><b>Every {@code SQLException} stops here.</b> Catch it and throw
 *       {@link RepositoryException} with a message a human can read. That is why the interface
 *       declares no checked exception: the callers above you have no idea H2 exists.</li>
 * </ul>
 *
 * <p>Practical notes:
 * <ul>
 *   <li>Read columns by <em>label</em>, not by number.</li>
 *   <li>{@code mark} and {@code course_id} can both be null. Use {@code rs.getBigDecimal(...)}
 *       and {@code rs.getInt(...)} and then {@code rs.wasNull()} — without that check a null mark
 *       silently becomes 0, which reads as a fail.</li>
 *   <li>For a null {@code Integer} or {@code BigDecimal} parameter use
 *       {@code ps.setNull(n, Types.INTEGER)} / {@code Types.DECIMAL}, not {@code setInt}.</li>
 *   <li>{@code save} on a student with id 0 is an INSERT with
 *       {@code Statement.RETURN_GENERATED_KEYS}; read the id back out of
 *       {@code getGeneratedKeys()} and return the stored student.</li>
 *   <li>Order {@code findAll} by {@code id} so the report is reproducible.</li>
 * </ul>
 */
public class H2StudentRepository implements StudentRepository {

    /** The column list, written once so the SELECTs cannot drift apart from each other. */
    protected static final String COLUMNS = "id, name, email, course_id, mark, active";

    protected final String url;

    public H2StudentRepository() {
        this(Db.URL);
    }

    /** Point this store at a different database — SelfCheck uses the throwaway in-memory one. */
    public H2StudentRepository(String url) {
        this.url = url;
    }

    @Override
    public Optional<Student> findById(int id) {
        // TODO R2
        throw new UnsupportedOperationException("R2: H2StudentRepository.findById() not implemented");
    }

    @Override
    public List<Student> findAll() {
        // TODO R2
        throw new UnsupportedOperationException("R2: H2StudentRepository.findAll() not implemented");
    }

    @Override
    public List<Student> findByCourse(int courseId) {
        // TODO R2
        throw new UnsupportedOperationException("R2: H2StudentRepository.findByCourse() not implemented");
    }

    @Override
    public List<Student> findByNameContaining(String fragment) {
        // TODO R2 — LIKE with a ? parameter. The % wildcards go around the VALUE, never in the SQL.
        throw new UnsupportedOperationException("R2: H2StudentRepository.findByNameContaining() not implemented");
    }

    @Override
    public Student save(Student student) {
        // TODO R2
        throw new UnsupportedOperationException("R2: H2StudentRepository.save() not implemented");
    }

    @Override
    public boolean deleteById(int id) {
        // TODO R2 — the rows-changed count is the answer. Do not SELECT first.
        throw new UnsupportedOperationException("R2: H2StudentRepository.deleteById() not implemented");
    }

    @Override
    public int count() {
        // TODO R2
        throw new UnsupportedOperationException("R2: H2StudentRepository.count() not implemented");
    }

    @Override
    public String storeName() {
        // TODO R2 — something like "H2 (file)". It ends up in the report.
        throw new UnsupportedOperationException("R2: H2StudentRepository.storeName() not implemented");
    }
}
