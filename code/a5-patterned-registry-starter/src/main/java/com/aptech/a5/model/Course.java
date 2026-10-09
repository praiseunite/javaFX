package com.aptech.a5.model;

import java.math.BigDecimal;

/**
 * One course in the catalogue. GIVEN — do not change it.
 *
 * <p>Money is a {@link BigDecimal} and never a {@code double}: a fee is a value customers are
 * charged, and {@code 0.1 + 0.2} is not {@code 0.3} in binary floating point. The
 * {@code BigDecimal} constructor is called with a <em>String</em> for the same reason.
 */
public record Course(int id, String title, int credits, BigDecimal fee) {

    public Course {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("a course needs a title");
        if (credits <= 0) throw new IllegalArgumentException("credits must be positive, got " + credits);
        if (fee == null || fee.signum() < 0) throw new IllegalArgumentException("a fee cannot be negative");
    }

    /**
     * A static factory. Shorter than the constructor at the call site, and it is the one place
     * that decides how a fee String becomes a BigDecimal — so "450.00" cannot be mistyped into
     * a different scale anywhere else.
     */
    public static Course of(int id, String title, int credits, String fee) {
        return new Course(id, title, credits, new BigDecimal(fee));
    }

    public String label() {
        return title + " (" + credits + " credits)";
    }
}
