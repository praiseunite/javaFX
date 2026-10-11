package com.aptech.s10.tests;

import com.aptech.s10.model.Student;
import com.aptech.s10.repository.FileBackedStudentRepository;
import com.aptech.s10.repository.InMemoryStudentRepository;
import com.aptech.s10.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example 4 — <strong>the centre of Session 10</strong>. One suite, two stores.
 *
 * <p>This is the payoff for the interface Session 9 extracted. Every test below is written
 * against {@link StudentRepository} and never names an implementation. {@link #stores()} hands
 * the same test method both stores in turn, so writing the suite once runs it twice.
 *
 * <p>Read {@link #stores()} carefully, because it is where the whole idea lives. It returns a
 * {@code Stream} of arguments, and each argument is a <em>single</em> store wearing a label —
 * {@link Named#of(String, Object)} pairs the two together, so the report can print {@code file}
 * or {@code in-memory} beside a failure while the test method still receives one store. JUnit
 * calls the method, then calls each test once per store. Add a third implementation and
 * <em>every test in this file</em> starts checking it, without a line changing below.
 *
 * <p>And now the sentence that matters most in this session:
 *
 * <blockquote>If a test passes against the in-memory store and fails against the file store,
 * the test was describing the in-memory store, not the interface.</blockquote>
 *
 * <p>That is what a second implementation is <em>for</em>. It is not extra work — it is the only
 * way to find out whether the contract you wrote down is the contract you meant. It is also
 * exactly what Assignment A5 asks for: this suite, run against the two stores <em>you</em>
 * built.
 *
 * <p>One detail worth copying. The file store needs a folder, and the folder must be different
 * for every invocation, or the second run of a test would find the first run's data still on
 * disk and cheerfully assert against it. {@link #newStoreFolder()} makes a unique one each time
 * {@code stores()} is called. A test with a shared fixture is a test that lies to you.
 */
class E04_BothStoresTest {

    /**
     * The two stores, with a name each so the report says which one failed.
     *
     * <p>This method is called again for every test method in the class, which is why the file
     * store gets a brand-new folder each time rather than sharing one.
     */
    static Stream<Arguments> stores() {
        return Stream.of(
                Arguments.of(Named.of("in-memory", new InMemoryStudentRepository())),
                Arguments.of(Named.of("file", new FileBackedStudentRepository(newStoreFolder()))));
    }

    private static Student ada() {
        return Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"));
    }

    private static Student grace() {
        return Student.unsaved("Grace Hopper", "grace@example.com", 1, new BigDecimal("91.00"));
    }

    private static Student margaret() {
        return Student.unsaved("Margaret Hamilton", "margaret@example.com", 2, null);
    }

    // ------------------------------------------------- the contract, once, for every store

    @ParameterizedTest(name = "{0}: a new store is empty")
    @MethodSource("stores")
    @DisplayName("a new store is empty")
    void aNewStoreIsEmpty(StudentRepository store) {
        assertEquals(0, store.count());
        assertTrue(store.findAll().isEmpty());
    }

    @ParameterizedTest(name = "{0}: an id nobody has is an empty Optional, not an exception")
    @MethodSource("stores")
    @DisplayName("an id nobody has is an empty Optional, not an exception")
    void aMissingIdIsEmpty(StudentRepository store) {
        // Absence is normal. If this throws instead of returning empty, every caller has to
        // write a try/catch to ask a simple question.
        assertTrue(store.findById(9999).isEmpty());
    }

    @ParameterizedTest(name = "{0}: save() gives an inserted student an id and hands it back")
    @MethodSource("stores")
    @DisplayName("save() gives an inserted student an id and hands it back")
    void insertAssignsAnId(StudentRepository store) {
        Student stored = store.save(ada());

        assertFalse(stored.id() == 0, "the caller must not have to invent an id");
        assertEquals(1, store.count());
        assertEquals(stored, store.findById(stored.id()).orElseThrow());
    }

    @ParameterizedTest(name = "{0}: two inserts get two different ids")
    @MethodSource("stores")
    @DisplayName("two inserts get two different ids")
    void twoInsertsGetDifferentIds(StudentRepository store) {
        Student first = store.save(ada());
        Student second = store.save(grace());

        assertFalse(first.id() == second.id(), "two students were given the same id");
        assertEquals(2, store.count());
    }

    @ParameterizedTest(name = "{0}: saving a student who is already there updates, and does not add")
    @MethodSource("stores")
    @DisplayName("saving a student who is already there updates, and does not add")
    void saveUpdatesWhenTheIdIsKnown(StudentRepository store) {
        Student stored = store.save(ada());
        Student remarked = store.save(stored.withMark(new BigDecimal("95.00")));

        assertEquals(stored.id(), remarked.id());
        assertEquals(1, store.count(), "an update must not become a second student");
        assertEquals(new BigDecimal("95.00"), store.findById(stored.id()).orElseThrow().mark());
    }

    @ParameterizedTest(name = "{0}: saving an id that nobody has is refused")
    @MethodSource("stores")
    @DisplayName("saving an id that nobody has is refused")
    void saveRefusesAnUnknownId(StudentRepository store) {
        // The mistake this catches is a real one: an update to a student who was deleted last
        // week quietly becoming a new student. BrokenRepository does exactly that (Example 5).
        assertThrows(IllegalArgumentException.class,
                () -> store.save(new Student(9999, "Ghost", "ghost@example.com", 1, null, true)));
        assertEquals(0, store.count(), "the refused save must not have added anybody");
    }

    @ParameterizedTest(name = "{0}: the search does not care about capital letters")
    @MethodSource("stores")
    @DisplayName("the search does not care about capital letters")
    void searchIsCaseInsensitive(StudentRepository store) {
        store.save(ada());

        for (String needle : List.of("lovelace", "LOVELACE", "LoveLace", "Ada")) {
            assertEquals(1, store.findByNameContaining(needle).size(),
                    "searching for \"" + needle + "\" should find Ada Lovelace");
        }
        assertTrue(store.findByNameContaining("Turing").isEmpty());
    }

    @ParameterizedTest(name = "{0}: deleteById says whether it actually removed anyone")
    @MethodSource("stores")
    @DisplayName("deleteById says whether it actually removed anyone")
    void deleteReportsHonestly(StudentRepository store) {
        Student stored = store.save(ada());

        assertTrue(store.deleteById(stored.id()), "a student who was there should report true");
        assertFalse(store.deleteById(stored.id()), "a student who is gone should report false");
        assertFalse(store.deleteById(9999), "an id nobody has should report false");
        assertEquals(0, store.count());
    }

    @ParameterizedTest(name = "{0}: a null mark comes back null, not zero")
    @MethodSource("stores")
    @DisplayName("a null mark comes back null, not zero")
    void aNullMarkStaysNull(StudentRepository store) {
        // The trap this catches: a store that reads a missing number as 0 turns "not marked
        // yet" into "scored nothing", and the report then fails a student who was never assessed.
        Student stored = store.save(margaret());
        Student back = store.findById(stored.id()).orElseThrow();

        assertNull(back.mark(), "a missing mark must not become 0");
        assertEquals(2, back.courseId().intValue(), "the course the student IS on must not be lost in the round trip");
        assertEquals("-", back.grade(), "and so the grade is a dash, not an F");
    }

    @ParameterizedTest(name = "{0}: findByCourse returns only that course's students")
    @MethodSource("stores")
    @DisplayName("findByCourse returns only that course's students")
    void findByCourseFilters(StudentRepository store) {
        store.save(ada());
        store.save(grace());
        store.save(margaret());

        assertEquals(2, store.findByCourse(1).size());
        assertEquals(1, store.findByCourse(2).size());
        assertTrue(store.findByCourse(3).isEmpty());
    }

    @ParameterizedTest(name = "{0}: findAll hands out a copy, so a caller cannot edit the store")
    @MethodSource("stores")
    @DisplayName("findAll hands out a copy, so a caller cannot edit the store")
    void findAllHandsOutACopy(StudentRepository store) {
        store.save(ada());

        assertThrows(UnsupportedOperationException.class,
                () -> store.findAll().clear(),
                "findAll() gave away its own list - a caller can now change the store from outside");
        assertEquals(1, store.count(), "the store's contents must be unchanged");
    }

    @ParameterizedTest(name = "{0}: the students survive a reopen")
    @MethodSource("stores")
    @DisplayName("every student survives, including the one with no mark")
    void dataSurvives(StudentRepository store) {
        store.save(ada());
        store.save(margaret());

        assertEquals(2, store.count(), "the store forgot a student");
        assertEquals(new BigDecimal("88.50"), store.findById(1).orElseThrow().mark());
        assertNull(store.findById(2).orElseThrow().mark(), "the unmarked student lost their null");
    }

    // ------------------------------------------------------------------ test plumbing

    /**
     * A folder that no other test is using. {@code Files.createTempDirectory} makes the name
     * unique, which is exactly what is needed: two tests must never be able to see each other's
     * files, or a green suite depends on the order the tests happened to run in.
     */
    private static Path newStoreFolder() {
        try {
            return Files.createTempDirectory("s10-store");
        } catch (IOException e) {
            throw new UncheckedIOException("could not make a folder for the file store", e);
        }
    }
}
