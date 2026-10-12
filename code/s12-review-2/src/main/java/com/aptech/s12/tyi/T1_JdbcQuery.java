package com.aptech.s12.tyi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * TRY IT YOURSELF 1 — Book Session 6: The JDBC API.
 *
 * Task: Connect to an embedded H2 database, create a courses table,
 * insert records safely using PreparedStatement, query courses by credit threshold,
 * and inspect database metadata (driver name, product version, columns).
 */
public class T1_JdbcQuery {

    private static final String URL = "jdbc:h2:mem:s12_tyi1;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        System.out.println("Try It Yourself 1 - JDBC API: PreparedStatement & MetaData");

        try (Connection conn = DriverManager.getConnection(URL)) {
            // 1. Create table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE courses (id INT PRIMARY KEY, title VARCHAR(100), credits INT)");
            }

            // 2. Insert records using PreparedStatement
            String insertSql = "INSERT INTO courses (id, title, credits) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                addCourse(ps, 1, "Java Foundations", 4);
                addCourse(ps, 2, "Database Systems & JDBC", 3);
                addCourse(ps, 3, "Web Development Basics", 2);
            }

            // 3. Query courses with credits >= 3
            System.out.println();
            System.out.println("Courses with credits >= 3 (parameterized query):");
            String querySql = "SELECT id, title, credits FROM courses WHERE credits >= ? ORDER BY credits DESC";
            try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setInt(1, 3);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("  [%d] %-25s (%d credits)%n",
                                rs.getInt("id"), rs.getString("title"), rs.getInt("credits"));
                    }
                }
            }

            // 4. Inspect DatabaseMetaData
            System.out.println();
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Database Metadata:");
            System.out.println("  Database Product : " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("  Driver Name      : " + meta.getDriverName());

            List<String> cols = new ArrayList<>();
            try (ResultSet rs = meta.getColumns(null, null, "COURSES", null)) {
                while (rs.next()) {
                    cols.add(rs.getString("COLUMN_NAME") + " (" + rs.getString("TYPE_NAME") + ")");
                }
            }
            System.out.println("  Table columns    : " + cols);
        }
    }

    private static void addCourse(PreparedStatement ps, int id, String title, int credits) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setInt(3, credits);
        ps.executeUpdate();
    }
}
