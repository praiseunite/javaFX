package com.aptech.s10.model;

import java.math.BigDecimal;

/**
 * One course. GIVEN — you test it, you do not change it.
 *
 * <p>The fee is a {@link BigDecimal} built from a <em>String</em>, for the reason Session 9
 * gave: a fee is money, and {@code 0.1 + 0.2} is not {@code 0.3} in binary floating point. That
 * is also why Example 2 asserts on money with {@code compareTo} rather than {@code equals}.
 */
public record Course(int id, String title, int credits, BigDecimal fee) {

    public Course {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("a course needs a title");
        if (credits <= 0) throw new IllegalArgumentException("credits must be positive, got " + credits);
        if (fee == null || fee.signum() < 0) throw new IllegalArgumentException("a fee cannot be negative");
    }

    /** A static factory, so "450.00" is turned into a BigDecimal in exactly one place. */
    public static Course of(int id, String title, int credits, String fee) {
        return new Course(id, title, credits, new BigDecimal(fee));
    }

    public String label() {
        return title + " (" + credits + " credits)";
    }
}
