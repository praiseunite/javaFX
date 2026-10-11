package com.aptech.s10.tests;

import com.aptech.s10.model.Student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Example 1 — the smallest useful test, and the shape every test has.
 *
 * <p>Three things to notice, and they are the whole of JUnit:
 *
 * <ol>
 *   <li><strong>The class has no {@code main}.</strong> It is not a program. It is a list of
 *       things that should be true, and a runner calls them. Compare that with
 *       {@code SelfCheck}, which had to invent a {@code Check} interface, a {@code run} loop, a
 *       counter and an exit code to get the same effect.</li>
 *   <li><strong>Each method is one claim.</strong> {@code eightyIsAnA} says one thing. When it
 *       goes red, nobody has to work out which of four assertions failed.</li>
 *   <li><strong>{@code assertEquals(expected, actual)}</strong> — expected first. Getting these
 *       the wrong way round does not break the test; it breaks the <em>message</em>, and the
 *       message is the only thing you have at three in the morning.</li>
 * </ol>
 *
 * <p>The method bodies are three lines each: build a student, ask the question, assert the
 * answer. That ratio is not laziness. A test that is hard to read is a test nobody maintains,
 * and a test nobody maintains is a test that gets deleted the first time it goes red.
 */
class E01_FirstTest {

    /** Builds a student with this mark. Every test below reads better because of this one line. */
    private static Student withMark(String mark) {
        return Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal(mark));
    }

    @Test
    @DisplayName("a student who has not been marked has no grade")
    void noMarkHasNoGrade() {
        Student ada = Student.unsaved("Ada Lovelace", "ada@example.com", 1, null);

        // Not "F". Being unmarked is not the same as having failed, and that is a decision the
        // code makes on purpose — so it is worth a test that would notice if it changed.
        assertEquals("-", ada.grade());
    }

    @Test
    @DisplayName("80 is an A")
    void eightyIsAnA() {
        assertEquals("A", withMark("80").grade());
    }

    @Test
    @DisplayName("79.99 is a B, not an A")
    void justUnderEightyIsAB() {
        // The boundary is the interesting value. This test and the one above it are what stop
        // ">=" quietly becoming ">".
        assertEquals("B", withMark("79.99").grade());
    }

    @Test
    @DisplayName("a mark of 100 is an A and a mark of 0 is an F")
    void theEndsOfTheRangeAreBothCovered() {
        assertEquals("A", withMark("100").grade());
        assertEquals("F", withMark("0").grade());
    }
}
