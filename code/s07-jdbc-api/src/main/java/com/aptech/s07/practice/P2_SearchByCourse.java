package com.aptech.s07.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PRACTICE 2 (Medium) — a real query: two tables, a parameter, and a task worth doing.
 *
 * <p>Write a method that answers "who is on this course, and how well are they doing?" The
 * data is split across two tables, so the query needs a JOIN — and the course name is a
 * parameter, so it needs a <code>?</code>.
 *
 * <p>The point of the exercise is not the JOIN. It is that the CALLER never sees a ResultSet.
 * A <code>ResultSet</code> is a live cursor into an open database connection; passing one out
 * of a method is how connections get forgotten. The DAO converts rows to objects and closes
 * everything before it returns.
 *
 * <p><strong>Expected output:</strong> the three Java Programming students, ordered by mark.
 */
public class P2_SearchByCourse {

    /** The caller's view of a student on a course. No JDBC types appear here. */
    record Enrolment(String student, String course, double mark, int credits) {
        @Override public String toString() {
            return String.format("%-20s %-20s %6.2f  (%d credits)", student, course, mark, credits);
        }
    }

    public static void main(String[] args) throws SQLException {

        System.out.println("P2 - a two-table query behind a parameterized method");

        String url = "jdbc:h2:mem:p2;DB_CLOSE_DELAY=-1";

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {
            setup(c);
            CourseDao dao = new CourseDao(c);

            System.out.println("\n  students on 'Java Programming':");
            for (Enrolment e : dao.findByCourse("Java Programming")) System.out.println("    " + e);

            System.out.println("\n  students on 'Database Fundamentals':");
            for (Enrolment e : dao.findByCourse("Database Fundamentals")) System.out.println("    " + e);

            System.out.println("\n  a course name that is not there:");
            System.out.println("    " + dao.findByCourse("Astrophysics").size() + " rows");

            System.out.println("\n  the same JOB, and a name that looks like SQL:");
            System.out.println("    " + dao.findByCourse("' OR '1'='1").size() + " rows"
                    + "   <- the ? made it a name, not a condition");
        }

        System.out.println("\nThe caller asked for objects. It never saw a ResultSet or a Connection.");
    }

    /** Two tables and a few rows, so the JOIN has something to join. */
    private static void setup(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("CREATE TABLE courses (id INT PRIMARY KEY, title VARCHAR(80), credits INT)");
            st.execute("CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(100), course_id INT, mark DECIMAL(5,2))");

            st.executeUpdate("INSERT INTO courses VALUES "
                    + "(1, 'Java Programming', 12), (2, 'Database Fundamentals', 10)");
            st.executeUpdate("INSERT INTO students VALUES "
                    + "(1, 'Ada Lovelace', 1, 88.50),"
                    + "(2, 'Grace Hopper', 1, 91.00),"
                    + "(3, 'Alan Turing',  1, 76.25),"
                    + "(4, 'Katherine Johnson', 2, 95.75),"
                    + "(5, 'Margaret Hamilton', 2, 89.00)");
        }
    }

    /** The DAO: it owns the JOIN, and it hands back objects. */
    static class CourseDao {
        private final Connection c;

        CourseDao(Connection c) { this.c = c; }

        List<Enrolment> findByCourse(String courseTitle) throws SQLException {
            String sql = "SELECT s.name, c.title, s.mark, c.credits "
                       + "FROM students s JOIN courses c ON c.id = s.course_id "
                       + "WHERE c.title = ? "
                       + "ORDER BY s.mark DESC";

            List<Enrolment> out = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, courseTitle);          // one ?, one value, no concatenation
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new Enrolment(rs.getString("name"), rs.getString("title"),
                                              rs.getDouble("mark"), rs.getInt("credits")));
                    }
                }
            }
            return out;
        }
    }
}
