package com.aptech.s09.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * What the registry hands back after an enrolment: who, on what, owing how much, worked out
 * by which policy — and how many listeners were told about it.
 *
 * <p>It is a model object, not a printout. It carries no {@code System.out.println}, so the
 * same receipt can be shown on a console, in a JavaFX window, or turned into JSON for a web
 * service. Turning it into text is the view's job (Part 7).
 */
public record EnrolmentReceipt(Student student, Course course, BigDecimal fee,
                               String policyName, int instalments, int notified) {

    /** What each instalment costs. The last one is not adjusted — see Part 5's note. */
    public BigDecimal perInstalment() {
        if (instalments < 1) return fee;
        return fee.divide(BigDecimal.valueOf(instalments), 2, RoundingMode.HALF_UP);
    }

    /** True when the fee was split into more than one payment. */
    public boolean isSplit() {
        return instalments > 1;
    }
}
