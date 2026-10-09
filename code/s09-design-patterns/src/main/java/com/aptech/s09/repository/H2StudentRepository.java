package com.aptech.s09.repository;

import com.aptech.s09.Db;
import com.aptech.s09.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The same interface, backed by the H2 database — and the <em>only</em> class in Session 9
 * that mentions {@code java.sql}.
 *
 * <p>Everything Session 7 taught about {@code PreparedStatement} is still true here. What has
 * changed is where it lives: the SQL is in one class, behind a type that does not admit SQL
 * exists. When someone asks "which classes touch the database?", the answer is a file name,
 * not a search.
 */
public final class H2StudentRepository implements StudentRepository {

    /** Written once, so the SELECT list and the reader below cannot drift apart. */
    private static final String COLUMNS = "id, name, email, course_id, mark, active";

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM students WHERE id = ?";
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(read(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("findById(" + id + ") failed: " + Db.firstLine(e), e);
        }
    }

    @Override
    public List<Student> findAll() {
        return query("SELECT " + COLUMNS + " FROM students ORDER BY id");
    }

    @Override
    public List<Student> findByCourse(int courseId) {
        return query("SELECT " + COLUMNS + " FROM students WHERE course_id = ? ORDER BY id", courseId);
    }

    @Override
    public List<Student> findByNameContaining(String fragment) {
        return query("SELECT " + COLUMNS + " FROM students WHERE LOWER(name) LIKE ? ORDER BY id",
                "%" + fragment.toLowerCase(Locale.ROOT) + "%");
    }

    @Override
    public Student save(Student student) {
        try (Connection c = Db.open()) {
            if (student.id() == 0) {
                return insert(c, student);
            }
            return update(c, student);
        } catch (SQLException e) {
            throw new RepositoryException("save(" + student.name() + ") failed: " + Db.firstLine(e), e);
        }
    }

    private static Student insert(Connection c, Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, course_id, mark, active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, student);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                return new Student(id, student.name(), student.email(), student.courseId(),
                        student.mark(), student.active());
            }
        }
    }

    private static Student update(Connection c, Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, course_id = ?, mark = ?, active = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, student);
            ps.setInt(6, student.id());
            if (ps.executeUpdate() == 0) {
                throw new RepositoryException("no student with id " + student.id());
            }
            return student;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("deleteById(" + id + ") failed: " + Db.firstLine(e), e);
        }
    }

    @Override
    public int count() {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM students");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RepositoryException("count() failed: " + Db.firstLine(e), e);
        }
    }

    @Override
    public String storeName() {
        return "H2 (file)";
    }

    // ------------------------------------------------------------------ helpers

    private List<Student> query(String sql, Object... params) {
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(read(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RepositoryException("query failed: " + Db.firstLine(e), e);
        }
    }

    /** One row, read into the record. Two nullable columns need two {@code wasNull} checks. */
    private static Student read(ResultSet rs) throws SQLException {
        int courseId = rs.getInt("course_id");
        Integer course = rs.wasNull() ? null : courseId;
        return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                course, rs.getBigDecimal("mark"), rs.getBoolean("active"));
    }

    private static void bind(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.name());
        ps.setString(2, s.email());
        if (s.courseId() == null) {
            ps.setNull(3, Types.INTEGER);
        } else {
            ps.setInt(3, s.courseId());
        }
        if (s.mark() == null) {
            ps.setNull(4, Types.DECIMAL);
        } else {
            ps.setBigDecimal(4, s.mark());
        }
        ps.setBoolean(5, s.active());
    }
}
