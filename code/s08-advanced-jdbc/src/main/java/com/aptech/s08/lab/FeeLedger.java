package com.aptech.s08.lab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import com.aptech.s08.Db;

/**
 * GUIDED LAB — The Fee Ledger.
 *
 * The registry from Session 7 has money in it now, and money is where transactions stop being
 * theory. This lab posts a term's enrolments and fees as real units of work, and every step uses
 * one of the tools from E01-E03:
 *
 *   STEP 1  enrol a student AND take their fee as ONE transaction
 *   STEP 2  make the second write fail, and watch both roll back
 *   STEP 3  post a batch of payments with a SAVEPOINT, so one bad payment does not lose the rest
 *   STEP 4  reconcile: total paid vs the course fee, per enrolment
 *
 * This is Assignment A4 extended, and it is what "A4 = JDBC with transactions" means.
 */
public class FeeLedger {

    public static void main(String[] args) throws SQLException {
        System.out.println("GUIDED LAB - the Fee Ledger");

        Db.resetRegistry();   // start from the seed, so this lab prints the same thing every run

        try (Connection c = Db.open()) {
            Ledger ledger = new Ledger(c);

            // ---- STEP 1: enrol + pay, as one transaction ----
            System.out.println(Db.rule("STEP 1 - enrol a student and take the fee, atomically"));
            int enrolmentId = ledger.enrolAndPay(3, 2, 380.00);   // Alan Turing onto Databases
            System.out.println("  enrolment " + enrolmentId + " created and paid, in one transaction");
            System.out.println("  " + ledger.describe(enrolmentId));

            // ---- STEP 2: the same, but make the payment fail ----
            System.out.println(Db.rule("STEP 2 - the payment fails, so nothing survives"));
            int before = ledger.countEnrolments();
            try {
                ledger.enrolAndPayExpectingFailure(6, 3, 320.00);   // Barbara onto Web Development
            } catch (SQLException e) {
                System.out.println("  the payment failed: " + firstLine(e.getMessage()));
            }
            int after = ledger.countEnrolments();
            System.out.println("  enrolments before: " + before + ", after: " + after
                    + (after == before ? "   -> the failed pair left NOTHING behind"
                                       : "   -> a half-enrolment leaked!"));

            // ---- STEP 3: a batch of payments with a savepoint ----
            System.out.println(Db.rule("STEP 3 - post four payments, one bad, keep the good three"));
            int enrolment = ledger.firstUnpaidEnrolment();     // Margaret (enrolment 5), unpaid
            System.out.println("  posting against enrolment " + enrolment);
            double[] amounts = { 100.00, 100.00, -20.00, 180.00 };
            int posted = ledger.postPaymentsWithSavepoint(enrolment, amounts);
            System.out.println("  " + posted + " of " + amounts.length + " payments posted");
            System.out.println("  " + ledger.describe(enrolment));

            // ---- STEP 4: reconcile ----
            System.out.println(Db.rule("STEP 4 - reconcile every enrolment against its course fee"));
            ledger.reconcile();
        }

        System.out.println("\nEvery write in this lab was either all-or-nothing, or undoable to a savepoint.");
        System.out.println("That is what a transaction buys: the ledger always adds up.");
    }

    // ------------------------------------------------------------------------

    /** Every SQL statement in this lab lives here, and nowhere else. */
    static class Ledger {
        private final Connection c;

        Ledger(Connection c) { this.c = c; }

