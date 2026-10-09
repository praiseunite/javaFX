package com.aptech.s08;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * E07 — stored procedures: calling logic that lives inside the database.
 *
 * <p>A stored procedure is a routine the database runs for you. Organisations use them to keep
 * business rules in one place, to save a round trip by doing several steps at once, and to
 * control access (you can be granted "call this procedure" without being granted the tables it
 * touches).
 *
 * <p><strong>An honest note about H2.</strong> Oracle, SQL Server and PostgreSQL have their own
 * procedural languages (PL/SQL, T-SQL, PL/pgSQL) in which you write the body of a procedure in
 * SQL. H2 does not — it has no PL/SQL. What H2 gives instead is <code>CREATE ALIAS</code>, which
 * exposes an ordinary <strong>Java</strong> method as if it were a database function. The JDBC
 * side is identical either way: you call it with a <code>CallableStatement</code>, and the shape
 * <code>{ ? = call name(?) }</code> is the same one you would use against Oracle. So what you
 * learn here transfers; only the <code>CREATE</code> statement that puts the routine in place
 * differs, and the page says how.
 */
public class E07_StoredProcedures {

    /** An H2 alias body: an ordinary static Java method, exposed to SQL by CREATE ALIAS. */
    public static double feeWithTax(double fee) {
        return Math.round(fee * 1.10 * 100.0) / 100.0;
    }

    /** A second alias: total paid against an enrolment id, seeing the database itself. */
    public static double totalPaidFor(Connection c, int enrolmentId) throws SQLException {
        // H2 can pass the Connection as the first parameter of an alias - so a
        // "procedure" can run its own queries, exactly like a stored procedure would.
        try (var ps = c.prepareStatement(
                "SELECT COALESCE(SUM(amount), 0) FROM fee_payments WHERE enrolment_id = ?")) {
            ps.setInt(1, enrolmentId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("E07 - stored procedures: calling a routine that lives in the database");

        Db.resetRegistry();   // start from the seed, so this program prints the same thing every run

        try (Connection c = Db.open()) {

            DatabaseMetaData md = c.getMetaData();
            System.out.println(Db.rule("does this database do stored procedures?"));
            System.out.println("  supportsStoredProcedures() -> " + md.supportsStoredProcedures());
            System.out.println("  H2 has no PL/SQL of its own; it exposes Java methods with CREATE ALIAS.");

            // ---- put the routines in place ----
            System.out.println(Db.rule("CREATE ALIAS - a Java method, registered as a SQL routine"));
            try (Statement st = c.createStatement()) {
                st.execute("CREATE ALIAS IF NOT EXISTS FEEWITHTAX FOR 'com.aptech.s08.E07_StoredProcedures.feeWithTax'");
                st.execute("CREATE ALIAS IF NOT EXISTS TOTALPAIDFOR FOR 'com.aptech.s08.E07_StoredProcedures.totalPaidFor'");
            }
            System.out.println("  registered FEEWITHTAX and TOTALPAIDFOR");
            System.out.println("  (in Oracle this step would be CREATE OR REPLACE PROCEDURE ... in PL/SQL)");

            // ---- call it in a plain SELECT ----
            System.out.println(Db.rule("call it like a function"));
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT FEEWITHTAX(450.00) AS withtax")) {
                rs.next();
                System.out.printf("  SELECT FEEWITHTAX(450.00) -> %.2f%n", rs.getDouble("withtax"));
            }
            System.out.println();
            System.out.println("  Note what just happened. supportsStoredProcedures() said FALSE,");
            System.out.println("  and the routine ran anyway. The flag answers a narrower question:");
            System.out.println("  'do you have Oracle-style PL/SQL procedures?' H2 does not - it");
            System.out.println("  registers Java methods instead. The flag is a description, not a");
            System.out.println("  gate. When a flag and a working call disagree, believe the call.");

            // ---- call it with CallableStatement, the JDBC way ----
            System.out.println(Db.rule("CallableStatement - { ? = call name(?) }"));
            try (CallableStatement cs = c.prepareCall("{ ? = call FEEWITHTAX(?) }")) {
                cs.registerOutParameter(1, Types.DOUBLE);   // the ? on the left holds the result
                cs.setDouble(2, 380.00);                    // the argument
                cs.execute();
                System.out.printf("  call FEEWITHTAX(380.00) -> %.2f%n", cs.getDouble(1));
                System.out.println("  note: OUT parameter registered first, arguments set after.");
            }

            // ---- a routine that reads the database itself ----
            System.out.println(Db.rule("a routine with a query inside it"));
            // Enrolment 1 was fully paid in the seed data; enrolment 3 was not.
            try (CallableStatement cs = c.prepareCall("{ ? = call TOTALPAIDFOR(?) }")) {
                cs.registerOutParameter(1, Types.DOUBLE);
                cs.setInt(2, 1);
                cs.execute();
                System.out.printf("  TOTALPAIDFOR(enrolment 1) -> %.2f%n", cs.getDouble(1));
            }
            try (CallableStatement cs = c.prepareCall("{ ? = call TOTALPAIDFOR(?) }")) {
                cs.registerOutParameter(1, Types.DOUBLE);
                cs.setInt(2, 3);
                cs.execute();
                System.out.printf("  TOTALPAIDFOR(enrolment 3) -> %.2f%n", cs.getDouble(1));
            }

            System.out.println(Db.rule("why an organisation uses them"));
            System.out.println("  1. the rule lives in one place - change the procedure, not ten programs");
            System.out.println("  2. fewer round trips - one CALL can do several statements");
            System.out.println("  3. access control - grant CALL without granting the underlying tables");
            System.out.println();
            System.out.println("  The trade: business logic in the database is harder to test and");
            System.out.println("  version-control than logic in your Java. Many teams keep it thin");
            System.out.println("  deliberately. Know what the tool is for before reaching for it.");

            System.out.println(Db.rule("the part that changes between databases"));
            System.out.println("  H2          CREATE ALIAS FEEWITHTAX FOR 'com.aptech.s08.....'");
            System.out.println("  Oracle      CREATE OR REPLACE FUNCTION FEETAX(p IN NUMBER) RETURN NUMBER IS ...");
            System.out.println("  SQL Server  CREATE PROCEDURE dbo.FeeTax @p DECIMAL AS ...");
            System.out.println("  The CALL from Java is the same shape in all three.");
        }

        System.out.println("\nA stored procedure is logic the database runs. The JDBC call is portable; "
                + "the CREATE is not.");
    }
}
