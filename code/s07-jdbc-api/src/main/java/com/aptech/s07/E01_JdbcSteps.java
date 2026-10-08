package com.aptech.s07;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E01 — the six steps of a JDBC application, in the order you will always write them.
 *
 * <p>This is the smallest complete JDBC program there is. Every other example in the
 * session is this program with one step expanded, so it is worth reading twice: once for
 * the order, once for which steps hold a resource that must be closed.
 *
 * <pre>
 *   1  load or register the driver      (automatic since JDBC 4)
 *   2  get a Connection                 (the URL, user and password)
 *   3  create a Statement
 *   4  execute the SQL
 *   5  read the ResultSet
 *   6  close everything                 (try-with-resources, on every path)
 * </pre>
 */
public class E01_JdbcSteps {

    public static void main(String[] args) throws SQLException {

        System.out.println("E01 - the six steps of a JDBC application");

        // ---- Step 2: get a Connection ------------------------------------------
        // Steps 3-6 all live inside the try-with-resources, so the Connection is closed
        // on every path out - the normal exit and the one where something throws.
        try (Connection connection = Db.open()) {

            System.out.println("Connected to H2.  version: "
                    + connection.getMetaData().getDatabaseProductVersion());

            // ---- Step 3: create a Statement ------------------------------------
            // A Statement holds no query yet; it is the channel you send SQL down.
            // Use PreparedStatement (E05) the moment the SQL contains a value.
            try (Statement statement = connection.createStatement()) {

                // ---- Step 4: execute the SQL -----------------------------------
                // executeQuery()    -> returns a ResultSet   (a SELECT)
                // executeUpdate()   -> returns a row count   (INSERT, UPDATE, DELETE)
                // execute()         -> returns a boolean     (when you do not know which)

                // ---- Step 5: read the ResultSet ---------------------------------
                // next() moves the cursor to the next row and answers "was there one?".
                // It starts BEFORE the first row, which is why you always call it once
                // before reading anything - forgetting that is the classic first bug.
                String sql = "SELECT id, name, mark FROM students ORDER BY mark DESC";
                System.out.println(Db.rule("step 4 + 5 - execute, then read"));

                try (ResultSet rs = statement.executeQuery(sql)) {
                    int rows = 0;
                    while (rs.next()) {
                        // Read by NAME, not by number: a column added to the SELECT
                        // cannot renumber your code out from under you.
                        int    id   = rs.getInt("id");
                        String name = rs.getString("name");
                        double mark = rs.getDouble("mark");

                        System.out.printf("  %-20s id=%-3d mark=%.2f%n", name, id, mark);
                        rows++;
                    }
                    System.out.println("  " + rows + " rows");
                }
            }
        }
        // ---- Step 6 happened here, automatically. ------------------------------
        // Nothing below this line can leave a connection open, because the try block
        // above closed it - even if next() had thrown halfway through the result set.

        System.out.println(Db.rule("what step 1 looks like now"));
        System.out.println("  Class.forName(\"org.h2.Driver\") is NOT needed.");
        System.out.println("  Since JDBC 4 a driver on the classpath registers itself,");
        System.out.println("  so step 1 is now done by the line that puts the jar on the classpath.");
        System.out.println("\nThe six steps are the same six steps for any database.");
    }
}
