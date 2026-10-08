package com.aptech.s07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The one place in this project that knows how to reach the database.
 *
 * <p>Every E01-E07 program, the lab and the practice solutions go through this class.
 * It exists so that the connection details appear exactly once — which is the whole
 * reason the course can point the same code at H2 today and MySQL tomorrow by changing
 * one string (see E07_MySqlComparison).
 *
 * <p>Nothing here is H2-specific. The URL is the only line that would change, and even
 * that is a constant rather than a literal sprinkled through the examples.
 */
public final class Db {

    /** The H2 database lives in ./data/registry.mv.db, relative to the project folder. */
    public static final String H2_URL = "jdbc:h2:./data/registry";

    /** A throwaway database in memory — used by tests and by the "start clean" demos. */
    public static final String H2_MEM_URL = "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1";

    /** The MySQL URL that would replace either of the above. Only shown, never used here. */
    public static final String MYSQL_URL =
            "jdbc:mysql://localhost:3306/registry"
            + "?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private Db() { }

    /** The file-backed database every example uses, schema and seed loaded on first call. */
    public static Connection open() throws SQLException {
        Connection c = DriverManager.getConnection(H2_URL, "sa", "");
        ensureLoaded(c);
        return c;
    }

    /** A connection to the named database with no schema handling — for the examples that create their own. */
    public static Connection connect(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Runs schema.sql then seed.sql, but only if the students table is not already there. */
    public static void ensureLoaded(Connection c) throws SQLException {
        if (tableExists(c, "STUDENTS")) return;
        runScript(c, "schema.sql");
        runScript(c, "seed.sql");
    }

    /** Is a table present in this database? Uses DatabaseMetaData — the subject of E04. */
    public static boolean tableExists(Connection c, String table) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(null, null, table, new String[] { "TABLE" })) {
            return rs.next();
        }
    }

    /**
     * Reads a .sql file from db/ and executes its statements.
     *
     * <p>Statements are separated by a semicolon at the end of a line. That is enough for the
     * schema and seed files, and it is deliberately simple — a full SQL parser is not the point
     * of this session. Comments starting with {@code --} are stripped first.
     */
    public static void runScript(Connection c, String fileName) throws SQLException {
        String sql;
        try {
            sql = Files.readString(findDbFolder().resolve(fileName));
        } catch (IOException e) {
            throw new SQLException("could not read db/" + fileName
                    + " — run the program from the project folder", e);
        }
        // strip -- comments, then split on the semicolons that end statements
        StringBuilder cleaned = new StringBuilder();
        for (String line : sql.split("\r?\n")) {
            int cut = line.indexOf("--");
            cleaned.append(cut >= 0 ? line.substring(0, cut) : line).append('\n');
        }
        try (Statement st = c.createStatement()) {
            for (String statement : cleaned.toString().split(";")) {
                if (!statement.isBlank()) st.execute(statement);
            }
        }
    }

    /**
     * Finds the folder holding schema.sql and seed.sql.
     *
     * <p>Checks ./db first, then walks up a few levels, so the examples work whether they are run
     * from the project folder (Maven, IntelliJ) or from a build folder inside it.
     */
    private static Path findDbFolder() {
        Path here = Path.of("").toAbsolutePath();
        for (int i = 0; i < 4 && here != null; i++) {
            Path candidate = here.resolve("db");
            if (Files.isRegularFile(candidate.resolve("schema.sql"))) return candidate;
            here = here.getParent();
        }
        return Path.of("db");   // fall back and let the error name the real problem
    }

    /** Wipes the file database so a demo can start from nothing. Deletes registry.mv.db. */
    public static void deleteDatabaseFiles() {
        for (String name : List.of("registry.mv.db", "registry.trace.db", "registry.lock.db")) {
            try {
                Files.deleteIfExists(Path.of("data", name));
            } catch (IOException ignored) {
                // best effort: a lock file held by another process is not fatal here
            }
        }
    }

    /** A small helper for the many examples that print a query result: returns column labels. */
    public static List<String> columnNames(ResultSet rs) throws SQLException {
        int n = rs.getMetaData().getColumnCount();
        List<String> names = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) names.add(rs.getMetaData().getColumnLabel(i));
        return names;
    }

    /** A horizontal rule the examples use to separate their sections on the console. */
    public static String rule(String title) {
        return "\n=== " + title + " " + "=".repeat(Math.max(0, 60 - title.length()));
    }
}
