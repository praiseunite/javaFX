package com.aptech.s12lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * The Enterprise Clinic service class.
 *
 * This class compiles and runs, but contains five planted faults (F1–F5)
 * spanning Book Sessions 6 to 10.
 *
 * Your goal: repair the five methods so that SelfCheck reports:
 *   5 passed, 0 failed.
 */
public class EnterpriseClinic {

    public static final String DB_URL = "jdbc:h2:mem:clinic_db;DB_CLOSE_DELAY=-1";

    /**
     * FAULT F1 (Session 6 — JDBC API & SQL Injection).
     * Searches for a student by exact name.
     *
     * In the starter: Uses string concatenation, leaving the query vulnerable to SQL injection.
     */
    public static List<String> searchStudent(String query) throws SQLException {
        List<String> results = new ArrayList<>();
        // FAULT: Concatenation allows query manipulation.
        // SelfCheck passes "' OR '1'='1" and expects 0 results or exact match, not the whole table.
        String sql = "SELECT name FROM clinic_students WHERE name = '" + query + "'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(rs.getString("name"));
            }
        }
        return results;
    }

    /**
     * FAULT F2 (Session 7 — Advanced JDBC & Transaction Atomicity).
     * Enrols a student in a course and records their fee.
     * Must be all-or-nothing: if the fee recording fails, the enrolment MUST be rolled back.
     *
     * In the starter: Leaves autoCommit enabled (true). When fee insertion fails,
     * the enrolment remains committed in the database.
     */
    public static boolean enrolWithFee(int studentId, String course, double fee) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // FAULT: autoCommit is left as true (default)!
            // Step 1: Enrolment commits immediately
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO clinic_enrolments (student_id, course) VALUES (?, ?)")) {
                ps1.setInt(1, studentId);
                ps1.setString(2, course);
                ps1.executeUpdate();
            }

            // Step 2: Record fee (will fail if fee < 0 due to database check constraint)
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO clinic_fees (student_id, amount) VALUES (?, ?)")) {
                ps2.setInt(1, studentId);
                ps2.setDouble(2, fee);
                ps2.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            // An exception occurred, but Step 1 is already permanently committed on the DB!
            return false;
        }
    }

    /**
     * FAULT F3 (Session 8 — Design Patterns & i18n Fallback).
     * Looks up a localized message for a given language code.
     * If the key is not defined in the requested language, it MUST fall back to English ("en").
     *
     * In the starter: Looks only in the target language and returns null on missing keys.
     */
    public static String getLocalizedStatus(String key, String lang) {
        Map<String, Map<String, String>> bundles = Map.of(
                "en", Map.of(
                        "status.enrolled", "Enrolled",
                        "status.withdrawn", "Withdrawn",
                        "status.graduated", "Graduated"),
                "fr", Map.of(
                        "status.enrolled", "Inscrit",
                        "status.graduated", "Diplômé")
                // Notice: "status.withdrawn" is missing in French on purpose
        );

        Map<String, String> targetBundle = bundles.get(lang);
        if (targetBundle == null) {
            return null;
        }

        // FAULT: Does not fall back to English ("en") if key is missing in target language
        return targetBundle.get(key);
    }

    /**
     * FAULT F4 (Session 9 — Unit Testing & Boundary Validation).
     * Validates whether a student qualifies for honors scholarship:
     * Student must have GPA >= 3.5 AND credit hours >= 12.
     *
     * In the starter: Uses || instead of &&, allowing part-time students (< 12 credits) to qualify.
     */
    public static boolean validateScholarshipEligibility(double gpa, int creditHours) {
        // FAULT: Logical OR used instead of AND
        return gpa >= 3.5 || creditHours >= 12;
    }

    /**
     * FAULT F5 (Session 10 — Java Data Structures & Stack 1-based indexing).
     * Checks if the given page is at the very TOP of the navigation history stack.
     *
     * In the starter: Assumes 0-based index (history.search(page) == 0).
     */
    public static boolean isTopNavigationPage(Stack<String> history, String page) {
        // FAULT: Stack.search() returns 1-based distance from top (1 = top), NOT 0!
        return history.search(page) == 0;
    }

    /** Helper to initialize the in-memory test database schema. */
    public static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS clinic_students (id INT PRIMARY KEY, name VARCHAR(50))");
            stmt.execute("CREATE TABLE IF NOT EXISTS clinic_enrolments (student_id INT, course VARCHAR(50))");
            stmt.execute("CREATE TABLE IF NOT EXISTS clinic_fees (student_id INT, amount DECIMAL(10,2) CHECK (amount > 0))");

            stmt.execute("DELETE FROM clinic_fees");
            stmt.execute("DELETE FROM clinic_enrolments");
            stmt.execute("DELETE FROM clinic_students");

            stmt.execute("INSERT INTO clinic_students VALUES (1, 'Ada Lovelace'), (2, 'Charles Babbage'), (3, 'Grace Hopper')");
        }
    }
}
