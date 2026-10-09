package com.aptech.a5.model;

import java.math.BigDecimal;

/**
 * One student, as the rest of the program sees one. GIVEN — do not change it.
 *
 * <p>A record, because a student is a fixed set of values: the compiler writes the constructor,
 * the accessors, {@code equals}, {@code hashCode} and {@code toString} for you. That
 * {@code equals} is what makes the export round-trip in R8 checkable with {@code list.equals(back)}
 * — two students with the same fields ARE the same student, no matter who built them.
 *
 * <p>An id of 0 means "the database has not named me yet". That is why {@link #unsaved} exists.
 */
public record Student(int id, String name, String email, Integer courseId, BigDecimal mark, boolean active) {

    /** The compact constructor runs before anything else, so no invalid Student can exist. */
    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("a student needs a name");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email must look like an email: " + email);
        }
        if (mark != null && (mark.compareTo(BigDecimal.ZERO) < 0 || mark.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("a mark is between 0 and 100, got " + mark);
        }
    }

    /** R8 compares lists of these, so toString is worth having in a readable shape. */
    @Override
    public String toString() {
        return String.format("%-4d %-19s %-24s %-6s %s",
                id, name, email, grade(), active ? "active" : "inactive");
    }

    /** A student the database has not seen yet. */
    public static Student unsaved(String name, String email, Integer courseId, BigDecimal mark) {
        return new Student(0, name, email, courseId, mark, true);
    }

    /** The same student with a different mark. Records are immutable: this makes a copy. */
    public Student withMark(BigDecimal newMark) {
        return new Student(id, name, email, courseId, newMark, active);
    }

    /** A, B, C, D or F. A null mark is "-", not "F" — not marked is not the same as failed. */
    public String grade() {
        if (mark == null) return "-";
        double m = mark.doubleValue();
        if (m >= 80) return "A";
        if (m >= 70) return "B";
        if (m >= 60) return "C";
        if (m >= 50) return "D";
        return "F";
    }
}
