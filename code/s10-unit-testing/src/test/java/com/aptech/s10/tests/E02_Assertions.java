package com.aptech.s10.tests;

import com.aptech.s10.model.Student;
import com.aptech.s10.policy.Money;
import com.aptech.s10.repository.InMemoryStudentRepository;
import com.aptech.s10.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example 2 — the assertion vocabulary, each one used on code that really exists.
 *
 * <p>An assertion is a claim plus the evidence that would disprove it. The name you choose says
 * what you expected; the value you pass says what you got. Read a failure as a sentence:
 * "expected: &lt;A&gt; but was: &lt;B&gt;" is the code and the test disagreeing, out loud.
 *
 * <p>Two of these are worth more than the rest:
 *
 * <ul>
 *   <li>{@code assertThrows} — because the validating constructor in {@link Student} is a
 *       <em>promise</em> that bad data cannot exist, and a promise nobody tests is a comment.</li>
 *   <li>{@code assertAll} — because a test that stops at the first failure hides the other
 *       three. When you are testing one object's five fields, you want all five answers.</li>
 * </ul>
 *
 * <p>And one trap, at the bottom: money. {@code BigDecimal.equals} compares the
 * <em>scale</em> as well as the number, so {@code 450.0} and {@code 450.00} are not
 * {@code equals} even though {@code compareTo} says they are the same. The last test makes that
 * difference visible instead of leaving it to be discovered in production.
 */
class E02_Assertions {

    private static Student ada() {
        return Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"));
    }

    @Test
    @DisplayName("assertEquals is for values that should be the same")
    void assertEqualsIsForValues() {
        assertEquals("Ada Lovelace", ada().name());
        assertEquals(5, "Hello".length());
    }

    @Test
    @DisplayName("assertNotEquals is for the thing that must have changed")
    void assertNotEqualsIsForChange() {
        Student before = ada();
        Student after = before.withMark(new BigDecimal("95.00"));

        // A record's withMark makes a copy, so the original is untouched. That is worth knowing.
        assertEquals(new BigDecimal("88.50"), before.mark());
        assertNotEquals(before.mark(), after.mark());
    }

    @Test
    @DisplayName("assertTrue and assertFalse are for yes/no questions")
    void booleanAssertions() {
        StudentRepository store = new InMemoryStudentRepository();
        assertTrue(store.findAll().isEmpty(), "a new store should have nothing in it");
        assertTrue(store.count() == 0);

        Student stored = store.save(ada());
        assertFalse(stored.id() == 0, "save() must hand back the id the store chose");
    }

    @Test
    @DisplayName("assertNull and assertNotNull are for absence")
    void nullAssertions() {
        Student unmarked = Student.unsaved("Nia Okoro", "nia@example.com", null, null);
        assertNull(unmarked.mark(), "no mark is null, not zero");
        assertNull(unmarked.courseId());

        assertNotNull(ada().mark());
    }

    @Test
    @DisplayName("assertThrows proves the guard actually guards")
    void theValidationRulesAreReal() {
        // The compact constructor promises no invalid Student can exist. Three ways to try.
        IllegalArgumentException noName = assertThrows(IllegalArgumentException.class,
                () -> Student.unsaved("   ", "ada@example.com", 1, null));
        assertTrue(noName.getMessage().contains("name"), "the message should say what was wrong: " + noName.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> Student.unsaved("Ada Lovelace", "not-an-email", 1, null));

        assertThrows(IllegalArgumentException.class,
                () -> Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("101")));
    }

    @Test
    @DisplayName("saving an id that nobody has is refused, not quietly inserted")
    void anIdNobodyHasIsRefused() {
        // This one exists to be contrasted with BrokenRepository in Example 5, which inserts
        // instead of refusing — and so turns one mistake into two students.
        StudentRepository store = new InMemoryStudentRepository();

        // id 0 means "insert me", so this one is fine and does not throw.
        assertDoesNotThrow(() -> store.save(ada()));

        // A non-zero id that is not in the store is a mistake, and the store says which one.
        IllegalArgumentException refused = assertThrows(IllegalArgumentException.class,
                () -> store.save(new Student(9999, "Ghost", "ghost@example.com", 1, null, true)));
        assertTrue(refused.getMessage().contains("9999"),
                "the message should name the bad id, got: " + refused.getMessage());

        assertEquals(1, store.count(), "the refused save must not have added anybody");
    }

    @Test
    @DisplayName("assertDoesNotThrow is for the happy path you want to keep")
    void assertDoesNotThrowIsForTheHappyPath() {
        assertDoesNotThrow(() -> Student.unsaved("Ada Lovelace", "ada@example.com", 1, null));
    }

    @Test
    @DisplayName("assertAll reports every failure, not just the first")
    void assertAllChecksEverything() {
        Student ada = ada();

        // Without assertAll this test stops at the first mismatch. With it, you get all four
        // answers in one run — which is the difference between one fix and four round trips.
        assertAll("Ada's fields",
                () -> assertEquals("Ada Lovelace", ada.name()),
                () -> assertEquals("ada@example.com", ada.email()),
                () -> assertEquals(1, ada.courseId()),
                () -> assertTrue(ada.active()),
                () -> assertEquals("A", ada.grade()));
    }

    @Test
    @DisplayName("assertInstanceOf checks what a factory really returned")
    void assertInstanceOfChecksWhatAFactoryReturned() {
        StudentRepository store = new InMemoryStudentRepository();
        assertInstanceOf(InMemoryStudentRepository.class, store);
        assertEquals("in-memory", store.storeName());
    }

    @Test
    @DisplayName("money is compared, not equalled — and here is why")
    void moneyNeedsCompareTo() {
        BigDecimal fee = Money.of("450.00");

        // Two BigDecimal values can be the same number and still not be equals(),
        // because equals() also compares the scale — the number of decimal places.
        assertNotEquals(fee, new BigDecimal("450.0"), "450.00 and 450.0 are NOT equals()");

        // compareTo asks the question you actually meant: are these the same amount of money?
        assertEquals(0, fee.compareTo(new BigDecimal("450.0")));
        assertEquals(0, Money.percent(new BigDecimal("450.00"), "0.90").compareTo(new BigDecimal("405.00")));
    }
}
