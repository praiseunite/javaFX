package com.aptech.s12lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Stack;

/**
 * SelfCheck for the Session 12 Guided Lab: Enterprise Repair Clinic.
 *
 * Measures all five faults (F1–F5) in EnterpriseClinic.java.
 * Output at the start:  0 passed, 5 failed.
 * Target after repairs: 5 passed, 0 failed.
 */
public class SelfCheck {

    public static void main(String[] args) {
        System.out.println("=== Enterprise Repair Clinic Self-Check ===");
        System.out.println();

        int passed = 0;
        int failed = 0;

        // --- F1: SQL Injection Check ---
        try {
            EnterpriseClinic.initDatabase();
            List<String> leaked = EnterpriseClinic.searchStudent("' OR '1'='1");
            if (leaked.isEmpty()) {
                System.out.println("PASS  F1  Session 6 - PreparedStatement blocks SQL injection");
                passed++;
            } else {
                System.out.println("FAIL  F1  Session 6 - PreparedStatement blocks SQL injection");
                System.out.println("      -> expected 0 students for SQL injection payload, got " + leaked.size() + " -> " + leaked);
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL  F1  Session 6 - crashed with: " + e.getMessage());
            failed++;
        }

        // --- F2: Transaction Atomicity Check ---
        try {
            EnterpriseClinic.initDatabase();
            // Attempt enrolment with illegal fee (-50.0). The fee write WILL fail.
            boolean outcome = EnterpriseClinic.enrolWithFee(999, "Advanced Java", -50.0);
            int orphanedCount = countEnrolments(999);

            if (!outcome && orphanedCount == 0) {
                System.out.println("PASS  F2  Session 7 - Transaction rollback preserves atomicity");
                passed++;
            } else {
                System.out.println("FAIL  F2  Session 7 - Transaction rollback preserves atomicity");
                System.out.println("      -> expected 0 orphaned enrolments on failure, found " + orphanedCount
                        + " - is setAutoCommit(false) and rollback() being used?");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL  F2  Session 7 - crashed with: " + e.getMessage());
            failed++;
        }

        // --- F3: Design Patterns & I18N Fallback Check ---
        try {
            String frenchStatus = EnterpriseClinic.getLocalizedStatus("status.withdrawn", "fr");
            if ("Withdrawn".equals(frenchStatus)) {
                System.out.println("PASS  F3  Session 8 - I18N key lookup falls back to base English");
                passed++;
            } else {
                System.out.println("FAIL  F3  Session 8 - I18N key lookup falls back to base English");
                System.out.println("      -> expected 'Withdrawn' (fallback from English), got: " + frenchStatus);
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL  F3  Session 8 - crashed with: " + e.getMessage());
            failed++;
        }

        // --- F4: Testing & Boundary Check ---
        try {
            boolean partTimeEligible = EnterpriseClinic.validateScholarshipEligibility(3.8, 6); // High GPA, but only 6 credits (part-time)
            boolean lowGpaEligible = EnterpriseClinic.validateScholarshipEligibility(3.2, 15);   // Full-time, but only 3.2 GPA

            if (!partTimeEligible && !lowGpaEligible) {
                System.out.println("PASS  F4  Session 9 - Scholarship boundary requires GPA >= 3.5 AND credits >= 12");
                passed++;
            } else {
                System.out.println("FAIL  F4  Session 9 - Scholarship boundary requires GPA >= 3.5 AND credits >= 12");
                System.out.println("      -> expected false for part-time (6 credits) and low GPA (3.2), but at least one returned true (did you use || instead of &&?)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL  F4  Session 9 - crashed with: " + e.getMessage());
            failed++;
        }

        // --- F5: Data Structures 1-based indexing Check ---
        try {
            Stack<String> history = new Stack<>();
            history.push("Home");
            history.push("Courses");
            history.push("Profile"); // Top of stack

            boolean isTop = EnterpriseClinic.isTopNavigationPage(history, "Profile");
            if (isTop) {
                System.out.println("PASS  F5  Session 10 - Stack.search() correctly handles 1-based top offset");
                passed++;
            } else {
                System.out.println("FAIL  F5  Session 10 - Stack.search() correctly handles 1-based top offset");
                System.out.println("      -> expected true for top page 'Profile' - did you check for 0 instead of 1?");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL  F5  Session 10 - crashed with: " + e.getMessage());
            failed++;
        }

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed.");
        if (failed == 0) {
            System.out.println("Every fault repaired - the enterprise clinic is closed.");
        } else {
            System.out.println("Repair the FAILs above one at a time, then run SelfCheck again.");
        }
    }

    private static int countEnrolments(int studentId) throws Exception {
        try (Connection conn = DriverManager.getConnection(EnterpriseClinic.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM clinic_enrolments WHERE student_id = " + studentId)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
