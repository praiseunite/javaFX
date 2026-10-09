package com.aptech.s08;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

/**
 * E03 — savepoints: rolling back part of a transaction without losing the rest.
 *
 * <p>A savepoint is a bookmark inside a transaction. {@code rollback(savepoint)} undoes
 * everything since that bookmark and keeps everything before it. It is the answer to
 * "most of this batch is good, but one item is not, and I do not want to throw away the
 * good items to discard the bad one".
 *
 * <p>The scenario: posting a term's fees, one payment at a time. Most payments are fine; one
 * is invalid (a negative amount). Without a savepoint the whole term's posting is lost. With
 * one, the bad payment is undone and the good ones survive — and at the end the program
 * reports exactly which.
 */
public class E03_Savepoints {

    public static void main(String[] args) throws SQLException {
        System.out.println("E03 - savepoints: undo part of a transaction, keep the rest");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            System.out.println(Db.rule("posting five payments, one of them invalid"));

            // Start clean: drop any payments for enrolment 3 (Alan, unpaid) from a previous run.
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM fee_payments WHERE enrolment_id = 3");
            }

            c.setAutoCommit(false);
            int posted = 0;
            try {
                // Five payments for enrolment 3. The third is invalid; we will undo just it.
                double[] amounts = { 100.00, 100.00, -50.00, 100.00, 180.00 };

                for (int i = 0; i < amounts.length; i++) {
                    double amount = amounts[i];

                    // The bookmark: everything before this stays if this item fails.
                    Savepoint point = c.setSavepoint("payment-" + (i + 1));
                    System.out.printf("  payment %d (%.2f) ... ", i + 1, amount);

                    try {
                        insertPayment(c, 3, amount);
                        posted++;
                        System.out.println("posted");
                    } catch (SQLException e) {
                        System.out.println("REJECTED -> rolling back to the savepoint");
                        c.rollback(point);
                        System.out.println("        reason: " + firstLine(e.getMessage()));
                    } finally {
                        c.releaseSavepoint(point);
                    }
                }

                c.commit();
                System.out.println("\n  committed. " + posted + " of " + amounts.length + " payments survived.");
            } catch (SQLException e) {
                c.rollback();
                System.out.println("  the whole posting rolled back: " + firstLine(e.getMessage()));
                throw e;
            } finally {
                c.setAutoCommit(true);
            }

            System.out.println(Db.rule("what is actually in the table now"));
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id, amount FROM fee_payments WHERE enrolment_id = 3 ORDER BY id")) {
                try (ResultSet rs = ps.executeQuery()) {
                    double total = 0;
                    while (rs.next()) {
                        System.out.printf("  payment %d : %10.2f%n", rs.getInt("id"), rs.getDouble("amount"));
                        total += rs.getDouble("amount");
                    }
                    System.out.printf("  %-9s : %10.2f%n", "total", total);
                }
            }

            System.out.println(Db.rule("why not just roll back the lot?"));
            System.out.println("  Because the four good payments are real work. Undoing them too");
            System.out.println("  would mean redoing the term's posting by hand. A savepoint lets the");
            System.out.println("  transaction say: 'this one item did not happen, the rest did.'");
            System.out.println();
            System.out.println("  Note the shape: setSavepoint() before the risky step, rollback(point)");
            System.out.println("  in the catch, releaseSavepoint(point) in the finally. The outer");
            System.out.println("  commit still happens only if the loop itself survives.");
        }

        System.out.println("\nA savepoint is a bookmark. rollback(point) rewinds to it, not to the start.");
    }

    /**
     * Inserts one payment, rejecting a non-positive amount the way a real system would —
     * a CHECK constraint, or a business rule, would do the same thing at the database end.
     */
    private static void insertPayment(Connection c, int enrolmentId, double amount) throws SQLException {
        if (amount <= 0) {
            throw new SQLException("a fee payment must be positive (was " + amount + ")");
        }
        String sql = "INSERT INTO fee_payments (enrolment_id, amount) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrolmentId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
