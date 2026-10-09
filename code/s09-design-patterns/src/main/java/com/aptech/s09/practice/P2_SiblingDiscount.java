package com.aptech.s09.practice;

import com.aptech.s09.Db;
import com.aptech.s09.model.Course;
import com.aptech.s09.model.Student;
import com.aptech.s09.policy.EarlyBird;
import com.aptech.s09.policy.EnrolmentRequest;
import com.aptech.s09.policy.FeePolicy;
import com.aptech.s09.policy.FlatFee;
import com.aptech.s09.policy.Instalment;
import com.aptech.s09.policy.Money;
import com.aptech.s09.repository.H2StudentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Practice 2 — a fourth fee rule, added without editing the first three.
 *
 * <p>Adding a policy is the smallest complete exercise in the Strategy pattern, and it makes
 * the Open/Closed principle checkable: if you had to edit {@code FlatFee}, {@code EarlyBird}
 * or {@code Instalment} to make this work, the design was not what it claimed to be.
 */
public final class P2_SiblingDiscount {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        Course java = Course.of(1, "Java Programming", 12, "450.00");
        Student ada = new H2StudentRepository().findById(1).orElseThrow();
        LocalDate date = LocalDate.of(2026, 8, 1);

        List<FeePolicy> policies = List.of(
                new FlatFee(), new EarlyBird(), new Instalment(), new SiblingDiscount());

        System.out.println(Db.rule("Four rules, one call, still no if"));
        for (FeePolicy policy : policies) {
            System.out.printf("  %-41s %9s%n", policy.name(),
                    policy.feeFor(EnrolmentRequest.of(ada, java, date)));
        }

        System.out.println(Db.rule("What a caller has to change"));
        System.out.println("  Nothing. The line that prices an enrolment is still");
        System.out.println("      policy.feeFor(request)");
        System.out.println("  and it still does not care which policy it was handed.");

        System.out.println(Db.rule("What this exercise proves"));
        System.out.println("  FlatFee, EarlyBird and Instalment were not opened to add a fourth rule.");
        System.out.println("  That is the Open/Closed principle: open to extension, closed to edit.");
        System.out.println("  It is a property you can check, not a slogan - and this is the check.");
    }

    /**
     * 15% off when a brother or sister is already on the roll.
     *
     * <p>A real version would need the family relationship, which is a change to
     * {@code EnrolmentRequest} — one new component, and every policy that does not care about
     * it keeps compiling untouched. That is the other half of why the request is an object.
     */
    static final class SiblingDiscount implements FeePolicy {

        /** Paying 85% of the fee is a 15% discount. */
        private static final String RATE = "0.85";

        @Override
        public BigDecimal feeFor(EnrolmentRequest request) {
            return Money.percent(request.course().fee(), RATE);
        }

        @Override
        public String name() {
            return "sibling (-15%)";
        }
    }
}
