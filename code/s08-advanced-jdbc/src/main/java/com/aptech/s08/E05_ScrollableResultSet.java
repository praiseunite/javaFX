package com.aptech.s08;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E05 — scrollable result sets, and moving the cursor by hand.
 *
 * <p>So far every <code>ResultSet</code> has been read once, forwards, with
 * <code>while (rs.next())</code>. That is the default: <code>TYPE_FORWARD_ONLY</code>. A
 * <strong>scrollable</strong> result set has a cursor you can move in any direction —
 * <code>first()</code>, <code>last()</code>, <code>absolute()</code>, <code>relative()</code>,
 * <code>previous()</code> — which is what a data grid on screen needs when the user clicks
 * "last page" or jumps to row 500.
 *
 * <p>The three result-set types:
 * <pre>
 *   TYPE_FORWARD_ONLY       the default. next() only. Cheapest.
 *   TYPE_SCROLL_INSENSITIVE can move anywhere; does NOT see later changes to the data.
 *   TYPE_SCROLL_SENSITIVE   can move anywhere; MAY see later changes (rarely supported).
 * </pre>
 *
 * <p>Scrollability is not free: the driver may have to buffer the rows. Ask for it only when
 * something genuinely needs to move backwards.
 */
public class E05_ScrollableResultSet {

    public static void main(String[] args) throws SQLException {
        System.out.println("E05 - scrollable result sets and row positioning");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            // ---- what this driver supports ----
            System.out.println(Db.rule("what this database supports"));
            var md = c.getMetaData();
            System.out.println("  TYPE_FORWARD_ONLY      : " + md.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
            System.out.println("  TYPE_SCROLL_INSENSITIVE: " + md.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
            System.out.println("  TYPE_SCROLL_SENSITIVE  : " + md.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));

            // ---- the default: forwards only ----
            System.out.println(Db.rule("the default - forwards only"));
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, name, mark FROM students ORDER BY id")) {
                System.out.println("  type: " + typeName(rs.getType())
                        + "   (remember this - it is what you have been using)");
                while (rs.next()) {
                    System.out.printf("    %d %-20s %.2f%n", rs.getInt("id"), rs.getString("name"), rs.getDouble("mark"));
                    if (rs.getRow() >= 3) { System.out.println("    ... (stopping early for the example)"); break; }
                }
            }

            // ---- a scrollable result set ----
            System.out.println(Db.rule("scrollable - move the cursor by hand"));

            // The two arguments: the TYPE and the CONCURRENCY (read-only here; E06 upgrades it).
            try (Statement st = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = st.executeQuery("SELECT id, name, mark FROM students ORDER BY id")) {

                System.out.println("  type: " + typeName(rs.getType()));

                if (rs.last()) {
                    System.out.println("  last()      -> id " + rs.getInt("id") + ", " + rs.getString("name")
                            + "   (there are " + rs.getRow() + " rows)");
                }
                if (rs.first()) {
                    System.out.println("  first()     -> id " + rs.getInt("id") + ", " + rs.getString("name"));
                }
                if (rs.absolute(3)) {
                    System.out.println("  absolute(3) -> id " + rs.getInt("id") + ", " + rs.getString("name")
                            + "   (jump straight to row 3)");
                }
                if (rs.relative(2)) {
                    System.out.println("  relative(2) -> id " + rs.getInt("id") + ", " + rs.getString("name")
                            + "   (two rows on from row 3)");
                }
                if (rs.previous()) {
                    System.out.println("  previous()  -> id " + rs.getInt("id") + ", " + rs.getString("name")
                            + "   (back one)");
                }

                System.out.println("  isAfterLast()  : " + rs.isAfterLast());
                System.out.println("  isBeforeFirst(): " + rs.isBeforeFirst());

                // ---- paging, which is what this is really for ----
                System.out.println(Db.rule("the real use: paging through a result"));

                int pageSize = 3;
                rs.last();
                int total = rs.getRow();
                int pages = (total + pageSize - 1) / pageSize;
                System.out.println("  " + total + " students, " + pageSize + " per page -> " + pages + " pages");

                for (int page = 1; page <= pages; page++) {
                    int firstRowOnPage = (page - 1) * pageSize + 1;
                    System.out.println("  page " + page + ":");
                    if (rs.absolute(firstRowOnPage)) {
                        for (int i = 0; i < pageSize; i++) {
                            System.out.printf("    %d %-20s %.2f%n",
                                    rs.getInt("id"), rs.getString("name"), rs.getDouble("mark"));
                            if (!rs.next()) break;   // last page is short
                        }
                    }
                }

                // NB: the more common way to page today is "LIMIT n OFFSET m" in the SQL,
                // which asks the database for one page and never moves the cursor at all.
                System.out.println("\n  note: these days most paging uses LIMIT/OFFSET in the SQL");
                System.out.println("  itself, and never needs a scrollable cursor. Both work; know both.");
            }

            System.out.println(Db.rule("the cost"));
            System.out.println("  A scrollable cursor may make the driver buffer rows in memory so it can");
            System.out.println("  go backwards. For a ten-million-row report that is the difference");
            System.out.println("  between a query and an outage. Ask for it when you need it.");
        }

        System.out.println("\nScrollable = a cursor you can move. Forwards-only is cheaper and is the default.");
    }

    private static String typeName(int type) {
        return switch (type) {
            case ResultSet.TYPE_FORWARD_ONLY       -> "TYPE_FORWARD_ONLY";
            case ResultSet.TYPE_SCROLL_INSENSITIVE -> "TYPE_SCROLL_INSENSITIVE";
            case ResultSet.TYPE_SCROLL_SENSITIVE   -> "TYPE_SCROLL_SENSITIVE";
            default -> "UNKNOWN(" + type + ")";
        };
    }
}
