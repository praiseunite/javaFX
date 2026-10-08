package com.aptech.s07;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * E02 — the two processing models the manual names: two-tier and three-tier.
 *
 * <p>The difference is not a difference in JDBC at all. It is a difference in <em>where the
 * SQL lives</em>, and it shows up the moment a second part of the program needs the same
 * query.
 *
 * <pre>
 *   TWO-TIER     application  ->  database
 *                SQL written inline, in the method that happens to need it.
 *
 *   THREE-TIER   application  ->  DAO  ->  database
 *                SQL lives in one class that only knows about the table;
 *                callers ask for objects and never see a ResultSet.
 * </pre>
 *
 * <p>Read the two halves of this program in order. The first is shorter. The second is the
 * one you can test, reuse and change without hunting for every copy of a SELECT.
 */
public class E02_ProcessingModels {

    /** The thing both versions return. Neither tier cares how it was fetched. */
    record Student(int id, String name, String email, double mark) {
        @Override public String toString() {
            return String.format("%-16s %-24s %6.2f", name, email, mark);
        }
    }

    public static void main(String[] args) throws SQLException {

        System.out.println("E02 - two processing models: two-tier and three-tier");

        twoTier();
        threeTier();

        System.out.println(Db.rule("the difference, in one line"));
        System.out.println("  Two-tier:   if you need \"the honours students\" twice, you write");
        System.out.println("              the WHERE clause twice - and fix it twice.");
        System.out.println("  Three-tier: you write it once, in the DAO, and call it twice.");
        System.out.println("  The database cannot tell the difference. Your next change can.");
    }

    // ======================================================================
    //  TWO-TIER — application talks straight to the database
    // ======================================================================

    private static void twoTier() throws SQLException {
        System.out.println(Db.rule("two-tier - SQL written where it is needed"));

        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, name, email, mark FROM students WHERE mark >= ? ORDER BY mark DESC")) {

            ps.setDouble(1, 80.0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(rs.getInt("id"), rs.getString("name"),
                                            rs.getString("email"), rs.getDouble("mark"));
                    System.out.println("  " + s);
                }
            }
        }
        System.out.println("  ...and if the office asks for this list again, the SQL is copied.");
    }

    // ======================================================================
    //  THREE-TIER — a DAO owns the SQL; callers see objects
    // ======================================================================

    private static void threeTier() throws SQLException {
        System.out.println(Db.rule("three-tier - a DAO owns the SQL"));

        try (Connection c = Db.open()) {
            StudentDao dao = new StudentDao(c);

            System.out.println("  honours students (mark >= 80):");
            for (Student s : dao.findByMinMark(80.0)) System.out.println("    " + s);

            System.out.println("  and the same query again, one call:");
            System.out.println("    " + dao.findByMinMark(80.0).size() + " rows, no SQL repeated");

            System.out.println("  lookup one student by id:");
            System.out.println("    " + dao.findById(1));

            System.out.println("  a student who does not exist:");
            System.out.println("    " + dao.findById(9999));
        }
    }

    /**
     * The DAO — the middle tier. Note what is NOT here: no URL, no user, no password,
     * no DriverManager. It is handed a Connection, and it knows one table.
     */
    static class StudentDao {
        private final Connection c;

        StudentDao(Connection c) { this.c = c; }

        List<Student> findByMinMark(double min) throws SQLException {
            String sql = "SELECT id, name, email, mark FROM students WHERE mark >= ? ORDER BY mark DESC";
            List<Student> out = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDouble(1, min);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new Student(rs.getInt("id"), rs.getString("name"),
                                            rs.getString("email"), rs.getDouble("mark")));
                    }
                }
            }
            return out;
        }

        /** Returns null when there is no such row — not an exception. Absence is normal. */
        Student findById(int id) throws SQLException {
            String sql = "SELECT id, name, email, mark FROM students WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next()
                            ? new Student(rs.getInt("id"), rs.getString("name"),
                                          rs.getString("email"), rs.getDouble("mark"))
                            : null;
                }
            }
        }
    }
}
