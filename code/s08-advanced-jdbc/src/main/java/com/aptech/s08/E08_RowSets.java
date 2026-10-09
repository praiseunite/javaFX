package com.aptech.s08;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.WebRowSet;

/**
 * E08 — RowSets: a ResultSet you can pass around, disconnect and even serialise.
 *
 * <p>A <code>ResultSet</code> is chained to its open <code>Connection</code> — close the
 * connection and the result is gone, and you cannot hand a live one to another layer without
 * also handing over the connection. A <strong>RowSet</strong> fixes that. It is a
 * <code>ResultSet</code> that is also a JavaBean: it can be created without a connection,
 * configured with setters, filled on demand, and read after the connection is closed.
 *
 * <p>The family:
 * <pre>
 *   JdbcRowSet     still connected. A thin wrapper — a ResultSet with nicer methods.
 *   CachedRowSet   DISCONNECTED. Reads all rows into memory; works after the connection closes.
 *   WebRowSet      a CachedRowSet that can read/write XML.
 *   FilteredRowSet a CachedRowSet with a filter applied to the view.
 *   JoinRowSet     a CachedRowSet that joins several others in memory.
 * </pre>
 *
 * <p>You get them from a <strong>RowSetFactory</strong>, which you get from
 * <strong>RowSetProvider</strong> — the point of the two-step being that the provider can be
 * swapped (an app server, a driver) without changing your code.
 */
public class E08_RowSets {

    public static void main(String[] args) throws SQLException {
        System.out.println("E08 - RowSets: JdbcRowSet, CachedRowSet and the factory");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        // ---- the factory ----
        System.out.println(Db.rule("the factory: RowSetProvider -> RowSetFactory"));
        RowSetFactory factory = RowSetProvider.newFactory();
        System.out.println("  RowSetProvider.newFactory() -> " + factory.getClass().getName());
        System.out.println("  the two-step exists so the provider can be swapped without");
        System.out.println("  changing the code that asks for a rowset.");

        try (Connection c = Db.open()) {

            // ---- JdbcRowSet: connected ----
            System.out.println(Db.rule("JdbcRowSet - connected, a ResultSet with setters"));
            try (JdbcRowSet jrs = factory.createJdbcRowSet()) {
                jrs.setUrl(Db.H2_URL);
                jrs.setUsername("sa");
                jrs.setPassword("");
                jrs.setCommand("SELECT id, name, mark FROM students WHERE mark >= ? ORDER BY mark DESC");
                jrs.setDouble(1, 85.0);
                jrs.execute();

                System.out.println("  students with mark >= 85:");
                while (jrs.next()) {
                    System.out.printf("    %d %-20s %.2f%n", jrs.getInt("id"), jrs.getString("name"), jrs.getDouble("mark"));
                }
                System.out.println("  note: no Connection or PreparedStatement in sight - the rowset");
                System.out.println("  holds the URL and the query itself.");
            }

            // ---- CachedRowSet: disconnected ----
            System.out.println(Db.rule("CachedRowSet - filled while connected, read after close"));

            CachedRowSet cached = factory.createCachedRowSet();
            // Fill it inside its own connection...
            try (Connection fill = Db.open()) {
                cached.setCommand("SELECT id, name, mark FROM students ORDER BY mark DESC");
                cached.execute(fill);
                System.out.println("  filled from the database");
            }   // <- the connection is CLOSED here

            // ...and read it now that the connection is gone.
            System.out.println("  connection closed. reading the cached rows anyway:");
            int n = 0;
            while (cached.next()) {
                n++;
                if (n <= 3) {
                    System.out.printf("    %d %-20s %.2f%n", cached.getInt("id"), cached.getString("name"), cached.getDouble("mark"));
                }
            }
            System.out.println("    ... " + n + " rows in total, all in memory, no connection");

            // This is the whole reason CachedRowSet exists: hand it to a UI layer, a report
            // writer, another thread - none of which should hold a database connection open.

            System.out.println(Db.rule("edit the cache, then write it back"));
            System.out.println("  A writable CachedRowSet needs a SIMPLE command. The JDK's");
            System.out.println("  CachedRowSetWriter builds its UPDATE by appending \"WHERE id = ?\"");
            System.out.println("  to the command you gave it - and appended to an ORDER BY clause");
            System.out.println("  that is not valid SQL. So this one selects without ordering, and");
            System.out.println("  we find the highest mark by walking the cache ourselves.");
            CachedRowSet editable = factory.createCachedRowSet();
            editable.setCommand("SELECT id, name, mark FROM students");
            try (Connection fill = Db.open()) {
                editable.execute(fill);
            }

            // Find the highest mark in the cache, and remember which row it is on.
            int topRow = 0;
            int topId = 0;
            double topMark = -1;
            String topName = null;
            editable.beforeFirst();
            while (editable.next()) {
                if (editable.getDouble("mark") > topMark) {
                    topMark = editable.getDouble("mark");
                    topName = editable.getString("name");
                    topId = editable.getInt("id");
                    topRow = editable.getRow();
                }
            }

            editable.absolute(topRow);
            System.out.println("  highest mark: " + topName + " / " + topMark);
            editable.updateDouble("mark", 99.00);
            editable.updateRow();                    // the change is in the CACHE, not the database
            System.out.println("  updated the cache to 99.00 - the database does not know yet");
            System.out.println("  database still says: " + markOf(topId)
                    + "   <- proof the cache and the table are separate");

            try (Connection back = Db.open()) {
                editable.acceptChanges(back);        // now push the cache back to the database
                System.out.println("  acceptChanges(connection) wrote the change through");
            } catch (SQLException e) {
                System.out.println("  acceptChanges failed: " + Db.firstLine(e));
            }
            System.out.println("  database now says  : " + markOf(topId));
            // Put the registry back to the seed, so re-running this example is clean.
            Db.resetRegistry();
            System.out.println("  (registry reset to the seed data)");

            // ---- WebRowSet: XML ----
            System.out.println(Db.rule("WebRowSet - a CachedRowSet that speaks XML"));
            try (WebRowSet web = factory.createWebRowSet()) {
                web.setCommand("SELECT id, title, fee FROM courses ORDER BY id");
                web.execute(c);
                java.io.StringWriter sw = new java.io.StringWriter();
                web.writeXml(sw);
                String xml = sw.toString();
                System.out.println("  the rowset as XML, first lines:");
                int lines = 0;
                for (String line : xml.split("\n")) {
                    if (lines++ >= 6) break;
                    System.out.println("    " + line.trim());
                }
                System.out.println("    ... (" + xml.split("\n").length + " lines in total)");
                System.out.println("  useful for sending a disconnected result over a network or to a file.");
            }

            System.out.println(Db.rule("which one, when"));
            System.out.println("  JdbcRowSet    you want a ResultSet with setters, still connected");
            System.out.println("  CachedRowSet  the result must outlive the connection - UI, reports, threads");
            System.out.println("  WebRowSet     the result must travel as XML");
            System.out.println("  FilteredRowSet  one cached result, several filtered views of it");
            System.out.println("  JoinRowSet    join several cached results without going back to the database");
        }

        System.out.println("\nA RowSet is a ResultSet you can configure, disconnect and pass around.");
    }

    /** Reads one student's mark straight from the database, to compare with the cache. */
    private static String markOf(int id) throws SQLException {
        try (Connection c = Db.open();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT mark FROM students WHERE id = " + id)) {
            return rs.next() ? String.format("%.2f", rs.getDouble(1)) : "(no such student)";
        }
    }
}
