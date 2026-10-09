package com.aptech.s08;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * A throwaway probe: what does H2 2.3.232 actually support for stored procedures,
 * isolation levels and ResultSet features? Run once to design E07-E09 honestly.
 */
public class Probe {

    /** Exposed to H2 as a SQL function via CREATE ALIAS. Must be static. */
    public static String addStrings(String a, String b) {
        return a + "|" + b;
    }

    /** An H2 alias that computes a fee with tax — the shape a real stored function would have. */
    public static double withTax(double amount) {
        return Math.round(amount * 1.1 * 100.0) / 100.0;
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:probe;DB_CLOSE_DELAY=-1", "sa", "")) {
            DatabaseMetaData md = c.getMetaData();

            System.out.println("H2 version            : " + md.getDatabaseProductVersion());
            System.out.println("supportsStoredProcs   : " + md.supportsStoredProcedures());
            System.out.println("default isolation     : " + c.getTransactionIsolation()
                    + "  (TRANSACTION_READ_COMMITTED=" + Connection.TRANSACTION_READ_COMMITTED + ")");
            System.out.println("supports savepoints   : " + md.supportsSavepoints());
            System.out.println("supports batch updates: " + md.supportsBatchUpdates());
            System.out.println("supports scroll insens: " + md.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
            System.out.println("supports concur updat : " + md.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));

            // ---- CREATE ALIAS: a Java method exposed as a SQL function ----
            try (Statement st = c.createStatement()) {
                st.execute("CREATE ALIAS IF NOT EXISTS ADDSTRINGS FOR 'com.aptech.s08.Probe.addStrings'");
                st.execute("CREATE ALIAS IF NOT EXISTS WITHTAX   FOR 'com.aptech.s08.Probe.withTax'");
            }
            try (ResultSet rs = c.createStatement().executeQuery("SELECT ADDSTRINGS('a','b') AS r, WITHTAX(100.0) AS t")) {
                rs.next();
                System.out.println("\nSELECT ADDSTRINGS -> " + rs.getString("r"));
                System.out.println("SELECT WITHTAX(100) -> " + rs.getDouble("t"));
            }

            // ---- calling it through CallableStatement, the JDBC way ----
            try (CallableStatement cs = c.prepareCall("{ ? = call WITHTAX(?) }")) {
                cs.registerOutParameter(1, Types.DOUBLE);
                cs.setDouble(2, 450.0);
                cs.execute();
                System.out.println("CallableStatement WITHTAX(450) -> " + cs.getDouble(1));
            }
        }
    }
}