        /** STEP 1 — enrol and pay as one unit. Either both rows go in, or neither does. */
        int enrolAndPay(int studentId, int courseId, double fee) throws SQLException {
            c.setAutoCommit(false);
            try {
                int enrolmentId = insertEnrolment(c, studentId, courseId);
                insertPayment(c, enrolmentId, fee);
                markPaid(c, enrolmentId);
                c.commit();
                return enrolmentId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }

        /** STEP 2 — identical, but the payment is deliberately invalid so the rollback is visible. */
        int enrolAndPayExpectingFailure(int studentId, int courseId, double fee) throws SQLException {
            c.setAutoCommit(false);
            try {
                int enrolmentId = insertEnrolment(c, studentId, courseId);
                System.out.println("  enrolment " + enrolmentId + " written (not yet committed)");
                // A negative fee violates the business rule the same way a bad card would.
                insertPayment(c, enrolmentId, -Math.abs(fee));
                markPaid(c, enrolmentId);
                c.commit();
                return enrolmentId;
            } catch (SQLException e) {
                System.out.println("  rolling back the whole unit");
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }

        /** STEP 3 — post several payments; a bad one is undone to its savepoint, the rest stay. */
        int postPaymentsWithSavepoint(int enrolmentId, double[] amounts) throws SQLException {
            c.setAutoCommit(false);
            int posted = 0;
            try {
                for (double amount : amounts) {
                    Savepoint point = c.setSavepoint("payment");
                    try {
                        insertPayment(c, enrolmentId, amount);
                        posted++;
                    } catch (SQLException e) {
                        System.out.println("    rejected " + Db.money(amount) + ": " + firstLine(e.getMessage()));
                        c.rollback(point);
                    } finally {
                        c.releaseSavepoint(point);
                    }
                }
                // If everything posted, the enrolment is now settled.
                markPaidIfSettled(c, enrolmentId);
                c.commit();
                return posted;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }

        /** STEP 4 — total paid against the fee, for every enrolment. */
        void reconcile() throws SQLException {
            String sql = "SELECT e.id AS enrolment, s.name AS student, c.title AS course, c.fee AS fee,"
                       + "       COALESCE(SUM(p.amount), 0) AS paid, e.paid AS flagged"
                       + "  FROM enrolments e"
                       + "  JOIN students s ON s.id = e.student_id"
                       + "  JOIN courses  c ON c.id = e.course_id"
                       + "  LEFT JOIN fee_payments p ON p.enrolment_id = e.id"
                       + " GROUP BY e.id, s.name, c.title, c.fee, e.paid"
                       + " ORDER BY e.id";

            System.out.printf("  %-4s %-20s %-22s %9s %9s  %s%n",
                    "enr", "student", "course", "fee", "paid", "state");
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double fee = rs.getDouble("fee");
                    double paid = rs.getDouble("paid");
                    boolean flagged = rs.getBoolean("flagged");
                    String state = paid >= fee ? "settled"
                                 : paid > 0     ? "part-paid"
                                 :                "unpaid";
                    // The flag and the arithmetic must agree - that is what the transaction protected.
                    if (flagged != (paid >= fee)) state += " (flag disagrees!)";
                    System.out.printf("  %-4d %-20s %-22s %9.2f %9.2f  %s%n",
                            rs.getInt("enrolment"), rs.getString("student"),
                            rs.getString("course"), fee, paid, state);
                }
            }
            System.out.println("  'settled' means the payments cover the fee; the paid flag agrees.");
        }

        String describe(int enrolmentId) throws SQLException {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT s.name, c.title, c.fee,"
                  + " (SELECT COALESCE(SUM(amount),0) FROM fee_payments WHERE enrolment_id = e.id) AS paid"
                  + " FROM enrolments e JOIN students s ON s.id=e.student_id JOIN courses c ON c.id=e.course_id"
                  + " WHERE e.id = ?")) {
                ps.setInt(1, enrolmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "enrolment " + enrolmentId + " does not exist";
                    return String.format("%s on %s owes %.2f, paid %.2f",
                            rs.getString("name"), rs.getString("title"),
                            rs.getDouble("fee"), rs.getDouble("paid"));
                }
            }
        }

        int countEnrolments() throws SQLException {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM enrolments")) {
                rs.next();
                return rs.getInt(1);
            }
        }

        int firstUnpaidEnrolment() throws SQLException {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(
                        "SELECT id FROM enrolments WHERE paid = FALSE ORDER BY id LIMIT 1")) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }

        // ---- the small write helpers ----

        private int insertEnrolment(Connection c, int studentId, int courseId) throws SQLException {
            String sql = "INSERT INTO enrolments (student_id, course_id, paid) VALUES (?, ?, FALSE)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return keys.getInt(1);
                }
            }
        }

        private void insertPayment(Connection c, int enrolmentId, double amount) throws SQLException {
            if (amount <= 0) throw new SQLException("a fee payment must be positive (was " + amount + ")");
            String sql = "INSERT INTO fee_payments (enrolment_id, amount) VALUES (?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, enrolmentId);
                ps.setDouble(2, amount);
                ps.executeUpdate();
            }
        }

        private void markPaid(Connection c, int enrolmentId) throws SQLException {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE enrolments SET paid = TRUE WHERE id = ?")) {
                ps.setInt(1, enrolmentId);
                ps.executeUpdate();
            }
        }

        /** Sets the flag only when the payments really do cover the fee. */
        private void markPaidIfSettled(Connection c, int enrolmentId) throws SQLException {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE enrolments SET paid = ("
                  + "  (SELECT COALESCE(SUM(p.amount),0) FROM fee_payments p WHERE p.enrolment_id = ?)"
                  + "   >= (SELECT c.fee FROM enrolments e JOIN courses c ON c.id = e.course_id WHERE e.id = ?))"
                  + " WHERE id = ?")) {
                ps.setInt(1, enrolmentId);
                ps.setInt(2, enrolmentId);
                ps.setInt(3, enrolmentId);
                ps.executeUpdate();
            }
        }
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
