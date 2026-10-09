package com.aptech.s09.policy;

import java.math.BigDecimal;

/**
 * How much this enrolment costs. One method — which is what makes it a Strategy, and also
 * what makes it a {@link FunctionalInterface}: a policy with simple arithmetic can be written
 * as a lambda instead of a class.
 *
 * <p>The point of the pattern is the <em>caller</em>. Look at
 * {@code EnrolmentService.enrol()}: it calls {@code policy.feeFor(request)} and has no idea
 * whether that is a flat fee, a discount, or a payment plan. Adding a fourth policy next term
 * changes no caller, no test, and no line of the service.
 *
 * <p>{@link #name()} is a {@code default} method, so a lambda gets a value without writing
 * one. The value it gets is deliberately unhelpful: if a policy's name appears in a receipt or
 * a log, it deserves a real name, and that means a class.
 */
@FunctionalInterface
public interface FeePolicy {

    /** What this enrolment costs, in the currency the course fee is in. */
    BigDecimal feeFor(EnrolmentRequest request);

    /** A label for receipts and logs. A lambda inherits the default below. */
    default String name() {
        return "custom policy (unnamed)";
    }
}
