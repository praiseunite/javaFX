package com.aptech.s07;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E05 — parameterized queries: PreparedStatement, and the four things it does for you.
 *
 * <p>A {@code PreparedStatement} is a Statement with holes in it. You write the SQL once with
 * {@code ?} where the values go, then hand the values over separately. That single change
 * buys four separate things, and students usually only know the first:
 *
 * <ol>
 *   <li><strong>Safety.</strong> A value can never become part of the SQL, so it cannot
 *       change the query's meaning. This is what stops SQL injection (see E06).</li>
 *   <li><strong>Correct types.</strong> {@code setDouble} sends a number as a number, so
 *       {@code 88.5} arrives as 88.5 — not as the text "88.5" the database has to guess at.</li>
 *   <li><strong>No quoting bugs.</strong> A name containing an apostrophe — O'Brien — is just
 *       a name. With string concatenation it is a syntax error, or worse.</li>
 *   <li><strong>Reuse.</strong> One prepared plan, many rows. The database can parse the
 *       statement once and run it a thousand times.</li>
 * </ol>
 *
 * <p>This program performs the full set of operations — INSERT, SELECT, UPDATE, DELETE — and
 * clears up after itself, so it can be run again from a clean database.
 */
public class E05_PreparedStatements {

    public static void main(String[] args) throws SQLException {

        System.out.println("E05 - parameterized queries with PreparedStatement");

        try (Connection c = Db.open()) {

            // ------------------------------------------------------------------
            //  INSERT — and get the id the database chose
            // ------------------------------------------------------------------
            System.out.println(Db.rule("insert - and read back the generated key"));

            String insert = "INSERT INTO students (name, email, course_id, mark) VALUES (?, ?, ?, ?)";
            int newId;

            // Statement.RETURN_GENERATED_KEYS is how you ask for the auto-numbered id.
            try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "O'Brien, Riley");   // an apostrophe - no escaping needed
                ps.setString(2, "riley@example.com");
                ps.setInt(3, 1);
                ps.setDouble(4, 77.25);

                int rows = ps.executeUpdate();
                System.out.println("  inserted " + rows + " row");

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    newId = keys.next() ? keys.getInt(1) : -1;
                    System.out.println("  the database assigned id = " + newId);
                }
            }
            System.out.println("  note the name: " + "O'Brien, Riley"
                    + " — with concatenation that apostrophe would have broken the SQL.");

            // ------------------------------------------------------------------
            //  SELECT — the same ? in a WHERE clause
            // ------------------------------------------------------------------
            System.out.println(Db.rule("select - one parameterised lookup"));

            String find = "SELECT id, name, mark FROM students WHERE name = ?";
            try (PreparedStatement ps = c.prepareStatement(find)) {
                ps.setString(1, "O'Brien, Riley");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("  found: %s (id=%d, mark=%.2f)%n",
                                rs.getString("name"), rs.getInt("id"), rs.getDouble("mark"));
                    }
                }
            }

            // ------------------------------------------------------------------
            //  UPDATE — a parameter in SET and another in WHERE
            // ------------------------------------------------------------------
            System.out.println(Db.rule("update - two parameters, one statement"));

            String update = "UPDATE students SET mark = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(update)) {
                ps.setDouble(1, 81.50);
                ps.setInt(2, newId);
                System.out.println("  updated " + ps.executeUpdate() + " row");
            }

            // ------------------------------------------------------------------
            //  REUSE — the same statement, many rows
            // ------------------------------------------------------------------
            System.out.println(Db.rule("reuse - one statement, many rows"));

            String insertMany = "INSERT INTO students (name, email, course_id, mark) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(insertMany)) {
                String[][] batch = {
                        { "Hopper, Ada",  "hopper@example.com",  "2", "90.00" },
                        { "Lovelace, Jo", "jo@example.com",      "3", "72.50" },
                        { "Turing, Eve",  "eve@example.com",     "4", "84.00" },
                };
                for (String[] row : batch) {
                    ps.setString(1, row[0]);
                    ps.setString(2, row[1]);
                    ps.setInt(3, Integer.parseInt(row[2]));
                    ps.setDouble(4, Double.parseDouble(row[3]));
                    ps.addBatch();                  // queue it, do not run it yet
                }
                int[] counts = ps.executeBatch();   // now run them all
                System.out.println("  inserted " + counts.length + " rows in one batch");
            }

            // ------------------------------------------------------------------
            //  DELETE — clear up, without touching the twelve seeded rows
            // ------------------------------------------------------------------
            System.out.println(Db.rule("delete - clear up the rows we added"));

            // Delete EXACTLY the four emails this program inserted. An earlier draft used
            // "WHERE email LIKE '%@example.com'", which also matched the twelve seeded
            // students and emptied the table — a reminder that a DELETE's WHERE clause is
            // the most expensive thing in the file to get wrong.
            String delete = "DELETE FROM students WHERE email IN (?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(delete)) {
                ps.setString(1, "riley@example.com");
                ps.setString(2, "hopper@example.com");
                ps.setString(3, "jo@example.com");
                ps.setString(4, "eve@example.com");
                int deleted = ps.executeUpdate();
                System.out.println("  deleted " + deleted + " rows we added above");
            }

            System.out.println(Db.rule("confirm the database is back to the seeded twelve"));
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                rs.next();
                System.out.println("  students now: " + rs.getInt(1) + "  (the twelve we started with)");
            }
        }

        System.out.println("\nEvery value went in through a ?. None of them could change the SQL.");
    }
}
