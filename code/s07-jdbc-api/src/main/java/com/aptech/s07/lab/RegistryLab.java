package com.aptech.s07.lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * GUIDED LAB — The Student Registry, moved into a database.
 *
 * You have kept this registry in an ArrayList since Session 1. It vanishes every time you
 * close the program. Today it gets a database of its own: a real table on disk, reached
 * through JDBC, that is still there tomorrow.
 *
 * Build it in the steps the lesson page lists. This file is the finished version, and every
 * step below is one of the ideas from E01-E06 put to work on data you already know:
 *
 *   STEP 1  connect, and create the table if it is not there
 *   STEP 2  insert a student, and read back the id the database chose
 *   STEP 3  find one student by id
 *   STEP 4  search by name with a PARAMETERIZED query - and prove it rejects injection
 *   STEP 5  update a mark, and delete a student
 *   STEP 6  read the schema back out with DatabaseMetaData
 *
 * This is assignment A4 in miniature. Finish this and A4 is the same skills, more methods.
 */
public class RegistryLab {

    /** A student, as the rest of the program sees one. No SQL, no ResultSet - just data. */
    record Student(int id, String name, String email, double mark) {
        @Override public String toString() {
            return String.format("%-4d %-20s %-24s %6.2f", id, name, email, mark);
        }
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("GUIDED LAB - The Student Registry, on a real database");

        // A throwaway in-memory database, so the lab can be run again and again.
        // Swap this one string for Db.H2_URL to keep the data between runs.
        String url = "jdbc:h2:mem:lab;DB_CLOSE_DELAY=-1";

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {
            RegistryDao dao = new RegistryDao(c);

            // ---- STEP 1 -------------------------------------------------------
            System.out.println("\n=== STEP 1 - create the table " + "=".repeat(38));
            dao.createSchema();
            System.out.println("  table students created (or already present)");

            // ---- STEP 2 -------------------------------------------------------
            System.out.println("\n=== STEP 2 - insert, and read back the generated id " + "=".repeat(18));
            int ada = dao.insert("Ada Lovelace", "ada@example.com", 88.50);
            int grace = dao.insert("Grace Hopper", "grace@example.com", 91.00);
            int alan = dao.insert("Alan Turing", "alan@example.com", 76.25);
            System.out.println("  inserted three students, ids: " + ada + ", " + grace + ", " + alan);

            // ---- STEP 3 -------------------------------------------------------
            System.out.println("\n=== STEP 3 - find one student by id " + "=".repeat(29));
            System.out.println("  id " + ada + " -> " + dao.findById(ada));
            System.out.println("  id 9999 -> " + dao.findById(9999) + "   (absence is null, not an error)");

            // ---- STEP 4 -------------------------------------------------------
            System.out.println("\n=== STEP 4 - parameterized search " + "=".repeat(31));
            System.out.println("  search 'Grace':");
            for (Student s : dao.searchByName("Grace")) System.out.println("    " + s);
            System.out.println("  search a SQL-injection attempt \"' OR '1'='1\":");
            List<Student> injected = dao.searchByName("' OR '1'='1");
            System.out.println("    " + injected.size() + " row(s) - the payload is just a name that");
            System.out.println("    matches nothing. The table did not leak.");

            // ---- STEP 5 -------------------------------------------------------
            System.out.println("\n=== STEP 5 - update and delete " + "=".repeat(34));
            int updated = dao.updateMark(alan, 82.75);
            System.out.println("  updated " + updated + " row -> " + dao.findById(alan));
            int deleted = dao.delete(grace);
            System.out.println("  deleted " + deleted + " row (Grace)");
            System.out.println("  all students now:");
            for (Student s : dao.findAll()) System.out.println("    " + s);

            // ---- STEP 6 -------------------------------------------------------
            System.out.println("\n=== STEP 6 - read the schema back with DatabaseMetaData " + "=".repeat(8));
            dao.printSchema();
        }

        System.out.println("\nThe registry is now in a database. Close the program; it will be there");
        System.out.println("tomorrow. That is the whole difference between a List and a table.");
    }

    // ========================================================================
    //  The DAO - every SQL statement in this lab lives here and nowhere else.
    // ========================================================================

    static class RegistryDao {
        private final Connection c;

        RegistryDao(Connection c) { this.c = c; }

        /** STEP 1 — create the table only if it is missing, so a re-run is safe. */
        void createSchema() throws SQLException {
            String sql = "CREATE TABLE IF NOT EXISTS students ("
                    + " id    INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                    + " name  VARCHAR(100) NOT NULL,"
                    + " email VARCHAR(150) UNIQUE,"
                    + " mark  DECIMAL(5,2))";
            try (Statement st = c.createStatement()) {
                st.execute(sql);
            }
        }

        /** STEP 2 — insert and return the id the database assigned. */
        int insert(String name, String email, double mark) throws SQLException {
            String sql = "INSERT INTO students (name, email, mark) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setDouble(3, mark);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : -1;
                }
            }
        }

        /** STEP 3 — find by id, or null. */
        Student findById(int id) throws SQLException {
            String sql = "SELECT id, name, email, mark FROM students WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? read(rs) : null;
                }
            }
        }

        /** STEP 4 — search by name. The ? is what makes the injection attempt harmless. */
        List<Student> searchByName(String name) throws SQLException {
            String sql = "SELECT id, name, email, mark FROM students WHERE name LIKE ?";
            List<Student> out = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + name + "%");    // the % is added to the VALUE, not the SQL
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add(read(rs));
                }
            }
            return out;
        }

        /** Every student, ordered by name. */
        List<Student> findAll() throws SQLException {
            List<Student> out = new ArrayList<>();
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, name, email, mark FROM students ORDER BY name")) {
                while (rs.next()) out.add(read(rs));
            }
            return out;
        }

        /** STEP 5 — update one mark. Returns the number of rows changed. */
        int updateMark(int id, double mark) throws SQLException {
            String sql = "UPDATE students SET mark = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDouble(1, mark);
                ps.setInt(2, id);
                return ps.executeUpdate();
            }
        }

        /** STEP 5 — delete one student. Returns the number of rows removed. */
        int delete(int id) throws SQLException {
            String sql = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate();
            }
        }

        /** STEP 6 — the table, described by the database itself. */
        void printSchema() throws SQLException {
            try (ResultSet rs = c.getMetaData().getColumns(null, null, "STUDENTS", null)) {
                while (rs.next()) {
                    System.out.printf("    %-10s %-20s %s%n",
                            rs.getString("COLUMN_NAME"),
                            rs.getString("TYPE_NAME"),
                            "NULL".equals(rs.getString("IS_NULLABLE")) ? "NOT NULL" : "");
                }
            }
        }

        /** One row of the ResultSet -> one Student. Kept in one place so it cannot drift. */
        private Student read(ResultSet rs) throws SQLException {
            return new Student(rs.getInt("id"), rs.getString("name"),
                               rs.getString("email"), rs.getDouble("mark"));
        }
    }
}
