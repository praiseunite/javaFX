package com.aptech.s08.practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.aptech.s08.Db;

/**
 * PRACTICE 2 (Medium) — a transfer: two updates that must both happen, or neither.
 *
 * <p>Move a balance from one account to another inside one transaction, and prove the invariant
 * that matters: <strong>the total across both accounts never changes.</strong> Then deliberately
 * make the second update fail and show the total is still intact — because the first one was
 * rolled back.
 *
 * <p>This is the textbook transaction example, and it is worth doing once by hand so that the
 * lab's fee posting is not the first time you have seen it.
 *
 * <p><strong>Expected output:</strong> the transfer succeeds and the total is unchanged; the
 * failed transfer leaves both balances exactly where they were.
 */
public class P2_AtomicTransfer {

    public static void main(String[] args) throws SQLException {
        System.out.println("P2 - a transfer that must not half-happen");

        try (Connection c = Db.open()) {

            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE IF EXISTS accounts");
                st.execute("CREATE TABLE accounts (id INT PRIMARY KEY, name VARCHAR(40), balance DECIMAL(9,2))");
                st.executeUpdate("INSERT INTO accounts VALUES "
                        + "(1, 'Ada',   500.00),"
                        + "(2, 'Grace', 300.00)");
            }

            double totalBefore = total(c);
            System.out.println(Db.rule("before"));
            print(c, "  ");
            System.out.printf("  total: %.2f%n", totalBefore);

            // ---- a transfer that works ----
            System.out.println(Db.rule("transfer 120.00 from Ada to Grace - one transaction"));
            transfer(c, 1, 2, 120.00);
            print(c, "  ");
            System.out.printf("  total: %.2f   (unchanged: %.2f)%n", total(c), totalBefore);

            // ---- a transfer that fails halfway ----
            System.out.println(Db.rule("transfer 9999.00 - more than Ada has, so it must fail"));
            double before = total(c);
            try {
                transferStrict(c, 1, 2, 9999.00);   // refuses to go negative, mid-transaction
            } catch (SQLException e) {
                System.out.println("  transfer refused: " + firstLine(e.getMessage()));
            }
            print(c, "  ");
            System.out.printf("  total: %.2f   (still %.2f - the debit was rolled back)%n", total(c), before);

            System.out.println(Db.rule("why this is the whole point"));
            System.out.println("  The debit and the credit are two statements. Without a transaction,");
            System.out.println("  a failure between them destroys money - the debit stands and the");
            System.out.println("  credit never arrives. The invariant 'the total is constant' is what a");
            System.out.println("  transaction is protecting, and it is what you assert in a test.");

            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE accounts");
            }
        }

        System.out.println("\nTwo writes, one unit. The total across the pair never changes by accident.");
    }

    /** Moves money, refusing to overdraw. The refusal happens inside the transaction, so it rolls back. */
    static void transfer(Connection c, int from, int to, double amount) throws SQLException {
        transferStrict(c, from, to, amount);   // allow the same code to throw for the failing case
    }

    static void transferStrict(Connection c, int from, int to, double amount) throws SQLException {
        c.setAutoCommit(false);
        try {
            double fromBalance = balance(c, from);
            if (amount > fromBalance) {
                throw new SQLException("insufficient funds: " + Db.money(fromBalance)
                        + " available, " + Db.money(amount) + " requested");
            }

            credit(c, from, -amount);   // debit
            credit(c, to,   +amount);   // credit
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    private static void credit(Connection c, int id, double delta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
            ps.setDouble(1, delta);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private static double balance(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT balance FROM accounts WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private static double total(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(balance),0) FROM accounts")) {
            rs.next();
            return rs.getDouble(1);
        }
    }

    private static void print(Connection c, String indent) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, balance FROM accounts ORDER BY id")) {
            while (rs.next()) {
                System.out.printf("%s%-8s %9.2f%n", indent, rs.getString("name"), rs.getDouble("balance"));
            }
        }
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
