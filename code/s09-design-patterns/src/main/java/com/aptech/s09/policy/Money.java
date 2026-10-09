package com.aptech.s09.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Every amount the registry prints goes through here first, so money is always two decimal
 * places and always rounded the same way.
 *
 * <p>This class is why the fee policies in Part 5 do arithmetic in {@link BigDecimal} rather
 * than {@code double}. Try {@code 450.00 * 0.9} in a {@code double} and you get
 * {@code 405.00000000000006}, because 0.9 cannot be written exactly in binary. Multiply that
 * by a few thousand students and the ledger is out by a cent — a cent that somebody has to
 * find. {@code BigDecimal} is slower and more awkward, and it is the right type for money.
 *
 * <p>It is also a reminder that a "utility class" needs a {@code private} constructor. Without
 * one, {@code new Money()} compiles, means nothing, and eventually appears in someone's code.
 */
public final class Money {

    /** The scale every amount is stored and printed at. */
    public static final int SCALE = 2;

    private Money() {
    }

    /** Two decimal places, half-up — the rounding a till uses. */
    public static BigDecimal of(BigDecimal amount) {
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /** The same, from a string such as {@code "450.00"}. */
    public static BigDecimal of(String amount) {
        return of(new BigDecimal(amount));
    }

    /**
     * A percentage of an amount, e.g. {@code percent(fee, "0.90")} for 90% of it.
     * The rate is a string on purpose: a decimal built from a {@code double} is already
     * slightly wrong before it arrives.
     */
    public static BigDecimal percent(BigDecimal amount, String rate) {
        return of(amount.multiply(new BigDecimal(rate)));
    }
}
