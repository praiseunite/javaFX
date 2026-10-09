package com.aptech.a5.policy;

import java.math.BigDecimal;

/**
 * How much an enrolment costs. GIVEN — do not change it.
 *
 * <p>{@code @FunctionalInterface} means exactly one abstract method, which is what lets the
 * caller write {@code policy.feeFor(request)} and never ask which rule it was handed. R5 is the
 * two implementations of this.
 *
 * <p>The default {@link #name()} is here for the report. A lambda cannot override a default
 * method, so a policy written as a lambda reports "custom policy (unnamed)" — which is honest,
 * and is why the two you write for R5 are classes.
 */
@FunctionalInterface
public interface FeePolicy {

    BigDecimal feeFor(EnrolmentRequest request);

    default String name() {
        return "custom policy (unnamed)";
    }
}
