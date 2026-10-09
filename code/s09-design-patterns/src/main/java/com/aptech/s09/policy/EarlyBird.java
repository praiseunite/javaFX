package com.aptech.s09.policy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 10% off for anyone who enrols before the term starts.
 *
 * <p>The date is a constant in the class, which is not where a real promotion would live — it
 * belongs in configuration, and Part 4 shows how {@code AppConfig} reads one. It is here so
 * the example prints the same numbers on every machine, and so the rule is stated in exactly
 * one place instead of inside an {@code if} in a service class.
 *
 * <p>Notice that the policy <em>reads</em> the date from the request rather than calling
 * {@code LocalDate.now()}. A policy that calls {@code now()} cannot be tested without waiting
 * until next term, and it means the receipt a student was given can never be reproduced.
 */
public final class EarlyBird implements FeePolicy {

    /** Enrol on or before this date and the discount applies. */
    public static final LocalDate PROMO_END = LocalDate.of(2026, 8, 31);

    /** Paying 90% of the fee is a 10% discount, expressed without a subtraction. */
    private static final String RATE = "0.90";

    @Override
    public BigDecimal feeFor(EnrolmentRequest request) {
        if (request.date().isAfter(PROMO_END)) {
            return Money.of(request.course().fee());
        }
        return Money.percent(request.course().fee(), RATE);
    }

    @Override
    public String name() {
        return "early bird (-10% on or before " + PROMO_END + ")";
    }
}
