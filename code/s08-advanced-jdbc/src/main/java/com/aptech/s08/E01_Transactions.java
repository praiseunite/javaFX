package com.aptech.s08;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E01 — transactions: two writes that must succeed together, or not at all.
 *
 * <p>The scenario is the one every database course uses because it is the one that matters:
 * <strong>enrolling a student and taking their fee</strong>. Two rows go in — an
 * {@code enrolments} row and a {@code fee_payments} row. If the second write fails, the
 * first must not survive, because an enrolment with no payment is a student who is on the
 * course but has not paid, and nobody decided that.
 *
 * <p>This program does the same operation twice. The first time <em>without</em> a
 * transaction, the second time <em>with</em> one, and in both cases on purpose the second
 * write fails. The difference in what is left in the database is the whole argument.
 */
public class E01_Transactions {

    public static void main(String[] args) throws SQLException {
        System.out.println("E01 - transactions: enrol a student and take the fee, together or not at all");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            // ---- the WRONG way: two independent writes ----
            System.out.println(Db.rule("WRONG - two separate writes, auto-commit on"));
            int before = count(c, "enrolments");
            try {
                enrolWithoutTransaction(c);
            } catch (SQLException e) {
                System.out.println("  the second write failed: " + firstLine(e.getMessage()));
            }
            int after = count(c, "enrolments");
            System.out.println("  enrolments before: " + before + ", after: " + after
                    + (after > before ? "   <-- the enrolment SURVIVED an operation that failed" : ""));

            // clean up that orphan so the next part starts level
            deleteOrphan(c);

            // ---- the RIGHT way: one transaction ----
            System.out.println(Db.rule("RIGHT - one transaction, commit at the end"));
            int beforeTx = count(c, "enrolments");
            int beforePay = count(c, "fee_payments");
            try {
                enrolInTransaction();
            } catch (SQLException e) {
                System.out.println("  the second write failed: " + firstLine(e.getMessage()));
            }
            int afterTx = count(c, "enrolments");
            int afterPay = count(c, "fee_payments");
            System.out.println("  enrolments  : " + beforeTx + " -> " + afterTx
                    + (afterTx == beforeTx ? "   (unchanged)" : "   <-- leaked!"));
            System.out.println("  fee_payments: " + beforePay + " -> " + afterPay
                    + (afterPay == beforePay ? "   (unchanged)" : "   <-- leaked!"));

            System.out.println(Db.rule("what the difference is"));
            System.out.println("  With auto-commit ON (the default), every statement is its own");
            System.out.println("  transaction and is saved the moment it runs. The first write was");
            System.out.println("  already permanent when the second one failed.");
            System.out.println();
            System.out.println("  With auto-commit OFF, nothing is saved until commit(). The rollback");
            System.out.println("  in the catch undoes the first write, so the pair stays atomic:");
            System.out.println("  both rows or neither.");
        }

        System.out.println(Db.rule("the pattern, in four lines"));
        System.out.println("  c.setAutoCommit(false);       // stop saving statement by statement");
        System.out.println("  ... do every write ...");
        System.out.println("  c.commit();                   // all of it becomes permanent here");
        System.out.println("  c.rollback();                 // in a catch - all of it is undone");
    }

    // ------------------------------------------------------------------------

    /** Enrols student 1 on course 2 and takes the fee — but with TWO independent commits. */
    private static void enrolWithoutTransaction(Connection c) throws SQLException {
        int enrolmentId;

        // write 1: the enrolment. Auto-commit saves this immediately.
        String enrol = "INSERT INTO enrolments (student_id, course_id, paid) VALUES (?, ?, FALSE)";
        try (PreparedStatement ps = c.prepareStatement(enrol, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, 1);
            ps.setInt(2, 2);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                enrolmentId = keys.getInt(1);
            }
        }
        System.out.println("  write 1: enrolled student 1 on course 2 (enrolment " + enrolmentId + ") - saved");

        // write 2: the payment. We pass a null amount on purpose to make this fail.
        String pay = "INSERT INTO fee_payments (enrolment_id, amount) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(pay)) {
            ps.setInt(1, enrolmentId);
            ps.setNull(2, java.sql.Types.DECIMAL);   // NOT NULL column -> this throws
            ps.executeUpdate();
        }
        System.out.println("  write 2: payment - this line is never reached");
    }

    /** The same two writes, inside one transaction. The failure rolls both back. */
    private static void enrolInTransaction() throws SQLException {
        try (Connection c = Db.open()) {
            c.setAutoCommit(false);
            try {
                int enrolmentId;
                String enrol = "INSERT INTO enrolments (student_id, course_id, paid) VALUES (?, ?, FALSE)";
                try (PreparedStatement ps = c.prepareStatement(enrol, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, 1);
                    ps.setInt(2, 2);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        enrolmentId = keys.getInt(1);
                    }
                }
                System.out.println("  write 1: enrolment " + enrolmentId + " written (not yet saved)");

                String pay = "INSERT INTO fee_payments (enrolment_id, amount) VALUES (?, ?)";
                try (PreparedStatement ps = c.prepareStatement(pay)) {
                    ps.setInt(1, enrolmentId);
                    ps.setNull(2, java.sql.Types.DECIMAL);   // fails, same as before
                    ps.executeUpdate();
                }

                c.commit();
            } catch (SQLException e) {
                System.out.println("  write 2 failed -> rollback()");
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    /** Removes any enrolment for student 1 / course 2 left behind by the broken version. */
    private static void deleteOrphan(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            int removed = st.executeUpdate("DELETE FROM enrolments WHERE student_id = 1 AND course_id = 2");
            if (removed > 0) System.out.println("  (cleared " + removed + " orphaned row before the next part)");
        }
    }

    private static int count(Connection c, String table) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** The driver's message, trimmed to one readable line — see {@link Db#firstLine(String)}. */
    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
