package com.aptech.s08.practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import com.aptech.s08.Db;

/**
 * PRACTICE 3 (Challenge) — three faults around transactions, and the fix for each.
 *
 * <pre>
 *   FAULT 1  work committed statement-by-statement because auto-commit was left on
 *            symptom: a "failed" operation has already left rows behind
 *
 *   FAULT 2  the exception swallowed, so the caller is told it worked
 *            symptom: the rollback never runs and success is reported for a failure
 *
 *   FAULT 3  one bad item throwing away a whole batch
 *            symptom: four good records lost to discard one bad one - fixed with a savepoint
 * </pre>
 *
 * <p>Each fault runs broken, prints the damage, then runs fixed and prints the difference.
 *
 * <p><strong>Expected output:</strong> fault 1 leaks a row, fault 2 reports a false success,
 * fault 3 loses three good rows — and each fixed version avoids its damage.
 */
public class P3_TransactionFaults {

    public static void main(String[] args) throws SQLException {
        System.out.println("P3 - three transaction faults, and the fix for each");

        try (Connection c = Db.open()) {
            prepare(c);
            fault1(c);
            try {
                fault2(c);
            } catch (SQLException e) {
                System.out.println("  the caller received: " + Db.firstLine(e));
                System.out.println("  -> the failure reached the top. That IS the fix: the caller");
                System.out.println("     now knows the work was undone and can tell the user.");
            }
            fault3(c);
            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE p3_demo");
            }
        }

        System.out.println("\nEvery fault here still compiles and still runs. That is why they ship.");
    }

    // ========================================================================
    //  FAULT 1 — auto-commit left on
    // ========================================================================
    private static void fault1(Connection c) throws SQLException {
        System.out.println(Db.rule("FAULT 1 - auto-commit left on"));

        int before = count(c);
        c.setAutoCommit(true);          // BROKEN: the default, and the mistake
        try {
            insert(c, "leaked-1");
            insert(c, "leaked-2");
            throw new SQLException("pretend the third step failed");
        } catch (SQLException e) {
            c.rollback();               // does nothing: each INSERT was already committed
        }
        int after = count(c);
        System.out.println("  before: " + before + ", after: " + after
                + (after > before ? "   -> " + (after - before) + " row(s) leaked past a rollback" : ""));

        // FIXED
        c.setAutoCommit(false);
        int beforeFixed = count(c);
        try {
            insert(c, "temp-1");
            insert(c, "temp-2");
            throw new SQLException("third step fails again");
        } catch (SQLException e) {
            c.rollback();               // now this actually works
        } finally {
            c.setAutoCommit(true);
        }
        System.out.println("  fixed : before: " + beforeFixed + ", after: " + count(c)
                + "   -> nothing leaked");
    }

    // ========================================================================
    //  FAULT 2 — the exception is swallowed
    // ========================================================================
    private static void fault2(Connection c) throws SQLException {
        System.out.println(Db.rule("FAULT 2 - the exception swallowed, success reported"));

        // BROKEN
        c.setAutoCommit(false);
        try {
            insert(c, "will-not-be-rolled-back");
            throw new SQLException("a real failure");
        } catch (SQLException e) {
            // BROKEN: caught and ignored. No rollback, no report to the caller.
            System.out.println("  broken: caught the failure and said nothing");
        }
        c.commit();                     // commits the half-done work
        c.setAutoCommit(true);
        System.out.println("  broken: the caller thinks it succeeded; the row is in the table");

        // FIXED
        c.setAutoCommit(false);
        try {
            insert(c, "rolled-back");
            throw new SQLException("a real failure");
        } catch (SQLException e) {
            c.rollback();
            c.setAutoCommit(true);
            System.out.println("  fixed : caught it, rolled back, and will re-throw to the caller");
            throw new SQLException("propagated: " + firstLine(e.getMessage()));
        } finally {
            c.setAutoCommit(true);
        }
    }

    // ========================================================================
    //  FAULT 3 — one bad row loses the whole batch
    // ========================================================================
    private static void fault3(Connection c) throws SQLException {
        System.out.println(Db.rule("FAULT 3 - one bad item destroys the whole batch"));
        String[] labels = { "good-1", "good-2", "toolong-toolong-toolong-toolong-toolong-toolong", "good-3" };

        // BROKEN: one failure rolls everything back
        int before = count(c);
        c.setAutoCommit(false);
        try {
            for (String label : labels) insert(c, label);
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            System.out.println("  broken: " + firstLine(e.getMessage()));
        } finally {
            c.setAutoCommit(true);
        }
        System.out.println("  broken: " + (count(c) - before) + " of " + labels.length
                + " rows survived   -> the three good ones were lost");

        // FIXED: a savepoint per item, so only the bad one is undone
        int beforeFixed = count(c);
        c.setAutoCommit(false);
        int posted = 0;
        try {
            for (String label : labels) {
                Savepoint point = c.setSavepoint("item");
                try {
                    insert(c, label);
                    posted++;
                } catch (SQLException e) {
                    c.rollback(point);
                    System.out.println("  fixed : rejected one item -> " + firstLine(e.getMessage()));
                } finally {
                    c.releaseSavepoint(point);
                }
            }
            c.commit();
        } finally {
            c.setAutoCommit(true);
        }
        System.out.println("  fixed : " + posted + " of " + labels.length
                + " rows survived   -> only the bad one was discarded");
    }

    // ------------------------------------------------------------------------

    private static void prepare(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS p3_demo");
            st.execute("CREATE TABLE p3_demo (id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                     + " label VARCHAR(40))");
        }
    }

    private static void insert(Connection c, String label) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO p3_demo (label) VALUES (?)")) {
            ps.setString(1, label);
            ps.executeUpdate();       // a label longer than 40 chars throws here
        }
    }

    private static int count(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM p3_demo")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
