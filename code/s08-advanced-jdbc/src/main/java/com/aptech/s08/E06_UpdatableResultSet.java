package com.aptech.s08;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E06 — updatable result sets: changing a row through the cursor that read it.
 *
 * <p>An updatable <code>ResultSet</code> lets you edit the row the cursor is sitting on and
 * write the change back with <code>updateRow()</code> — no separate <code>UPDATE</code>
 * statement. It also supports <code>moveToInsertRow()</code>/<code>insertRow()</code> and
 * <code>deleteRow()</code>. This is how a data grid binds directly to a query result.
 *
 * <p>Two honest caveats, both shown in the output below:
 * <ul>
 *   <li>The <code>SELECT</code> must be simple — one table, no joins, and the key column has to
 *       be present. Ask for an updatable cursor over a join and you get
 *       <code>CONCUR_READ_ONLY</code> instead, without an error.</li>
 *   <li>It is convenient, not free. Every <code>updateRow()</code> is a statement to the
 *       database, and readability suffers compared with a plain <code>UPDATE</code> whose SQL
 *       says what it does. Use it for grid-style editing, not as the default.</li>
 * </ul>
 */
public class E06_UpdatableResultSet {

    public static void main(String[] args) throws SQLException {
        System.out.println("E06 - updatable result sets: update, insert and delete through the cursor");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            // Work on a scratch copy so the seeded students are never disturbed.
            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE IF EXISTS upd_demo");
                st.execute("CREATE TABLE upd_demo (id INT PRIMARY KEY, name VARCHAR(40), mark DECIMAL(5,2))");
                st.executeUpdate("INSERT INTO upd_demo VALUES "
                        + "(1, 'Ada Lovelace',   88.50),"
                        + "(2, 'Grace Hopper',   91.00),"
                        + "(3, 'Alan Turing',    76.25)");
            }

            // ---- what the driver actually granted ----
            System.out.println(Db.rule("asking for an updatable cursor"));
            try (Statement st = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                 ResultSet rs = st.executeQuery("SELECT id, name, mark FROM upd_demo ORDER BY id")) {

                System.out.println("  requested concurrency: CONCUR_UPDATABLE");
                System.out.println("  granted concurrency  : " + concName(rs.getConcurrency())
                        + (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE
                            ? "   (the driver agreed)" : "   <-- the driver downgraded us"));

                if (rs.getConcurrency() != ResultSet.CONCUR_UPDATABLE) {
                    System.out.println("  this driver will not update through the cursor - the rest is skipped");
                    return;
                }

                // ---- UPDATE a row through the cursor ----
                System.out.println(Db.rule("updateRow - change the row under the cursor"));
                if (rs.absolute(3)) {
                    System.out.println("  row 3 before: " + rs.getString("name") + " / " + rs.getDouble("mark"));
                    rs.updateDouble("mark", 82.75);
                    rs.updateRow();                  // writes it back
                    System.out.println("  updateDouble(\"mark\", 82.75) + updateRow()");
                    System.out.println("  row 3 after : " + rs.getString("name") + " / " + rs.getDouble("mark")
                            + "   (read back through the same cursor)");
                }

                // ---- INSERT a new row ----
                System.out.println(Db.rule("insertRow - build a row, then move it into the table"));
                rs.moveToInsertRow();                    // a blank staging row
                rs.updateInt("id", 4);
                rs.updateString("name", "Katherine Johnson");
                rs.updateDouble("mark", 95.75);
                rs.insertRow();
                rs.moveToCurrentRow();                   // come back to where we were
                System.out.println("  inserted id 4: Katherine Johnson / 95.75");

                // ---- what the table holds now: the update AND the insert ----
                System.out.println(Db.rule("the table now, read with a plain SELECT"));
            }
            printTable(c);

            // ---- DELETE a row ----
            try (Statement st = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                 ResultSet rs = st.executeQuery("SELECT id, name, mark FROM upd_demo ORDER BY id")) {
                System.out.println(Db.rule("deleteRow - remove the row under the cursor"));
                rs.absolute(2);
                System.out.println("  deleting " + rs.getString("name") + " (id " + rs.getInt("id") + ")");
                rs.deleteRow();
                System.out.println("  note: the row is gone from the TABLE, not just from this cursor.");
            }
            System.out.println(Db.rule("the table after the delete"));
            printTable(c);

            // ---- the caveat: a join silently downgrades the cursor ----
            System.out.println(Db.rule("the caveat - a join gives you a read-only cursor"));
            try (Statement st = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                 ResultSet rs = st.executeQuery(
                        "SELECT s.id, s.name, c.title FROM students s JOIN courses c ON c.id = s.course_id")) {
                System.out.println("  requested: CONCUR_UPDATABLE");
                System.out.println("  granted  : " + concName(rs.getConcurrency())
                        + "   <- the driver downgraded it, with no error");
                System.out.println("  a join has no single row to write back to - so an updatable");
                System.out.println("  cursor over one is read-only. Check getConcurrency() if it matters.");
            }

            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE upd_demo");
            }
        }

        System.out.println("\nAn updatable cursor edits the row it is on. Convenient for grids, "
                + "less clear than a plain UPDATE.");
    }

    /** Prints the scratch table, so every change above can be checked against the real data. */
    private static void printTable(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, mark FROM upd_demo ORDER BY id")) {
            while (rs.next()) {
                System.out.printf("  %d %-20s %.2f%n", rs.getInt("id"), rs.getString("name"), rs.getDouble("mark"));
            }
        }
    }

    private static String concName(int concur) {
        return switch (concur) {
            case ResultSet.CONCUR_READ_ONLY  -> "CONCUR_READ_ONLY";
            case ResultSet.CONCUR_UPDATABLE  -> "CONCUR_UPDATABLE";
            default -> "UNKNOWN(" + concur + ")";
        };
    }
}
