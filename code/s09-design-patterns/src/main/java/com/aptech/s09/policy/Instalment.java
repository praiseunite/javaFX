package com.aptech.s09.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pay in instalments: the fee plus 5% administration, split into the number of parts the
 * request asks for.
 *
 * <p>{@link #feeFor} still answers the one question the interface asks — <em>what does this
 * enrolment cost in total</em> — so the receipt and the ledger do not need to know a payment
 * plan is involved. {@link #perInstalment} is an extra method that only makes sense for this
 * policy, and it is deliberately not on {@link FeePolicy}: a flat fee has nothing useful to
 * say about instalments, and putting it on the interface would force every policy to pretend
 * otherwise.
 *
 * <p>Real instalment plans do not divide evenly — 472.50 over 4 is 118.125. The examples use
 * amounts that divide exactly so the arithmetic is visible; Part 5's note says what a real
 * ledger does about the last payment.
 */
public final class Instalment implements FeePolicy {

    /** The administration charge: 105% of the fee is the fee plus 5%. */
    private static final String ADMIN_RATE = "1.05";

    @Override
    public BigDecimal feeFor(EnrolmentRequest request) {
        return Money.percent(request.course().fee(), ADMIN_RATE);
    }

    /** What one of the requested parts costs, to the cent. */
    public BigDecimal perInstalment(EnrolmentRequest request) {
        return feeFor(request).divide(BigDecimal.valueOf(request.instalments()),
                Money.SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public String name() {
        return "instalment (+5% admin)";
    }
}
