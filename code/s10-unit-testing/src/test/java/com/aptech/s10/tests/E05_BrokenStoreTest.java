package com.aptech.s10.tests;

import com.aptech.s10.model.Student;
import com.aptech.s10.repository.BrokenRepository;
import com.aptech.s10.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example 5 — a test that <strong>must</strong> fail, and what its failure tells you.
 *
 * <p>This is the same contract as Example 4, pointed at {@link BrokenRepository} — a store with
 * three deliberate faults and nothing else wrong with it. Four tests here, and the run comes
 * back with three red and one green.
 *
 * <p>Read that result again, because it is the point of the whole example. A suite that goes
 * <em>completely</em> red tells you almost nothing: something is broken, somewhere. A suite
 * where three named behaviours fail and the rest pass has told you <strong>exactly</strong> what
 * is wrong, and — just as usefully — what is still fine. That is what "a test that fails for a
 * reason you can name" means in the A5 rubric, and it is the difference between a test suite and
 * a stack trace.
 *
 * <p>The three failures, each naming a behaviour rather than a line number:
 *
 * <ol>
 *   <li>the search is case-sensitive, so "LOVELACE" finds nobody;</li>
 *   <li>{@code deleteById} claims success whether or not it deleted anything;</li>
 *   <li>{@code save} inserts an unknown id instead of refusing it — so a failed update becomes
 *       a duplicate student.</li>
 * </ol>
 *
 * <p>The fourth test is green on purpose. A suite has to be able to tell you that something
 * <em>is</em> working; if every test went red you would have no idea how far the damage spread.
 *
 * <p>And a warning about how this example is usually misused. If you delete these tests once
 * you have seen the red, you have learned nothing durable. The habit worth keeping is the one
 * A5 asks for: break your own code on purpose — change a {@code >=} to a {@code >}, make a
 * search case-sensitive, return {@code true} from a delete — and check that something goes red.
 * <strong>If nothing fails, the test was not testing anything.</strong>
 */
class E05_BrokenStoreTest {

    /** The store under test. Only this line differs from a test of a correct store. */
    private StudentRepository store;

    @BeforeEach
    void aFreshBrokenStore() {
        store = new BrokenRepository();
        store.save(Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50")));
    }

    @Test
    @DisplayName("FAILS: the search should not care about capital letters")
    void searchIsCaseInsensitive() {
        assertEquals(1, store.findByNameContaining("LOVELACE").size(),
                "searching for LOVELACE should find Ada Lovelace");
    }

    @Test
    @DisplayName("FAILS: deleteById should report false when it removed nobody")
    void deleteReportsHonestly() {
        // It removed nothing — Ada is still there. A caller that trusts this return value will
        // tell a user their record is gone when it is not:
        assertFalse(store.deleteById(9999), "nothing was deleted, so this must be false");
        assertEquals(1, store.count(), "the store still holds Ada, which is why false was correct");
    }

    @Test
    @DisplayName("FAILS: saving an unknown id should be refused, not turned into a new student")
    void saveRefusesAnUnknownId() {
        assertThrows(IllegalArgumentException.class,
                () -> store.save(new Student(9999, "Ghost", "ghost@example.com", 1, null, true)),
                "an update to a student who is not there must not become a new student");

        assertEquals(1, store.count(), "the refused save must not have added anybody");
    }

    @Test
    @DisplayName("PASSES: everything else about this store is correct")
    void theRestOfTheStoreIsFine() {
        // Four of the eight methods are inherited unchanged and work perfectly. Knowing that is
        // worth something: the fault is in three named places, not in the whole class.
        assertTrue(store.findById(1).isPresent());
        assertEquals(List.of("Ada Lovelace"), store.findAll().stream().map(Student::name).toList());
        assertEquals("broken", store.storeName());
        assertEquals(2, store.save(Student.unsaved("Grace Hopper", "grace@example.com", 1, null)).id(),
                "inserting a brand-new student still works");
    }
}
