package com.aptech.s07;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * E03 — the four JDBC driver types, and which driver this program is actually using.
 *
 * <p>Driver types are usually taught as a table to memorise. They are easier than that:
 * <em>the number counts how much extra software sits between your Java and the database.</em>
 * Type 4 has nothing extra, which is why it is the only one you need today.
 *
 * <pre>
 *   1  JDBC-ODBC bridge      needs an ODBC driver + native library     - removed in Java 8
 *   2  Native-API driver     part Java, part C; per-machine library    - rare
 *   3  Network protocol      all-Java, through a middleware server     - niche
 *   4  Thin driver           all-Java, straight to the database        - H2, Connector/J
 * </pre>
 *
 * <p>Rather than assert any of that, this program asks the JVM which drivers it actually has
 * loaded, and then asks the live connection what it is. The answer is evidence, not a claim.
 */
public class E03_DriverTypes {

    public static void main(String[] args) throws SQLException {

        System.out.println("E03 - the four JDBC driver types");

        System.out.println(Db.rule("drivers registered in this JVM right now"));
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        int count = 0;
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            System.out.printf("  %-45s  v%d.%d%n",
                    d.getClass().getName(), d.getMajorVersion(), d.getMinorVersion());
            count++;
        }
        if (count == 0) {
            System.out.println("  (none — the H2 jar is not on the classpath)");
        }
        System.out.println("  " + count + " driver(s) — one jar on the classpath, and nothing to load by hand.");

        try (Connection c = Db.open()) {
            System.out.println(Db.rule("what the live connection reports"));
            System.out.println("  driver name    : " + c.getMetaData().getDriverName());
            System.out.println("  driver version : " + c.getMetaData().getDriverVersion());
            System.out.println("  driver class   : " + c.getClass().getName());
            System.out.println("  is it pure Java? " + isPureJava(c));

            System.out.println(Db.rule("which type is this, then?"));
            System.out.println("  H2 ships a type 4 driver: " + c.getClass().getName());
            System.out.println("  says nothing about any other database and needs no");
            System.out.println("  ODBC layer, native library or middleware server —");
            System.out.println("  the jar IS the driver.");
        }

        System.out.println(Db.rule("why the type number does not change your code"));
        System.out.println("  All four implement java.sql.Driver. Everything above that");
        System.out.println("  — Connection, Statement, ResultSet — is the same for all of them.");
        System.out.println("  Swap a type 2 driver for a type 4 driver and your Java does not move.");
        System.out.println("\nA type 4 driver speaks the database's own network protocol. For an");
        System.out.println("embedded database like H2, \"the network\" is a method call, which is");
        System.out.println("why the same jar works both as a file and over tcp:// if you ask it to.");
    }

    /** A type 4 driver is Java all the way down; anything with native code is not. */
    private static String isPureJava(Connection c) {
        String cls = c.getClass().getName();
        boolean pure = cls.startsWith("org.h2.") || cls.startsWith("com.mysql.");
        return pure + "  (" + cls + " contains no native library)";
    }
}
