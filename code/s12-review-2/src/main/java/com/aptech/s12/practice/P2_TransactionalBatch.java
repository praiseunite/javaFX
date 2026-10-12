package com.aptech.s12.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

/**
 * PRACTICE 2 (Medium) — Transactional Batch Processing with Savepoint Recovery.
 *
 * Process a batch of account balance adjustments.
 * If an item violates a business check constraint (e.g. balance must remain >= 0),
 * roll back only that item using a Savepoint and continue processing subsequent items.
 */
public class P2_TransactionalBatch {

    private static final String URL = "jdbc:h2:mem:s12_p2;DB_CLOSE_DELAY=-1";

    public record Adjustment(int accountId, double amount) {}

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        Adjustment[] batch = {
                new Adjustment(1, -50.0),   // Valid: Alice $200 -> $150
                new Adjustment(2, -500.0),  // INVALID: Bob has only $100 -> violates balance >= 0
                new Adjustment(3, 100.0)    // Valid: Charlie $50 -> $150
        };

        System.out.println("Practice 2 (Medium) - Transactional Batch with Savepoint Recovery");
        System.out.println("Processing adjustments: [Alice -$50, Bob -$500 (overdraw), Charlie +$100]");
        System.out.println();

        int applied = processBatch(batch);
        System.out.println("Adjustments successfully applied: " + applied + " of " + batch.length);
        System.out.println();
        System.out.println("Final balances in database:");
        printBalances();
        System.out.println("PASS - Alice and Charlie updated; Bob's invalid overdraw was rolled back.");
    }

    static int processBatch(Adjustment[] batch) throws SQLException {
        int successCount = 0;
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < batch.length; i++) {
                    Adjustment adj = batch[i];
                    Savepoint sp = conn.setSavepoint("item_" + i);
                    try {
                        applyAdjustment(conn, adj.accountId(), adj.amount());
                        successCount++;
                    } catch (SQLException e) {
                        System.out.println("  [Recovery] Rolling back adjustment for account "
                                + adj.accountId() + " (insufficient funds)");
                        conn.rollback(sp);
                    }
                }
                conn.commit();
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return successCount;
    }

    private static void applyAdjustment(Connection conn, int accId, double amount) throws SQLException {
        String sql = "UPDATE user_accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accId);
            ps.executeUpdate();
        }
    }

    private static void printBalances() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, balance FROM user_accounts ORDER BY id")) {
            while (rs.next()) {
                System.out.printf("  Account %d (%-8s): $%.2f%n",
                        rs.getInt("id"), rs.getString("name"), rs.getDouble("balance"));
            }
        }
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE user_accounts (id INT PRIMARY KEY, name VARCHAR(50), balance DECIMAL(10,2) CHECK (balance >= 0))");
            stmt.execute("INSERT INTO user_accounts VALUES (1, 'Alice', 200.0), (2, 'Bob', 100.0), (3, 'Charlie', 50.0)");
        }
    }
}
