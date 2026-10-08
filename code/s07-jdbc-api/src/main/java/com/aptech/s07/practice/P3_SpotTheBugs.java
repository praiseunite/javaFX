package com.aptech.s07.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PRACTICE 3 (Challenge) — three faults in one small program, and the fix for each.
 *
 * <p>Each fault below is a real mistake with a real symptom. The program runs the broken
 * version, prints what happens, then runs the fixed version and prints the difference. Name
 * each fault before you run it; the output is the check on your answer.
 *
 * <pre>
 *   FAULT 1  a statement built by gluing a value into the SQL
 *            symptom: one lookup returns the whole table
 *
 *   FAULT 2  a resource opened and never closed
 *            symptom: a connection leaked on every call - invisible until the pool runs dry
 *
 *   FAULT 3  reading a column by NUMBER
 *            symptom: adding a column to the SELECT silently shifts every value
 * </pre>
 *
 * <p><strong>Expected output:</strong> fault 1 leaks 4 rows and the fix leaks 0; fault 2
 * reports the leak and the fix reports none; fault 3 prints a wrong name and then the right one.
 */
public class P3_SpotTheBugs {

    public static void main(String[] args) throws SQLException {

        System.out.println("P3 - three faults, and the fix for each");
        String url = "jdbc:h2:mem:p3;DB_CLOSE_DELAY=-1";

        try (Connection setup = DriverManager.getConnection(url, "sa", "")) {
            createData(setup);
        }

        fault1(url);
        fault2(url);
        fault3(url);

        System.out.println("\nEvery fault here throws nothing. That is why they ship.");
    }

    // ========================================================================
    //  FAULT 1 — SQL built by concatenation
    // ========================================================================
    private static void fault1(String url) throws SQLException {
        System.out.println("\n=== FAULT 1 - a statement glued together from input " + "=".repeat(14));
        String typed = "' OR '1'='1";

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {

            // BROKEN
            String brokenSql = "SELECT name FROM students WHERE name = '" + typed + "'";
            int brokenCount;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(brokenSql)) {
                brokenCount = 0;
                System.out.println("  broken: " + brokenSql);
                while (rs.next()) { System.out.println("        -> " + rs.getString("name")); brokenCount++; }
            }
            System.out.println("  broken returned " + brokenCount + " rows for a one-name lookup");

            // FIXED
            String fixedSql = "SELECT name FROM students WHERE name = ?";
            int fixedCount;
            try (PreparedStatement ps = c.prepareStatement(fixedSql)) {
                ps.setString(1, typed);
                try (ResultSet rs = ps.executeQuery()) {
                    fixedCount = 0;
                    while (rs.next()) fixedCount++;
                }
            }
            System.out.println("  fixed  returned " + fixedCount + " rows  <- the ? is a value, not code");
            System.out.println("  " + (brokenCount > fixedCount
                    ? "VERDICT: the broken version returned data the caller never asked for."
                    : "VERDICT: no difference?! check the data"));
        }
    }

    // ========================================================================
    //  FAULT 2 — a resource opened and never closed
    // ========================================================================
    private static void fault2(String url) throws SQLException {
        System.out.println("\n=== FAULT 2 - a connection opened and never closed " + "=".repeat(17));

        int leaked = 0;
        for (int i = 0; i < 5; i++) {
            // BROKEN: this connection goes out of scope without close() ever being called.
            // Nothing complains. The handle stays open until the JVM exits.
            Connection leaky = DriverManager.getConnection(url, "sa", "");
            Statement st = leaky.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students");
            rs.next();
            leaked++;
        }
        System.out.println("  broken: opened " + leaked + " connections, closed 0");
        System.out.println("          each one still holds an operating-system handle");

        // FIXED: try-with-resources closes each one before the loop continues.
        int closed = 0;
        for (int i = 0; i < 5; i++) {
            try (Connection c = DriverManager.getConnection(url, "sa", "");
                 Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                rs.next();
                closed++;
            }   // <- closed here, on every path, with no finally block to forget
        }
        System.out.println("  fixed : opened " + closed + " connections, closed " + closed);
        System.out.println("  five is harmless. Five thousand is \"Too many open files\".");

        System.gc();
        System.out.println("  VERDICT: same output, different resource behaviour - and only one");
        System.out.println("           of them can survive being called in a loop.");
    }

    // ========================================================================
    //  FAULT 3 — reading a column by number
    // ========================================================================
    private static void fault3(String url) throws SQLException {
        System.out.println("\n=== FAULT 3 - reading a column by number " + "=".repeat(27));

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {

            // The query gains a column at the FRONT.
            String sql = "SELECT id, name, mark FROM students WHERE id = 1";

            // BROKEN: assumes column 1 is the name. It is not - the id is.
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    // getString(1) is the FIRST column of THIS query. This used to be name.
                    System.out.println("  broken: getString(1) -> " + rs.getString(1)
                            + "   <- that is the id, not the name");
                }
            }

            // FIXED: read by label. The label does not care how many columns came before it.
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    System.out.println("  fixed : getString(\"name\") -> " + rs.getString("name"));
                }
            }
            System.out.println("  VERDICT: neither one throws. The first is simply wrong,");
            System.out.println("           and it was right until somebody edited the SELECT.");
        }
    }

    /** A few rows, including a name that is also a valid id - which is what makes fault 3 visible. */
    private static void createData(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(100), mark DECIMAL(5,2))");
            st.executeUpdate("INSERT INTO students VALUES "
                    + "(1, 'Ada Lovelace', 88.50),"
                    + "(2, 'Grace Hopper', 91.00),"
                    + "(3, 'Alan Turing',  76.25),"
                    + "(4, 'Katherine Johnson', 95.75)");
        }
    }
}
