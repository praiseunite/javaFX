package com.aptech.s08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * The one place in this project that knows how to reach the database.
 *
 * <p>This is the Session 7 helper with one addition: {@link #inTransaction}, which
 * runs a piece of work as a single unit. That single method is the whole subject of
 * Session 8 — everything else here is unchanged.
 *
 * <p>There is nothing H2-specific except {@link #H2_URL}. Point it at MySQL and none of
 * the Java above would move (see the H2 &amp; MySQL guide).
 */
public final class Db {

    /** The H2 database lives in ./data/registry.mv.db, relative to the project folder. */
    public static final String H2_URL = "jdbc:h2:./data/registry";

    /** A throwaway in-memory database, for examples that must not leave data behind. */
    public static final String H2_MEM_URL = "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1";

    private Db() { }

    /** The file-backed database every example uses, schema and seed loaded on first call. */
    public static Connection open() throws SQLException {
        Connection c = DriverManager.getConnection(H2_URL, "sa", "");
        ensureLoaded(c);
        return c;
    }

    /** A connection to an arbitrary database, with no schema handling. */
    public static Connection connect(String url) throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    /** Runs schema.sql then seed.sql, but only if the students table is not already there. */
    public static void ensureLoaded(Connection c) throws SQLException {
        if (tableExists(c, "STUDENTS")) return;
        runScript(c, "schema.sql");
        runScript(c, "seed.sql");
    }

    /**
     * Puts the registry back to exactly what db/seed.sql describes: eight students, four
     * courses, eight enrolments, four payments, and identity counters back at 1.
     *
     * <p>Every example calls this first, and that is on purpose. The H2 database is a
     * <em>file</em> that survives between runs, so without a reset the numbers printed by
     * E03 would depend on whether you had already run E01 — and a student following along
     * would see different output from the page. Resetting first makes every program
     * <strong>repeatable</strong>: run it once, run it ten times, same result.
     */
    public static void resetRegistry() throws SQLException {
        try (Connection c = open(); Statement st = c.createStatement()) {
            // child tables first: their foreign keys point at the parents.
            for (String table : List.of("fee_payments", "enrolments", "students", "courses")) {
                st.executeUpdate("DELETE FROM " + table);
            }
            // identity columns remember the highest id ever used - put them back to 1.
            for (String table : List.of("fee_payments", "enrolments", "students")) {
                st.execute("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH 1");
            }
            runScript(c, "seed.sql");
        }
    }

    /** Is a table present? Uses DatabaseMetaData — the subject of Session 7's E04. */
    public static boolean tableExists(Connection c, String table) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(null, null, table, new String[] { "TABLE" })) {
            return rs.next();
        }
    }

    // ---------------------------------------------------------------- transactions

    /**
     * The work to do inside one transaction. Implementations get the {@link Connection}
     * and may throw; if they do, the transaction is rolled back.
     */
    @FunctionalInterface
    public interface Work<T> {
        T run(Connection c) throws SQLException;
    }

    /**
     * Runs {@code work} as a single unit of work and returns its result.
     *
     * <p>This is the pattern every transactional method in this session uses:
     * <ol>
     *   <li>turn auto-commit OFF, so nothing is saved until we say so;</li>
     *   <li>do the work;</li>
     *   <li>{@code commit()} — make it all permanent;</li>
     *   <li>if anything threw, {@code rollback()} — undo everything the unit did.</li>
     * </ol>
     *
     * <p>The {@code catch} re-throws after rolling back, so the caller still learns that
     * it failed. Silently swallowing the failure here is the bug this whole session is
     * about: a half-finished transfer that reports success.
     */
    public static <T> T inTransaction(Work<T> work) throws SQLException {
        try (Connection c = open()) {
            boolean originalAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(originalAutoCommit);
            }
        }
    }

    // ---------------------------------------------------------------- scripts

    /**
     * Reads a .sql file from db/ and executes its statements, splitting on the semicolons
     * that end a line. {@code --} comments are stripped first. Enough for schema and seed.
     */
    public static void runScript(Connection c, String fileName) throws SQLException {
        String sql;
        try {
            sql = Files.readString(findDbFolder().resolve(fileName));
        } catch (IOException e) {
            throw new SQLException("could not read db/" + fileName
                    + " — run the program from the project folder", e);
        }
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
     * Finds the folder holding schema.sql and seed.sql — ./db first, then up a few levels,
     * so examples run whether started from the project folder or a build folder inside it.
     */
    private static Path findDbFolder() {
        Path here = Path.of("").toAbsolutePath();
        for (int i = 0; i < 4 && here != null; i++) {
            Path candidate = here.resolve("db");
            if (Files.isRegularFile(candidate.resolve("schema.sql"))) return candidate;
            here = here.getParent();
        }
        return Path.of("db");
    }

    /** Wipes the file database so a demo can start from nothing. */
    public static void deleteDatabaseFiles() {
        for (String name : List.of("registry.mv.db", "registry.trace.db", "registry.lock.db")) {
            try {
                Files.deleteIfExists(Path.of("data", name));
            } catch (IOException ignored) {
                // best effort
            }
        }
    }

    /** A horizontal rule the examples use to separate their sections on the console. */
    public static String rule(String title) {
        return "\n=== " + title + " " + "=".repeat(Math.max(0, 60 - title.length()));
    }

    /** A named money format used by every example that prints an amount. */
    public static String money(double amount) {
        return String.format("%.2f", amount);
    }

    /**
     * The first line of a driver's error message, with the SQL echo cut off.
     *
     * <p>H2 formats a failure across two lines, repeating our own SQL back at us:
     * <pre>
     *   NULL not allowed for column "AMOUNT"; SQL statement:
     *   INSERT INTO fee_payments (enrolment_id, amount) VALUES (?, ?) [90006-232]
     * </pre>
     * Everything from {@code "; SQL statement:"} on is our own text coming back. Cutting it
     * leaves {@code NULL not allowed for column "AMOUNT"} — which is the sentence a student
     * actually needs to read, in a console and on a slide.
     */
    public static String firstLine(String message) {
        if (message == null) return "(no message)";
        String line = message.split("\n")[0].trim();
        int cut = line.indexOf("; SQL statement:");
        if (cut >= 0) line = line.substring(0, cut).trim();
        return line;
    }

    /** The same, straight from a caught exception. */
    public static String firstLine(SQLException e) {
        return firstLine(e.getMessage());
    }
}
