package com.aptech.a4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * A4 SELF-CHECK — run this class to test your StudentRegistry. Aim for all PASS.
 *
 * It uses a throwaway in-memory database, so it never touches your real data and can be
 * run as often as you like. Every check is something the assignment asked for.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    interface Check { void run() throws Exception; }

    /** Fresh database per run, seeded with two courses so the foreign key has something to point at. */
    private static final String URL = "jdbc:h2:mem:a4check;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws Exception {
        System.out.println("=== A4 Self-Check ===\n");

        seedCourses();

        run("R1 createSchema() creates both tables, and is safe to call twice", () -> {
            StudentRegistry r = new StudentRegistry(URL);
            r.createSchema();
            r.createSchema();   // must not throw "table already exists"

            try (Connection c = DriverManager.getConnection(URL, "sa", "");
                 ResultSet rs = c.getMetaData().getTables(null, null, "STUDENTS", null)) {
                check(rs.next(), "the students table was not created");
            }
            try (Connection c = DriverManager.getConnection(URL, "sa", "");
                 ResultSet rs = c.getMetaData().getTables(null, null, "COURSES", null)) {
                check(rs.next(), "the courses table was not created");
            }
        });

        run("R2 insert() returns the id the database assigned", () -> {
            StudentRegistry r = fresh();
            int ada = r.insert("Ada Lovelace", "ada@ex.com", 1, 88.50);
            int grace = r.insert("Grace Hopper", "grace@ex.com", 1, 91.00);
            check(ada > 0, "insert() returned " + ada + " — the generated key, not -1, is required");
            check(grace > ada, "the second id should be larger than the first (got " + grace + " after " + ada + ")");
        });

        run("R3 findById() returns the student, or null", () -> {
            StudentRegistry r = fresh();
            int id = r.insert("Alan Turing", "alan@ex.com", 1, 76.25);

            StudentRegistry.Student s = r.findById(id);
            check(s != null, "findById() returned null for a student that exists");
            check("Alan Turing".equals(s.name()), "expected name 'Alan Turing' but got " + s.name());
            check(Math.abs(s.mark() - 76.25) < 0.001, "expected mark 76.25 but got " + s.mark());

            check(r.findById(9999) == null, "findById() on a missing id must return null");
        });

        run("R4 searchByName() finds a partial name", () -> {
            StudentRegistry r = fresh();
            r.insert("Ada Lovelace", "ada@ex.com", 1, 88.50);
            r.insert("Grace Hopper", "grace@ex.com", 1, 91.00);
            r.insert("Alan Turing", "alan@ex.com", 1, 76.25);

            List<StudentRegistry.Student> found = r.searchByName("a");
            check(found != null, "searchByName() returned null");
            check(found.size() == 3, "searching 'a' should match all three names, found " + found.size());

            List<StudentRegistry.Student> grace = r.searchByName("Grace");
            check(grace.size() == 1, "searching 'Grace' should match one row, found " + grace.size());
            check(grace.isEmpty() || "Grace Hopper".equals(grace.get(0).name()),
                    "the one match should be Grace Hopper");
        });

        run("R4 searchByName() is parameterized - an injection returns nothing", () -> {
            StudentRegistry r = fresh();
            r.insert("Ada Lovelace", "ada@ex.com", 1, 88.50);
            r.insert("Grace Hopper", "grace@ex.com", 1, 91.00);

            // If the search glues this into the SQL, the WHERE becomes true for every row
            // and the table leaks. With a ? it is just a name that matches nothing.
            List<StudentRegistry.Student> leaked = r.searchByName("' OR '1'='1");
            check(leaked.isEmpty(),
                    "the search returned " + leaked.size() + " rows for an injection payload - "
                            + "the value is reaching the SQL, not a parameter");
        });

        run("R5 updateMark() changes exactly one row", () -> {
            StudentRegistry r = fresh();
            int id = r.insert("Margaret Hamilton", "margaret@ex.com", 1, 70.00);

            check(r.updateMark(id, 95.50) == 1, "updateMark() should change 1 row");
            check(Math.abs(r.findById(id).mark() - 95.50) < 0.001,
                    "the mark was not updated (found " + r.findById(id).mark() + ")");
            check(r.updateMark(9999, 50.0) == 0, "updateMark() on a missing id should change 0 rows");
        });

        run("R6 delete() removes exactly one row", () -> {
            StudentRegistry r = fresh();
            int id = r.insert("Temporary Student", "temp@ex.com", 1, 60.00);

            check(r.delete(id) == 1, "delete() should remove 1 row");
            check(r.findById(id) == null, "the student is still there after delete()");
            check(r.delete(9999) == 0, "delete() on a missing id should remove 0 rows");
        });

        run("R7 count() reports the number of students", () -> {
            StudentRegistry r = fresh();
            check(r.count() == 0, "a fresh table should hold 0 students, reported " + r.count());
            r.insert("A", "a@ex.com", 1, 50.0);
            r.insert("B", "b@ex.com", 1, 60.0);
            check(r.count() == 2, "after two inserts count() should be 2, reported " + r.count());
        });

        run("R8 columnNames() reads the real schema from DatabaseMetaData", () -> {
            StudentRegistry r = fresh();
            List<String> cols = r.columnNames();
            check(cols != null, "columnNames() returned null");
            check(cols.size() == 5,
                    "the students table has 5 columns, but " + cols.size() + " were reported: " + cols);
            check(cols.contains("ID") || cols.contains("id"),
                    "the column names should include the id column, got " + cols);
            check(cols.contains("MARK") || cols.contains("mark"),
                    "the column names should include the mark column, got " + cols);
        });

        run("the whole registry works from a clean start, twice in a row", () -> {
            StudentRegistry r = fresh();
            r.insert("Repeat One", "r1@ex.com", 1, 70.0);
            r.insert("Repeat Two", "r2@ex.com", 1, 80.0);
            check(r.count() == 2, "two inserts should leave two rows");
            check(r.searchByName("Repeat").size() == 2, "both repeats should be found");
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.out.println(failed == 0
                ? "The registry is on a database. Well done."
                : "Fix the FAILs above, then run SelfCheck again.");
        if (failed > 0) System.exit(1);
    }

    // ------------------------------------------------------------------------

    /** A clean students table for one check, and the two courses every test inserts against. */
    private static StudentRegistry fresh() throws Exception {
        try (Connection c = DriverManager.getConnection(URL, "sa", "");
             Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS students");
            st.execute("DROP TABLE IF EXISTS courses");
            st.execute("CREATE TABLE courses (id INT PRIMARY KEY, title VARCHAR(80), credits INT)");
            st.executeUpdate("INSERT INTO courses VALUES (1, 'Java Programming', 12), (2, 'Databases', 10)");
            st.execute("CREATE TABLE students ("
                     + " id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                     + " name VARCHAR(100) NOT NULL,"
                     + " email VARCHAR(150) UNIQUE,"
                     + " course_id INT,"
                     + " mark DECIMAL(5,2))");
        }
        StudentRegistry r = new StudentRegistry(URL);
        r.createSchema();   // a no-op here, but it must be safe to call
        return r;
    }

    private static void seedCourses() throws Exception {
        try (Connection c = DriverManager.getConnection(URL, "sa", "");
             Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS students");
            st.execute("DROP TABLE IF EXISTS courses");
            st.execute("CREATE TABLE courses (id INT PRIMARY KEY, title VARCHAR(80), credits INT)");
            st.executeUpdate("INSERT INTO courses VALUES (1, 'Java Programming', 12), (2, 'Databases', 10)");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void run(String name, Check check) {
        try {
            check.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (UnsupportedOperationException e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> not implemented yet (" + e.getMessage() + ")");
        } catch (AssertionError e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> crashed with " + e);
        }
    }
}
