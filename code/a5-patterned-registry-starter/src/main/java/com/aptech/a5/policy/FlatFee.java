package com.aptech.a5.policy;

import java.math.BigDecimal;

/**
 * R5 — the fee with nothing taken off.   <<< WRITE THIS CLASS >>>
 *
 * <p>The simplest possible rule, and it still has to be a class rather than a lambda, because the
 * report prints {@link #name()}. The course fee, rounded to 2 decimal places by
 * {@link Money#of}.
 *
 * <p>Ask for the fee as {@code request.course().fee()} — a rule that reaches back to the database
 * for the course is a rule that cannot be tested without one.
 */
public final class FlatFee implements FeePolicy {

    @Override
    public BigDecimal feeFor(EnrolmentRequest request) {
        // TODO R5
        throw new UnsupportedOperationException("R5: FlatFee.feeFor() not implemented");
    }

    @Override
    public String name() {
        // TODO R5 — "flat fee" is what the report shows.
        return "custom policy (unnamed)";
    }
}
