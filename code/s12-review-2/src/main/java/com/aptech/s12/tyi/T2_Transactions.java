package com.aptech.s12.tyi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

/**
 * TRY IT YOURSELF 2 — Book Session 7: Advanced JDBC.
 *
 * Task: Implement an atomic financial transfer between two accounts with Savepoints.
 * Debit account A, Credit account B, and attempt an optional processing fee.
 * If the fee violates a constraint, roll back only the fee using a Savepoint,
 * preserving the primary transfer.
 */
public class T2_Transactions {

    private static final String URL = "jdbc:h2:mem:s12_tyi2;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        setupDatabase();

        System.out.println("Try It Yourself 2 - Advanced JDBC: Transactions & Savepoints");
        System.out.println("Initial balances:");
        printBalances();

        // Transfer 200.0 from Alice (acc 1) to Bob (acc 2), with an invalid fee (-15.0)
        System.out.println();
        System.out.println("Executing transfer: $200.0 from Alice to Bob, optional fee -$15.0 (invalid):");
        boolean success = transferWithSavepoint(1, 2, 200.0, -15.0);

        System.out.println("Transfer completed: " + success);
        System.out.println();
        System.out.println("Final balances:");
        printBalances();
        System.out.println("  -> Primary transfer succeeded ($200 moved); invalid fee was safely rolled back.");
    }

    private static boolean transferWithSavepoint(int fromAcc, int toAcc, double amount, double fee) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // 1. Debit sender
                updateBalance(conn, fromAcc, -amount);

                // 2. Credit receiver
                updateBalance(conn, toAcc, amount);

                // 3. Optional processing fee with Savepoint
                Savepoint feeSavepoint = conn.setSavepoint("fee_checkpoint");
                try {
                    // This will throw if fee < 0 due to CHECK constraint
                    recordFee(conn, fromAcc, fee);
                } catch (SQLException e) {
                    System.out.println("  [Savepoint Rollback] Fee failed (" + e.getMessage().split(";")[0]
                            + ") -> rolling back fee only.");
                    conn.rollback(feeSavepoint);
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                System.out.println("  [Full Rollback] Primary transfer failed: " + e.getMessage());
                conn.rollback();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static void updateBalance(Connection conn, int accId, double delta) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setInt(2, accId);
            ps.executeUpdate();
        }
    }

    private static void recordFee(Connection conn, int accId, double fee) throws SQLException {
        String sql = "INSERT INTO fee_audit (acc_id, fee) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accId);
            ps.setDouble(2, fee);
            ps.executeUpdate();
        }
    }

    private static void printBalances() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, owner, balance FROM accounts ORDER BY id")) {
            while (rs.next()) {
                System.out.printf("  Account #%d (%-6s) : $%.2f%n",
                        rs.getInt("id"), rs.getString("owner"), rs.getDouble("balance"));
            }
        }
    }

    private static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE accounts (id INT PRIMARY KEY, owner VARCHAR(50), balance DECIMAL(10,2) CHECK (balance >= 0))");
            stmt.execute("CREATE TABLE fee_audit (acc_id INT, fee DECIMAL(10,2) CHECK (fee > 0))");
            stmt.execute("INSERT INTO accounts VALUES (1, 'Alice', 500.0), (2, 'Bob', 100.0)");
        }
    }
}
