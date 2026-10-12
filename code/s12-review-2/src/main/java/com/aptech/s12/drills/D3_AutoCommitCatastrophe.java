package com.aptech.s12.drills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DRILL 3 — Book Session 7: Advanced JDBC &amp; Transaction Atomicity.
 *
 * In JDBC, connections default to autoCommit = true.
 * In a multi-step business process (e.g. enrolment + fee payment), if step 1 succeeds
 * and step 2 fails, autoCommit commits step 1 immediately, corrupting the database.
 * The fix is setAutoCommit(false), with commit() on success and rollback() on catch.
 */
public class D3_AutoCommitCatastrophe {

    private static final String URL = "jdbc:h2:mem:s12_d3;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        System.out.println("Drill 3 - Transaction atomicity: auto-commit vs explicit transaction boundary");
        System.out.println();

        System.out.println("BROKEN - autoCommit = true (default). Step 1 commits before Step 2 fails:");
        runBroken();
        System.out.println("  enrolments in DB: " + countRows("enrolments"));
        System.out.println("  fee_payments in DB: " + countRows("fee_payments"));
        System.out.println("  -> Enrolment #1 was committed alone: an orphaned record with no payment.");

        resetData();

        System.out.println();
        System.out.println("FIXED - setAutoCommit(false) with commit() and rollback():");
        runFixed();
        System.out.println("  enrolments in DB: " + countRows("enrolments"));
        System.out.println("  fee_payments in DB: " + countRows("fee_payments"));
        System.out.println("  -> Atomic rollback: neither record exists. All-or-nothing guaranteed.");
    }

    private static void runBroken() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            // Step 1: Insert enrolment (succeeds and auto-commits immediately!)
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO enrolments (id, student_id, course_name) VALUES (1, 101, 'Java Foundations')")) {
                ps1.executeUpdate();
            }

            // Step 2: Insert fee payment (fails due to database constraint: amount > 0)
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO fee_payments (id, enrolment_id, amount) VALUES (1, 1, -50.0)")) {
                ps2.executeUpdate();
            } catch (SQLException e) {
                // Step 2 threw, but Step 1 is already permanently committed!
            }
        }
    }

    private static void runFixed() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // Begin explicit transaction boundary
            try {
                // Step 1: Insert enrolment
                try (PreparedStatement ps1 = conn.prepareStatement(
                        "INSERT INTO enrolments (id, student_id, course_name) VALUES (1, 101, 'Java Foundations')")) {
                    ps1.executeUpdate();
                }

                // Step 2: Insert fee payment (fails due to constraint)
                try (PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO fee_payments (id, enrolment_id, amount) VALUES (1, 1, -50.0)")) {
                    ps2.executeUpdate();
                }

                conn.commit(); // Only reached if both steps succeed
            } catch (SQLException e) {
                conn.rollback(); // Cancel all modifications made since setAutoCommit(false)
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static int countRows(String table) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static void resetData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM fee_payments");
            stmt.execute("DELETE FROM enrolments");
        }
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS enrolments (id INT PRIMARY KEY, student_id INT, course_name VARCHAR(50))");
            stmt.execute("CREATE TABLE IF NOT EXISTS fee_payments (id INT PRIMARY KEY, enrolment_id INT, amount DECIMAL(10,2) CHECK (amount > 0))");
            resetData();
        }
    }
}
