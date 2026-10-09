package com.aptech.a5.policy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * R5 — 10% off for enrolling before term starts.   <<< WRITE THIS CLASS >>>
 *
 * <p>The rule: if {@code request.date()} is <b>on or before</b> 31 August 2026, the fee is 90% of
 * the course fee; otherwise it is the full fee. Use {@link Money#percent} with a rate of
 * {@code "0.90"} — paying 90% is the same as taking 10% off, and the rate reads better than
 * {@code multiply(new BigDecimal("0.9"))} in three places.
 *
 * <p>Test the date with {@code !date.isAfter(PROMO_END)}, which means "on or before" and cannot be
 * got wrong the way {@code date.isBefore(END)} can — that one silently excludes the last day, and
 * the boundary is the only interesting case a fee rule has.
 *
 * <p>{@link #name()} should say what the rule does, not what it is called, because the report is
 * the only explanation a student sees:
 * {@code "early bird (-10% on or before 2026-08-31)"}.
 */
public final class EarlyBird implements FeePolicy {

    /** The last day the discount applies, inclusive. A named constant, never a literal in a test. */
    public static final LocalDate PROMO_END = LocalDate.of(2026, 8, 31);

    /** 90% of the fee is 10% off it. */
    private static final String RATE = "0.90";

    @Override
    public BigDecimal feeFor(EnrolmentRequest request) {
        // TODO R5
        throw new UnsupportedOperationException("R5: EarlyBird.feeFor() not implemented");
    }

    @Override
    public String name() {
        // TODO R5
        return "custom policy (unnamed)";
    }
}
