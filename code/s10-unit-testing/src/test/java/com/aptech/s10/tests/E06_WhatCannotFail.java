package com.aptech.s10.tests;

import com.aptech.s10.model.Student;
import com.aptech.s10.repository.InMemoryStudentRepository;
import com.aptech.s10.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example 6 — four tests that <strong>pass, and prove nothing</strong>.
 *
 * <p>Every test in this class is green. Run the file: four tests, four passes, zero failures.
 * And not one of them would notice if {@code Student.grade()} were deleted tomorrow. This is the
 * most dangerous state a test suite can be in, because a green tick is exactly what you were
 * hoping to see, and it is the one result nobody investigates.
 *
 * <p>Each test below has a matching mistake, and each mistake is one an AI assistant makes
 * readily when you ask it to "write some tests for this class". That is not a coincidence —
 * Part 8 of this session is about spotting exactly these four shapes in generated code.
 *
 * <table border="1">
 *   <caption>Four ways to write a test that cannot fail</caption>
 *   <tr><th>Test</th><th>The mistake</th></tr>
 *   <tr><td>{@link #thisTestAssertsNothing()}</td>
 *       <td>The failure is that there is no assertion at all. Calling a method is not testing it.</td></tr>
 *   <tr><td>{@link #thisTestComparesAValueToItself()}</td>
 *       <td>{@code assertEquals(x, x)} is true whatever {@code x} is.</td></tr>
 *   <tr><td>{@link #thisTestSwallowsItsOwnFailure()}</td>
 *       <td>An empty {@code catch} turns a failing assertion into a passing test.</td></tr>
 *   <tr><td>{@link #thisTestAssertsTheSetupNotTheCode()}</td>
 *       <td>It asserts what the test itself just built, not what the code did with it.</td></tr>
 * </table>
 *
 * <p>The cure is one question, asked of every test you write or accept:
 * <strong>if I broke the code, would this go red?</strong> A test you cannot imagine failing is
 * not a test — it is a comment that costs electricity to run.
 *
 * <p>Note that this class is not {@code @Disabled} and the tests are not left out of the suite.
 * They are here to be read, and the habit is easier to build when you have seen the traps in
 * the same directory as the real tests.
 */
class E06_WhatCannotFail {

    private static Student ada() {
        return Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"));
    }

    @Test
    @DisplayName("...passes, because it never asks a question")
    void thisTestAssertsNothing() {
        Student student = ada();
        student.grade();
        student.name();

        // No assertion. If grade() returned null, or threw, or returned "Z" — this test would
        // still pass. It proves the methods do not crash, and nothing else. If that is all you
        // wanted to know, say so in the name; otherwise it is a false sense of safety.
    }

    @Test
    @DisplayName("...passes, because a value always equals itself")
    void thisTestComparesAValueToItself() {
        String grade = ada().grade();

        // True for every possible value of grade. This is a tautology wearing a test's clothes —
        // and it is astonishingly easy to write by accident when you meant (expected, actual).
        assertEquals(grade, grade);
    }

    @Test
    @DisplayName("...passes, because it catches the evidence")
    void thisTestSwallowsItsOwnFailure() {
        try {
            // This assertion is false: a mark of 10 is an F, not an A.
            assertEquals("A", ada().withMark(new BigDecimal("10")).grade());
        } catch (AssertionError swallowed) {
            // Empty on purpose, to show what it does. JUnit never hears about the failure, so
            // the test is green and the bug ships. A catch that logs and continues is the same
            // mistake with better manners.
        }
    }

    @Test
    @DisplayName("...passes, because it asserts the setup instead of the code")
    void thisTestAssertsTheSetupNotTheCode() {
        StudentRepository store = new InMemoryStudentRepository();
        Student saved = store.save(ada());

        // saved was built by save(). Asserting on it tests Student's constructor, not the store.
        // The store is never asked anything, so nothing about the store is checked. A useful
        // version of this test asks the store the question back:
        //     assertEquals(saved, store.findById(saved.id()).orElseThrow());
        assertEquals(1, saved.id());
        assertTrue(saved.active());
    }
}
