package com.aptech.s12lab;

import java.sql.SQLException;
import java.util.List;
import java.util.Stack;

/**
 * Main application exercising the Enterprise Clinic services.
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        System.out.println("=== Enterprise Academic Clinic Demo ===");
        System.out.println();

        EnterpriseClinic.initDatabase();

        // 1. Search student
        System.out.println("1. Searching student by name: 'Ada Lovelace'");
        List<String> found = EnterpriseClinic.searchStudent("Ada Lovelace");
        System.out.println("   Found: " + found);

        // 2. Enrol with fee
        System.out.println();
        System.out.println("2. Enrolling student #101 in 'Software Engineering' with fee $250.00");
        boolean enrolled = EnterpriseClinic.enrolWithFee(101, "Software Engineering", 250.0);
        System.out.println("   Enrolment status: " + (enrolled ? "SUCCESS" : "FAILED"));

        // 3. Localized status
        System.out.println();
        System.out.println("3. Checking localized enrollment status:");
        System.out.println("   English 'status.enrolled'  : " + EnterpriseClinic.getLocalizedStatus("status.enrolled", "en"));
        System.out.println("   French  'status.enrolled'  : " + EnterpriseClinic.getLocalizedStatus("status.enrolled", "fr"));
        System.out.println("   French  'status.withdrawn' : " + EnterpriseClinic.getLocalizedStatus("status.withdrawn", "fr"));

        // 4. Scholarship validation
        System.out.println();
        System.out.println("4. Checking scholarship eligibility:");
        System.out.println("   GPA 3.8, 15 credits (Honors)    : " + EnterpriseClinic.validateScholarshipEligibility(3.8, 15));
        System.out.println("   GPA 3.2,  6 credits (Part-time) : " + EnterpriseClinic.validateScholarshipEligibility(3.2, 6));

        // 5. Navigation Stack
        System.out.println();
        System.out.println("5. Navigation history stack check:");
        Stack<String> stack = new Stack<>();
        stack.push("Dashboard");
        stack.push("Courses");
        stack.push("Grades");
        System.out.println("   Current top is 'Grades'? " + EnterpriseClinic.isTopNavigationPage(stack, "Grades"));

        System.out.println();
        System.out.println("Demo completed. Run SelfCheck.java to test for planted defects.");
    }
}
