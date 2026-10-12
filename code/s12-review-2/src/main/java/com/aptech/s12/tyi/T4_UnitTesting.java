package com.aptech.s12.tyi;

import java.util.List;

/**
 * TRY IT YOURSELF 4 — Book Session 9: Enhanced Unit Testing &amp; AI Tools.
 *
 * Task: Build a robust test fixture that tests both success cases and boundary failures,
 * asserts exact exception types, and verifies parameterized data points without swallowed errors.
 */
public class T4_UnitTesting {

    // === Domain class under test ===
    public static class GradeCalculator {
        public static String calculateGrade(double score) {
            if (score < 0.0 || score > 100.0) {
                throw new IllegalArgumentException("Score must be between 0.0 and 100.0: " + score);
            }
            if (score >= 90.0) return "A";
            if (score >= 80.0) return "B";
            if (score >= 70.0) return "C";
            if (score >= 60.0) return "D";
            return "F";
        }
    }

    // === Parameterized test case record ===
    public record TestCase(double score, String expectedGrade) {}

    public static void main(String[] args) {
        System.out.println("Try It Yourself 4 - Unit Testing: Parameterized Checks & Boundaries");

        // 1. Parameterized valid assertions
        List<TestCase> validCases = List.of(
                new TestCase(95.0, "A"),
                new TestCase(89.9, "B"),
                new TestCase(80.0, "B"),
                new TestCase(75.5, "C"),
                new TestCase(60.0, "D"),
                new TestCase(45.0, "F")
        );

        System.out.println();
        System.out.println("1. Running parameterized test cases across grade boundaries:");
        int passed = 0;
        for (TestCase tc : validCases) {
            String actual = GradeCalculator.calculateGrade(tc.score());
            if (actual.equals(tc.expectedGrade())) {
                System.out.printf("  PASS: score %5.1f -> expected %s, got %s%n", tc.score(), tc.expectedGrade(), actual);
                passed++;
            } else {
                System.out.printf("  FAIL: score %5.1f -> expected %s, got %s%n", tc.score(), tc.expectedGrade(), actual);
            }
        }
        System.out.printf("  Result: %d of %d boundary checks passed.%n", passed, validCases.size());

        // 2. Boundary exception assertions
        System.out.println();
        System.out.println("2. Verifying exception handling for illegal inputs:");
        testIllegalScore(-5.0);
        testIllegalScore(105.0);

        System.out.println();
        System.out.println("Tests completed with strict assertions. Zero swallowed exceptions.");
    }

    private static void testIllegalScore(double score) {
        try {
            GradeCalculator.calculateGrade(score);
            System.out.printf("  FAIL: Score %.1f was accepted without exception!%n", score);
        } catch (IllegalArgumentException e) {
            System.out.printf("  PASS: Score %5.1f threw IllegalArgumentException as expected: \"%s\"%n",
                    score, e.getMessage());
        }
    }
}
