package com.aptech.s09.model;

import java.math.BigDecimal;

/**
 * One student on the registry.
 *
 * <p>Note what is <em>not</em> here: no JDBC, no SQL, no {@code Connection}, no
 * {@code ResultSet}, no {@code PreparedStatement}. This class would compile in a project
 * that had never heard of a database. That is the whole idea behind the Repository pattern
 * in Part 2 — the model describes the business, and something else worries about storage.
 *
 * <p>{@code mark} and {@code courseId} are the boxed types because both columns are
 * nullable in the database: a student can be registered before they are given a course or
 * a mark. A {@code null} mark means "not marked yet" and prints as a dash.
 */
public record Student(int id, String name, String email, Integer courseId,
                      BigDecimal mark, boolean active) {

    /** A mark of 100, as a BigDecimal, for the range check below. */
    private static final BigDecimal FULL_MARKS = new BigDecimal("100");

    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email looks wrong: " + email);
        }
        if (mark != null && (mark.signum() < 0 || mark.compareTo(FULL_MARKS) > 0)) {
            throw new IllegalArgumentException("mark must be between 0 and 100, got " + mark);
        }
    }

    /**
     * A student who has not been saved yet. Id 0 is the registry's way of saying
     * "the database has not named me" — {@code save()} inserts when it sees 0.
     */
    public static Student unsaved(String name, String email, Integer courseId, BigDecimal mark) {
        return new Student(0, name, email, courseId, mark, true);
    }

    /**
     * A copy with a different mark. Records are immutable, so "changing" one means making
     * a new one — which is why the generated {@code equals} can be trusted everywhere.
     */
    public Student withMark(BigDecimal newMark) {
        return new Student(id, name, email, courseId, newMark, active);
    }

    /** The letter grade for the mark, or "-" when the student has not been marked. */
    public String grade() {
        if (mark == null) return "-";
        double m = mark.doubleValue();
        if (m >= 90) return "A";
        if (m >= 80) return "B";
        if (m >= 70) return "C";
        if (m >= 60) return "D";
        return "F";
    }
}
