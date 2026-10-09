package com.aptech.s09;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Example 1 — the code that works, and cannot change.
 *
 * <p>This program is not a straw man. It is the shape most working code actually has: one
 * class that knows the fee rule, the SQL, and the wording of the message, because that was
 * the fastest way to make it work the first time.
 *
 * <p>It runs. It prints the right answer. Nothing in it is a bug. The next three sections of
 * the page are about the three requests a real registry gets within a year, and what each one
 * costs here.
 */
public final class E01_WhyPatterns {

    public static void main(String[] args) throws SQLException {
        Db.resetRegistry();

        HardWiredDesk desk = new HardWiredDesk();
        DeskReceipt receipt = desk.enrol("Nia Okoro", "nia@example.com", 1, LocalDate.of(2026, 8, 1));

        System.out.println("enrolled  : " + receipt.name() + " on course " + receipt.courseId()
                + ", fee " + receipt.fee());
        System.out.println("students  : " + desk.countStudents());
        System.out.println("the email : " + desk.lastEmail());

        System.out.println(Db.rule("What this code costs when the requirements move"));

        System.out.println("""

                  request 1: "we are moving to MySQL"
                    files to edit   E01_WhyPatterns.java, and every other class that says jdbc
                    places to find the rule   one per method: each opens its own connection

                  request 2: "instalment payers pay 5% more"
                    files to edit   the one method that works out a fee today, and every copy
                                    of the rule that will appear the moment there are two
                    places to find the rule   the fee is not a thing you can call, so you
                                    cannot test it without running a whole enrolment

                  request 3: "show the screen in French"
                    files to edit   every string literal in the program
                    places to find the rule   text is produced, not looked up, so there is
                                    nothing a translator can be given to edit
                """.stripTrailing());

        System.out.println("""
                None of this is a bug. The program works.
                That is exactly the problem: nothing in it is wrong enough to make you fix it,
                which is why it is still here when the third request arrives.
                """.stripTrailing());

        System.out.println(Db.rule("The same three requests, answered in Parts 2 to 8"));
        System.out.println("  request 1  ->  Part 2: a StudentRepository, and Part 3: a factory");
        System.out.println("  request 2  ->  Part 5: a FeePolicy, chosen at run time");
        System.out.println("  request 3  ->  Part 8: a ResourceBundle, read by locale");
    }

    /** What the desk hands back. A record, so the caller gets fields rather than a string. */
    record DeskReceipt(String name, int courseId, String fee) {
    }

    /**
     * The whole registry, in the shape it takes when nobody has asked a question about it.
     *
     * <p>Read the {@code enrol} method and count the decisions in it: how much the fee is,
     * whether the discount applies, how a student is stored, and what the email says. Four
     * decisions, in one method, none of them named. Every part of Session 9 takes one of them
     * out and gives it a home.
     */
    static final class HardWiredDesk {

        /** Course 1 costs this much. Ask anyone. */
        private static final BigDecimal COURSE_ONE_FEE = new BigDecimal("450.00");

        private String lastEmail = "(none)";

        /**
         * Enrols a student: works out the fee, inserts the row, and writes the message — all
         * here, because that was the quickest way to make it work.
         */
        DeskReceipt enrol(String name, String email, int courseId, LocalDate date) throws SQLException {
            // decision 1 and 2: the fee, and when the discount applies.
            BigDecimal fee = COURSE_ONE_FEE;
            if (!date.isAfter(LocalDate.of(2026, 8, 31))) {
                fee = fee.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
            }

            // decision 3: how a student is stored.
            try (Connection c = Db.open();
                 PreparedStatement ps = c.prepareStatement(
                         "INSERT INTO students (name, email, course_id, active) VALUES (?, ?, ?, TRUE)",
                         Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setInt(3, courseId);
                ps.executeUpdate();
            }

            // decision 4: what the student is told, in one language.
            lastEmail = "Welcome to the Student Registry, " + name + ". Your fee is " + fee + ".";
            return new DeskReceipt(name, courseId, fee.toPlainString());
        }

        int countStudents() throws SQLException {
            try (Connection c = Db.open();
                 Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                rs.next();
                return rs.getInt(1);
            }
        }

        String lastEmail() {
            return lastEmail;
        }
    }
}
