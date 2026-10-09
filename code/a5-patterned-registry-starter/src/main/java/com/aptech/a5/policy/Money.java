package com.aptech.a5.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Money arithmetic, in one place. GIVEN — do not change it. */
public final class Money {

    /** Fees have two decimal places, and there is no third one to argue about. */
    public static final int SCALE = 2;

    private Money() {
    }

    /** Rounds to 2 decimal places, half up — the rounding a customer expects on an invoice. */
    public static BigDecimal of(BigDecimal amount) {
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal of(String amount) {
        return of(new BigDecimal(amount));
    }

    /** A percentage of an amount. "0.90" is 90% of it, i.e. 10% off. */
    public static BigDecimal percent(BigDecimal amount, String rate) {
        return of(amount.multiply(new BigDecimal(rate)));
    }
}
