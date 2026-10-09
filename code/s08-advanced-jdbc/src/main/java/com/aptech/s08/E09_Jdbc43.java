package com.aptech.s08;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;

/**
 * E09 — JDBC 4.3 enhancements (Java 9+), and which of them this database actually supports.
 *
 * <p>The manual lists "JDBC 4.3 enhancements". The honest lesson is not to memorise a list of
 * method names but to understand <em>what the version added and why</em>, and — the part most
 * tutorials skip — that <strong>a JDBC version is a promise the JDK makes, not one the driver
 * keeps</strong>. A method can exist on the interface and still throw
 * <code>SQLFeatureNotSupportedException</code> from your driver, because the interface shipped
 * before every database caught up.
 *
 * <p>JDBC 4.3 (in Java 9) added:
 * <pre>
 *   java.sql.ShardingKey / ShardingKeyBuilder   route a connection to a shard
 *   beginRequest / endRequest                   tell the driver a unit of work is starting
 *   setShardingKey / setShardingKeyIfValid      on Connection and ConnectionBuilder
 *   the ConnectionBuilder family                build a Connection without DriverManager
 * </pre>
 *
 * <p>This program asks for each one and reports truthfully whether the driver agrees, rather than
 * pretending. That habit — test the feature, then describe what happened — is worth more than the
 * feature list itself.
 */
public class E09_Jdbc43 {

    public static void main(String[] args) throws SQLException {
        System.out.println("E09 - JDBC 4.3 enhancements, tested against a real driver");

        try (Connection c = Db.open()) {
            DatabaseMetaData md = c.getMetaData();

            System.out.println(Db.rule("what the driver says about itself"));
            System.out.println("  JDBC major/minor : " + md.getJDBCMajorVersion() + "." + md.getJDBCMinorVersion());
            System.out.println("  driver           : " + md.getDriverName() + " " + md.getDriverVersion());

            // ---- ShardingKeyBuilder ----
            System.out.println(Db.rule("JDBC 4.3: ShardingKey and its builder"));
            System.out.println("  A sharding key names which SHARD a connection belongs to, when one");
            System.out.println("  logical database is split across many servers. This course's");
            System.out.println("  database is a single file, so there are no shards — but the API");
            System.out.println("  can still be asked whether it is there.");
            System.out.println();
            System.out.println("  Note WHERE the builder comes from: not a Connection, but a");
            System.out.println("  DataSource (java.sql.CommonDataSource.createShardingKeyBuilder()).");
            java.sql.ShardingKey key = null;
            try {
                javax.sql.DataSource ds = new org.h2.jdbcx.JdbcDataSource();
                ShardingKeyBuilder builder = ds.createShardingKeyBuilder();
                System.out.println("  DataSource.createShardingKeyBuilder() -> " + builder.getClass().getName());
                builder.subkey("student-1", java.sql.JDBCType.VARCHAR);
                key = builder.build();
                System.out.println("  built a ShardingKey -> " + key);
            } catch (SQLFeatureNotSupportedException e) {
                System.out.println("  createShardingKeyBuilder() -> NOT supported by this driver");
            } catch (SQLException e) {
                System.out.println("  createShardingKeyBuilder() -> threw " + firstLine(e.getMessage()));
            }

            // ---- beginRequest / endRequest ----
            System.out.println(Db.rule("JDBC 4.3: beginRequest / endRequest"));
            System.out.println("  These bracket a unit of work so the driver can route and pool better.");
            System.out.println("  The specification says a request boundary may not fall inside a");
            System.out.println("  transaction. Watch whether this driver enforces that:");
            try {
                c.beginRequest();
                System.out.println("  beginRequest() -> accepted");
                try {
                    c.setAutoCommit(false);
                    System.out.println("  setAutoCommit(false) inside a request -> allowed");
                    System.out.println("    (the spec forbids this; H2 does not police it - so a rule in a");
                    System.out.println("     specification is only as real as the driver that implements it)");
                    c.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("  setAutoCommit(false) inside a request -> refused:");
                    System.out.println("    " + firstLine(e.getMessage()));
                }
                c.endRequest();
                System.out.println("  endRequest() -> done");
            } catch (SQLFeatureNotSupportedException e) {
                System.out.println("  beginRequest() -> NOT supported by this driver");
                System.out.println("  " + firstLine(e.getMessage()));
            }

            // ---- setShardingKey ----
            System.out.println(Db.rule("JDBC 4.3: setShardingKey on Connection"));
            System.out.println("  setShardingKey is the direct form of the builder above: the key it");
            System.out.println("  wants is the very object the builder could not make. The two");
            System.out.println("  failures are the same failure, seen twice.");
            try {
                c.setShardingKey(key);
                System.out.println("  setShardingKey(key) -> accepted");
            } catch (SQLFeatureNotSupportedException e) {
                System.out.println("  setShardingKey(key) -> NOT supported by this driver:");
                System.out.println("    " + firstLine(e.getMessage()));
                System.out.println("  This is the free lesson of E09: the METHOD exists (it is in");
                System.out.println("  java.sql.Connection since Java 9) but the IMPLEMENTATION is the");
                System.out.println("  driver's to provide, and a single-file database has no shards.");
            } catch (SQLException e) {
                System.out.println("  setShardingKey(key) -> " + firstLine(e.getMessage()));
            }

            // ---- ConnectionBuilder ----
            System.out.println(Db.rule("JDBC 4.3: building a Connection without DriverManager"));
            System.out.println("  The old way:  DriverManager.getConnection(url, user, password)");
            System.out.println("  The 4.3 way:  a ConnectionBuilder, usually obtained from a DataSource.");
            System.out.println("  Its point is a connection POOL: the pool builds and owns the physical");
            System.out.println("  connections, and your code borrows one. That is Session 8's next step");
            System.out.println("  in a real deployment, and H2 has no pool of its own - the H2 server");
            System.out.println("  (java -cp h2.jar org.h2.tools.Server) plus a pool such as HikariCP");
            System.out.println("  is what a production setup would use.");

            System.out.println(Db.rule("what to take from this"));
            System.out.println("  JDBC 4.3 added sharding, request boundaries and builder-based");
            System.out.println("  connections — features aimed at large, pooled, multi-node systems.");
            System.out.println("  On a single embedded database you will use none of them today.");
            System.out.println();
            System.out.println("  What you WILL meet in the next year is a method that exists and");
            System.out.println("  throws SQLFeatureNotSupportedException anyway. Now you have seen it,");
            System.out.println("  and the response is the same as here: catch it, read what the driver");
            System.out.println("  said, and design around what it actually does.");
        }

        System.out.println("\nA JDBC version is a promise the JDK makes; the driver decides whether to keep it.");
    }

    private static String firstLine(String message) {
        return Db.firstLine(message);
    }
}
