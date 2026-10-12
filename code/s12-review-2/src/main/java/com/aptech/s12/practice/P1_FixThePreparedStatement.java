package com.aptech.s12.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PRACTICE 1 (Easy) — Fix the Vulnerable SQL Query.
 *
 * Take a method that builds dynamic SQL via string concatenation and
 * replace it with a type-safe PreparedStatement using parameter markers (?).
 */
public class P1_FixThePreparedStatement {

    private static final String URL = "jdbc:h2:mem:s12_p1;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        String department = "Engineering";
        int minYear = 2;

        System.out.println("Practice 1 (Easy) - Fix the vulnerable SQL query");
        System.out.println();

        System.out.println("BROKEN - String concatenation:");
        List<String> brokenResults = queryStudentsBroken(department, minYear);
        System.out.println("  found: " + brokenResults);

        System.out.println();
        System.out.println("FIXED - PreparedStatement with ? placeholders:");
        List<String> fixedResults = queryStudentsFixed(department, minYear);
        System.out.println("  found: " + fixedResults);
        System.out.println("  PASS - Both parameters bound safely with setString() and setInt().");
    }

    /** BROKEN: Vulnerable to SQL injection. */
    static List<String> queryStudentsBroken(String dept, int minYear) throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM student_roster WHERE dept = '" + dept + "' AND study_year >= " + minYear;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }

    /** FIXED: PreparedStatement with index parameter markers. */
    static List<String> queryStudentsFixed(String dept, int minYear) throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM student_roster WHERE dept = ? AND study_year >= ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept);
            ps.setInt(2, minYear);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        }
        return names;
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE student_roster (id INT PRIMARY KEY, name VARCHAR(50), dept VARCHAR(50), study_year INT)");
            stmt.execute("INSERT INTO student_roster VALUES (1, 'Ada', 'Engineering', 3), (2, 'Bob', 'Engineering', 1), (3, 'Charlie', 'Engineering', 4)");
        }
    }
}
