package com.aptech.s12.drills;

/**
 * DRILL 6 — Book Session 9: Unit Testing &amp; AI Tools.
 *
 * An AI tool frequently generates tests that "pass" by catching an exception and doing nothing,
 * or asserting trivial truths (e.g. assertTrue(true)).
 * A test that cannot fail is not a test — it provides false security.
 */
public class D6_TheTestThatCannotFail {

    public static class Validator {
        public static void requireValidId(int id) {
            if (id <= 0) {
                throw new IllegalArgumentException("Student ID must be positive: " + id);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Drill 6 - Unit testing: the test that cannot fail vs true failure verification");
        System.out.println();

        System.out.println("AI-GENERATED FLAWED TEST - catches the exception and prints a message:");
        boolean brokenResult = testBroken();
        System.out.println("  test passed green? " + brokenResult);
        System.out.println("  -> If requireValidId(-1) silently stopped throwing, this test STILL passes!");
        System.out.println();

        System.out.println("FIXED TEST - asserts the exact exception was thrown, failing if it wasn't:");
        boolean fixedResult = testFixed();
        System.out.println("  test passed green? " + fixedResult);
        System.out.println("  -> Genuine regression test: catches logic errors and boundary bugs.");
    }

    private static boolean testBroken() {
        try {
            Validator.requireValidId(-1);
        } catch (Exception e) {
            System.out.println("  [BrokenTest] Caught exception as expected: " + e.getMessage());
        }
        // Always returns true, even if no exception was thrown!
        return true;
    }

    private static boolean testFixed() {
        boolean thrown = false;
        try {
            Validator.requireValidId(-1);
        } catch (IllegalArgumentException e) {
            thrown = true; // Expected
        }

        if (!thrown) {
            System.out.println("  [FixedTest] FAIL: Expected IllegalArgumentException was NOT thrown!");
            return false;
        }
        System.out.println("  [FixedTest] PASS: Confirmed IllegalArgumentException was thrown on negative ID.");
        return true;
    }
}
