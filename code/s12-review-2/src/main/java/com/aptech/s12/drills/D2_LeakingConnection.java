package com.aptech.s12.drills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DRILL 2 — Book Session 6: JDBC Resource Management.
 *
 * Database connections and prepared statements are scarce operating-system resources.
 * If an exception is thrown before close() is reached, the connection handle leaks permanently.
 * Try-with-resources closes every resource on normal exit AND exception paths.
 */
public class D2_LeakingConnection {

    private static final String URL = "jdbc:h2:mem:s12_d2;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        System.out.println("Drill 2 - JDBC resource cleanup: manual close() vs try-with-resources");
        System.out.println();

        System.out.println("BROKEN - manual close() after a statement that throws an exception:");
        Connection leakedConn = runBroken();
        System.out.println("  connection.isClosed() = " + leakedConn.isClosed());
        System.out.println("  -> The exception skipped close(); the connection remained open and leaked.");
        leakedConn.close(); // clean up after test

        System.out.println();
        System.out.println("FIXED - try-with-resources managing the Connection and PreparedStatement:");
        Connection closedConn = runFixed();
        System.out.println("  connection.isClosed() = " + closedConn.isClosed());
        System.out.println("  -> Resource released automatically on the exception path.");
    }

    private static Connection runBroken() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            // Deliberate syntax error: throws SQLException
            stmt.executeQuery("SELECT * FROM non_existent_table_xyz");
            conn.close(); // unreachable due to exception!
        } catch (SQLException e) {
            // Error logged, but connection was not closed
        }
        return conn;
    }

    private static Connection runFixed() throws SQLException {
        Connection captured;
        try (Connection conn = DriverManager.getConnection(URL)) {
            captured = conn;
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM non_existent_table_xyz")) {
                ps.executeQuery();
            } catch (SQLException e) {
                // PreparedStatement closed by its own try-with-resources
            }
        }
        // At this line, the outer try block has completed and closed conn
        return captured;
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS test_data (id INT)");
        }
    }
}
