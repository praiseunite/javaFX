package com.aptech.s12.drills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DRILL 1 — Book Session 6: The JDBC API &amp; SQL Injection.
 *
 * Concatenating user text into raw SQL strings allows attackers to alter query logic.
 * PreparedStatement pre-compiles the SQL command and passes arguments as literal data values.
 */
public class D1_SqlInjection {

    private static final String URL = "jdbc:h2:mem:s12_d1;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        String maliciousInput = "' OR '1'='1";

        System.out.println("Drill 1 - SQL injection: concatenation vs PreparedStatement");
        System.out.println("Searching for student name: " + maliciousInput);
        System.out.println();

        System.out.println("BROKEN - concatenated query: \"SELECT name FROM students WHERE name = '\" + input + \"'\"");
        List<String> leaked = brokenSearch(maliciousInput);
        System.out.println("  students returned: " + leaked.size() + " -> " + leaked);
        System.out.println("  -> Query structure was altered: '1'='1' dumped every record in the table.");
        System.out.println();

        System.out.println("FIXED - PreparedStatement with parameter marker (?):");
        List<String> secured = securedSearch(maliciousInput);
        System.out.println("  students returned: " + secured.size() + " -> " + secured);
        System.out.println("  -> The database engine treated the input as a literal string value.");
    }

    private static List<String> brokenSearch(String input) throws SQLException {
        List<String> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT name FROM students WHERE name = '" + input + "'";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
            }
        }
        return results;
    }

    private static List<String> securedSearch(String input) throws SQLException {
        List<String> results = new ArrayList<>();
        String sql = "SELECT name FROM students WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, input);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
            }
        }
        return results;
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY, name VARCHAR(50))");
            stmt.execute("DELETE FROM students");
            stmt.execute("INSERT INTO students VALUES (1, 'Ada Lovelace'), (2, 'James Gosling'), (3, 'Alan Turing')");
        }
    }
}
