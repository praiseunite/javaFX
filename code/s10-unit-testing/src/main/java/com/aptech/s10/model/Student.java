package com.aptech.s10.model;

import java.math.BigDecimal;

/**
 * One student. GIVEN — you do not change this file in Session 10; you test it.
 *
 * <p>A record, so the compiler writes the constructor, the accessors, {@code equals},
 * {@code hashCode} and {@code toString}. Two of those matter enormously to a test:
 *
 * <ul>
 *   <li>{@code equals} means a test can compare whole objects —
 *       {@code assertEquals(expected, actual)} — instead of picking the fields apart one at a
 *       time and hoping the interesting one is in the list.</li>
 *   <li>The <strong>compact constructor</strong> below is a guard: it runs before the object
 *       exists, so an invalid {@code Student} cannot be built at all. That is a promise a test
 *       can check with {@code assertThrows}.</li>
 * </ul>
 *
 * <p>An id of 0 means "no store has named me yet". {@link #unsaved} exists so that you never
 * invent an id in a test.
 */
public record Student(int id, String name, String email, Integer courseId, BigDecimal mark, boolean active) {

    /** Runs before anything else. No invalid Student can come into existence. */
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

    /** A student the store has not seen yet. Use this in tests, not the 6-argument constructor. */
    public static Student unsaved(String name, String email, Integer courseId, BigDecimal mark) {
        return new Student(0, name, email, courseId, mark, true);
    }

    /** The same student with a different mark. Records are immutable: this makes a copy. */
    public Student withMark(BigDecimal newMark) {
        return new Student(id, name, email, courseId, newMark, active);
    }

    /**
     * A, B, C, D or F. This is the method Example 1 tests, and it is worth a second look.
     *
     * <p>The boundaries are the interesting part: 80 is an A and 79.99 is not, 60 is a C and
     * 59.99 is not. A test that only checks 88 (an A) would pass even if every boundary were
     * wrong — which is the whole subject of Part 10.
     */
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
