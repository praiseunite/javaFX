package com.aptech.s07;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * E04 — database meta information: DatabaseMetaData and ResultSetMetaData.
 *
 * <p>Meta information is "data about data". JDBC gives you two windows into it, and beginners
 * confuse the two constantly, so fix the distinction first:
 *
 * <pre>
 *   DatabaseMetaData     describes the DATABASE   - which tables, columns, keys and
 *                                                  features this database has.
 *                        Obtained from Connection.
 *
 *   ResultSetMetaData    describes ONE RESULT SET - how many columns this query returned,
 *                                                  what they are called, what Java type
 *                                                  each one converts to.
 *                        Obtained from a ResultSet.
 * </pre>
 *
 * <p>This is the material the manual calls "Database Meta Information", and it is not
 * decoration: it is how a program can be written against a database it has never seen.
 */
public class E04_DatabaseMeta {

    public static void main(String[] args) throws SQLException {

        System.out.println("E04 - database meta information");

        try (Connection c = Db.open()) {
            DatabaseMetaData md = c.getMetaData();

            System.out.println(Db.rule("DatabaseMetaData - what the database is"));
            System.out.println("  product      : " + md.getDatabaseProductName()
                    + " " + md.getDatabaseProductVersion());
            System.out.println("  driver       : " + md.getDriverName()
                    + " " + md.getDriverVersion());
            System.out.println("  URL          : " + md.getURL());
            System.out.println("  user         : " + md.getUserName());
            System.out.println("  identifier quoting : " + md.getIdentifierQuoteString());
            System.out.println("  read only?   : " + md.isReadOnly());

            System.out.println(Db.rule("what tables exist"));
            // Schema "PUBLIC" is where your own tables live; H2 keeps its own catalogue
            // (CONSTANTS, USERS, SETTINGS ...) in INFORMATION_SCHEMA. Asking for PUBLIC
            // is what makes this list answer "what did I create?" rather than "what is H2
            // built out of?" — using the metadata to filter metadata.
            try (ResultSet rs = md.getTables(null, "PUBLIC", "%", new String[] { "TABLE" })) {
                while (rs.next()) {
                    System.out.printf("  %-12s  (%s)%n", rs.getString("TABLE_NAME"), rs.getString("TABLE_TYPE"));
                }
            }

            System.out.println(Db.rule("the columns of students, straight from the catalogue"));
            System.out.printf("  %-12s %-22s %-6s %s%n", "COLUMN", "TYPE", "NULL", "DEFAULT");
            try (ResultSet rs = md.getColumns(null, "PUBLIC", "STUDENTS", null)) {
                while (rs.next()) {
                    System.out.printf("  %-12s %-22s %-6s %s%n",
                            rs.getString("COLUMN_NAME"),
                            rs.getString("TYPE_NAME"),
                            rs.getString("IS_NULLABLE"),
                            String.valueOf(rs.getString("COLUMN_DEF")));
                }
            }

            System.out.println(Db.rule("the key relationship, discovered not assumed"));
            try (ResultSet rs = md.getImportedKeys(null, "PUBLIC", "STUDENTS")) {
                while (rs.next()) {
                    System.out.printf("  %s.%s -> %s.%s%n",
                            rs.getString("FKTABLE_NAME"), rs.getString("FKCOLUMN_NAME"),
                            rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"));
                }
            }

            System.out.println(Db.rule("what this database supports"));
            System.out.println("  transactions      : " + md.supportsTransactions());
            System.out.println("  scrollable results : " + md.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
            System.out.println("  batch updates     : " + md.supportsBatchUpdates());
            System.out.println("  outer joins       : " + md.supportsOuterJoins());

            // --------------------------------------------------------------------
            // ResultSetMetaData - describes ONE query's output
            // --------------------------------------------------------------------
            System.out.println(Db.rule("ResultSetMetaData - what THIS query returned"));

            String sql = "SELECT s.id, s.name, s.mark, c.title AS course "
                       + "FROM students s JOIN courses c ON c.id = s.course_id "
                       + "WHERE s.mark >= ? ORDER BY s.mark DESC";

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDouble(1, 85.0);

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData rm = rs.getMetaData();
                    System.out.println("  " + rm.getColumnCount() + " columns:");

                    for (int i = 1; i <= rm.getColumnCount(); i++) {
                        System.out.printf("    %d  %-10s Java type: %-20s  table: %s%n",
                                i,
                                rm.getColumnLabel(i),          // what the column is CALLED (alias-aware)
                                rm.getColumnClassName(i),      // which Java class getObject() returns
                                rm.getTableName(i));           // which table it came from
                    }

                    System.out.println("  ...so this program could label and print any query at all,\n"
                                     + "  without being told in advance what columns it would get.");

                    System.out.println(Db.rule("the rows themselves"));
                    while (rs.next()) {
                        // Read by the label the metadata just gave us - the same labels.
                        System.out.printf("  %-16s %-22s %6.2f%n",
                                rs.getString("name"), rs.getString("course"), rs.getDouble("mark"));
                    }
                }
            }
        }

        System.out.println("\nMeta information is what lets one program work against a database");
        System.out.println("whose shape it was never told in advance.");
    }
}
