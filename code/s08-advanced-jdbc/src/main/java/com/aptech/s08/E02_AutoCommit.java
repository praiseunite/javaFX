package com.aptech.s08;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E02 — auto-commit and isolation levels: the two settings that decide what a
 * transaction even means.
 *
 * <p><strong>Auto-commit</strong> is the first thing to understand, because it explains the
 * behaviour of every JDBC program you have written so far without thinking about it. When it
 * is ON (the default), each statement is a complete transaction on its own and is committed
 * the moment it finishes. That is why E01's first version could not roll back: there was
 * nothing left to roll back — the write was already saved.
 *
 * <p><strong>Isolation level</strong> is the second. When two transactions run at once, the
 * level decides how much of each other's uncommitted work they can see. The four standard
 * levels trade correctness against concurrency, and this program reads the default, sets
 * each level in turn, and shows what each one permits.
 */
public class E02_AutoCommit {

    public static void main(String[] args) throws SQLException {
        System.out.println("E02 - auto-commit and isolation levels");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            // ---- auto-commit ----
            System.out.println(Db.rule("auto-commit: the default, and what it means"));
            System.out.println("  getAutoCommit() -> " + c.getAutoCommit());
            System.out.println("  ON  : every statement is its own transaction, saved immediately.");
            System.out.println("  OFF : statements group up until commit() or rollback().");

            // Prove it: write a row with auto-commit ON, then change our mind.
            c.setAutoCommit(true);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM courses WHERE id = 900");
                st.executeUpdate("INSERT INTO courses (id, title, credits, fee) VALUES (900, 'Temp', 1, 1.00)");
            }
            System.out.println("\n  inserted course 900 with auto-commit ON, then tried to roll back...");
            try {
                c.rollback();
                System.out.println("  rollback() returned without error (some drivers allow this)");
            } catch (SQLException e) {
                System.out.println("  rollback() threw: " + firstLine(e.getMessage()));
            }
            System.out.println("  row still there? " + courseExists(c, 900)
                    + "   <- nothing was undone; there was no transaction to undo");

            // Now with auto-commit OFF.
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM courses WHERE id = 901");
                st.executeUpdate("INSERT INTO courses (id, title, credits, fee) VALUES (901, 'Temp', 1, 1.00)");
            }
            System.out.println("  inserted course 901 with auto-commit OFF, then rolled back...");
            c.rollback();
            System.out.println("  row still there? " + courseExists(c, 901)
                    + "   <- rollback undid it");
            c.setAutoCommit(true);

            // ---- isolation levels ----
            System.out.println(Db.rule("isolation levels"));

            DatabaseMetaData md = c.getMetaData();
            System.out.println("  this database's DEFAULT is: " + nameOf(c.getTransactionIsolation()));
            System.out.println();

            int[] levels = {
                    Connection.TRANSACTION_READ_UNCOMMITTED,
                    Connection.TRANSACTION_READ_COMMITTED,
                    Connection.TRANSACTION_REPEATABLE_READ,
                    Connection.TRANSACTION_SERIALIZABLE,
            };
            for (int level : levels) {
                int previous = c.getTransactionIsolation();
                try {
                    c.setTransactionIsolation(level);
                    System.out.printf("  %-22s supported (set ok)%n", nameOf(level));
                } catch (SQLException e) {
                    System.out.printf("  %-22s NOT supported: %s%n", nameOf(level), firstLine(e.getMessage()));
                } finally {
                    c.setTransactionIsolation(previous);
                }
            }

            System.out.println(Db.rule("what each level permits"));
            System.out.println("  READ_UNCOMMITTED  sees other transactions' uncommitted rows");
            System.out.println("                    (dirty reads allowed - fastest, least safe)");
            System.out.println("  READ_COMMITTED    sees only committed rows  <- most databases' default");
            System.out.println("  REPEATABLE_READ   a row read twice gives the same answer both times");
            System.out.println("  SERIALIZABLE      as if the transactions ran one after another");
            System.out.println("                    (safest, and the least concurrent)");

            System.out.println(Db.rule("the three phenomena the levels are named after"));
            System.out.println("  dirty read      : reading a row another transaction has changed but not committed");
            System.out.println("  non-repeatable  : reading the same row twice and getting two different answers");
            System.out.println("  phantom read    : running the same query twice and getting different ROWS");

            System.out.println(Db.rule("in practice"));
            System.out.println("  Do not change the isolation level unless you can name the problem you");
            System.out.println("  are solving. The default is chosen by people who know the database,");
            System.out.println("  and lowering it to fix a performance problem trades correctness for speed.");
        }

        System.out.println("\nAuto-commit decides WHEN work is saved. Isolation decides WHAT others see.");
    }

    private static boolean courseExists(Connection c, int id) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM courses WHERE id = " + id)) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    private static String nameOf(int level) {
        return switch (level) {
            case Connection.TRANSACTION_NONE             -> "TRANSACTION_NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED   -> "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ  -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE     -> "SERIALIZABLE";
            default -> "UNKNOWN(" + level + ")";
        };
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
