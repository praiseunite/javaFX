package com.aptech.s09;

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
 * Example 5 — three fee rules, one call, and no {@code if}.
 *
 * <p>The method that matters is not in this file. It is {@code FeePolicy.feeFor}, and the
 * point of the pattern is that the caller of that method cannot tell which rule it got. So
 * read the output as a table of <em>answers</em>, and notice that the code producing them
 * never branches.
 */
public final class E05_Strategy {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        Course java = Course.of(1, "Java Programming", 12, "450.00");
        Course engineering = Course.of(4, "Software Engineering", 14, "520.00");
        Student ada = new H2StudentRepository().findById(1).orElseThrow();

        LocalDate early = LocalDate.of(2026, 8, 1);
        LocalDate late = LocalDate.of(2026, 9, 30);

        List<FeePolicy> policies = List.of(new FlatFee(), new EarlyBird(), new Instalment());

        System.out.println(Db.rule("Three rules, one method, two dates"));
        System.out.printf("  %-41s %10s %10s%n", "policy", early, late);
        System.out.println("  " + "-".repeat(63));
        for (FeePolicy policy : policies) {
            BigDecimal onTime = policy.feeFor(EnrolmentRequest.of(ada, java, early));
            BigDecimal tooLate = policy.feeFor(EnrolmentRequest.of(ada, java, late));
            System.out.printf("  %-41s %10s %10s%n", policy.name(), onTime, tooLate);
        }
        System.out.println();
        System.out.println("  Every row came from the same line of code: policy.feeFor(request).");
        System.out.println("  The only thing that changed between rows was which object it was.");

        System.out.println(Db.rule("One request, priced in turn, split into instalments"));
        EnrolmentRequest plan = new EnrolmentRequest(ada, java, early, 3);
        for (FeePolicy policy : policies) {
            System.out.println("  " + policy.name());
            System.out.println("    total : " + policy.feeFor(plan));
        }
        Instalment instalment = new Instalment();
        System.out.println("    per instalment : " + instalment.perInstalment(plan)
                + "   (3 x 157.50 = 472.50)");

        System.out.println(Db.rule("A policy with no class at all"));
        FeePolicy scholarship = request -> Money.percent(request.course().fee(), "0.50");
        System.out.println("  scholarship 50% on " + engineering.title() + " : "
                + scholarship.feeFor(EnrolmentRequest.of(ada, engineering, early)));
        System.out.println("  its name()    : " + scholarship.name());
        System.out.println();
        System.out.println("  A lambda is enough for arithmetic, and inherits the default name.");
        System.out.println("  The moment a policy's name goes on a receipt, give it a class of its own.");

        System.out.println(Db.rule("Money is not a double"));
        System.out.println("  450.00 x 0.90 as a double : " + (450.00 * 0.9));
        System.out.println("  450.00 x 0.90 as Money    : " + Money.percent(new BigDecimal("450.00"), "0.90"));
        System.out.println();
        System.out.println("  Nine thousand students later, that is a ledger that balances.");
    }
}
