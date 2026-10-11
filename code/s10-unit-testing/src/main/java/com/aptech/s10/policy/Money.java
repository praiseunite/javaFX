package com.aptech.s10.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money arithmetic, in one place. GIVEN — used by Example 2 to make a point about assertions.
 *
 * <p>A fee is a value a customer is charged, so it is a {@link BigDecimal} and never a
 * {@code double}. In a test that matters in a way it does not elsewhere: two BigDecimals can be
 * <em>numerically equal and not {@code equals}</em>, because {@code equals} compares the scale
 * too. {@code new BigDecimal("450.0")} is not {@code equals} to {@code new BigDecimal("450.00")},
 * although {@code compareTo} says they are the same number.
 *
 * <p>So Example 2 uses {@code assertEquals(0, a.compareTo(b))} for money and plain
 * {@code assertEquals} for everything else — and says why, because "sometimes equals,
 * sometimes compareTo" is exactly the kind of rule that gets copied without being understood.
 */
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
