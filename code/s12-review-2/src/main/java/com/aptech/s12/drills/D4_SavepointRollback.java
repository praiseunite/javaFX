package com.aptech.s12.drills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DRILL 4 — Book Session 7: Advanced JDBC &amp; Savepoints.
 *
 * In batch transactions, one bad record can cause a full rollback, losing good records.
 * A Savepoint marks an intermediate checkpoint within a transaction, allowing partial rollback
 * without discarding preceding successful updates.
 */
public class D4_SavepointRollback {

    private static final String URL = "jdbc:h2:mem:s12_d4;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        double[] batchAmounts = {100.0, -50.0, 150.0}; // -50.0 violates amount > 0 CHECK constraint

        System.out.println("Drill 4 - Transaction Savepoints: full rollback vs partial rollback");
        System.out.println("Processing payment batch: [100.0, -50.0, 150.0]");
        System.out.println();

        System.out.println("WITHOUT SAVEPOINT - one bad amount aborts the whole batch:");
        runWithoutSavepoint(batchAmounts);
        System.out.println("  payments recorded: " + readPayments());
        System.out.println("  -> The entire batch was cancelled; valid payment $100.0 was lost.");

        resetData();

        System.out.println();
        System.out.println("WITH SAVEPOINT - checkpoints protect valid updates:");
        runWithSavepoints(batchAmounts);
        System.out.println("  payments recorded: " + readPayments());
        System.out.println("  -> Bad payment -50.0 was rolled back to its savepoint; $100.0 and $150.0 survived.");
    }

    private static void runWithoutSavepoint(double[] amounts) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < amounts.length; i++) {
                    insertPayment(conn, i + 1, amounts[i]);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); // Drops EVERYTHING!
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static void runWithSavepoints(double[] amounts) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < amounts.length; i++) {
                    Savepoint sp = conn.setSavepoint("sp_" + i);
                    try {
                        insertPayment(conn, i + 1, amounts[i]);
                    } catch (SQLException e) {
                        conn.rollback(sp); // Roll back only this single item!
                    }
                }
                conn.commit();
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static void insertPayment(Connection conn, int id, double amount) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO payments (id, amount) VALUES (?, ?)")) {
            ps.setInt(1, id);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
    }

    private static List<Double> readPayments() throws SQLException {
        List<Double> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT amount FROM payments ORDER BY id")) {
            while (rs.next()) {
                list.add(rs.getDouble(1));
            }
        }
        return list;
    }

    private static void resetData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM payments");
        }
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS payments (id INT PRIMARY KEY, amount DECIMAL(10,2) CHECK (amount > 0))");
            resetData();
        }
    }
}
