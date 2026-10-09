package com.aptech.s09.policy;

import java.math.BigDecimal;

/**
 * The list price: the course fee, and nothing else.
 *
 * <p>It is worth writing even though it is one line, because it is the policy the others are
 * exceptions to. When someone asks "what should this student have paid?", the answer has to
 * be a policy someone chose, not a number that appears in three places.
 */
public final class FlatFee implements FeePolicy {

    @Override
    public BigDecimal feeFor(EnrolmentRequest request) {
        return Money.of(request.course().fee());
    }

    @Override
    public String name() {
        return "flat fee";
    }
}
