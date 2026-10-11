package com.aptech.s10.tests;

import com.aptech.s10.model.Student;
import com.aptech.s10.repository.FileBackedStudentRepository;
import com.aptech.s10.repository.InMemoryStudentRepository;
import com.aptech.s10.repository.StudentRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example 3 — the four lifecycle annotations, and the one that saves the most time: {@code @BeforeEach}.
 *
 * <p>Run the tests and read the printed lines in order. The two "once" annotations wrap the
 * whole class; the two "each" annotations wrap every single test. That is the entire lifecycle.
 *
 * <p>The point of the middle pair is <strong>isolation</strong>. {@link #freshStoreForEveryTest}
 * builds a brand-new store before each test, so the second test cannot see what the first one
 * did. Without it, tests start depending on the order they run in — and a suite whose result
 * changes when you rename a method is worse than no suite, because it teaches you to ignore red.
 *
 * <p>{@code @TestMethodOrder} is here for a reason worth saying out loud: <strong>JUnit does not
 * promise to run your test methods in any particular order</strong>, on purpose, to stop you
 * relying on it. The annotation is used here only so this example prints the same thing twice —
 * and a suite that needs it to pass has a bug.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class E03_Lifecycle {

    /** Built fresh by {@code @BeforeEach}, so every test starts from the same known state. */
    private StudentRepository store;

    @BeforeAll
    static void beforeAnything() {
        System.out.println("@BeforeAll   once, before the first test of the class");
    }

    @AfterAll
    static void afterEverything() {
        System.out.println("@AfterAll    once, after the last test of the class");
    }

    @BeforeEach
    void freshStoreForEveryTest() {
        store = new InMemoryStudentRepository();
        store.save(Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50")));
        System.out.println("@BeforeEach  a new store, holding exactly one student");
    }

    @AfterEach
    void afterEveryTest() {
        System.out.println("@AfterEach   this test is over; the store is about to be discarded");
    }

    @Test
    @Order(1)
    @DisplayName("the first test sees exactly one student")
    void firstTestSeesOneStudent() {
        assertEquals(1, store.count());
    }

    @Test
    @Order(2)
    @DisplayName("the second test may make a mess")
    void secondTestMakesAMess() {
        store.save(Student.unsaved("Grace Hopper", "grace@example.com", 1, new BigDecimal("91.00")));
        assertEquals(2, store.count(), "this test's own insert should be visible to it");
    }

    @Test
    @Order(3)
    @DisplayName("the third test proves the second test's mess is gone")
    void thirdTestStartsClean() {
        // If this fails with "expected: <1> but was: <2>", the tests are sharing a store and
        // @BeforeEach is missing — the single most common cause of a suite that passes alone
        // and fails together.
        assertEquals(1, store.count(), "if this is 2, two tests are sharing state");
        assertTrue(store.findById(2).isEmpty(), "Grace should not exist in a fresh store");
    }

    @Test
    @Order(4)
    @DisplayName("a temporary folder, made new for this test and deleted after it")
    void aFreshFolderForEveryTest(@TempDir Path tempDir) {
        StudentRepository onDisk = new FileBackedStudentRepository(tempDir);
        onDisk.save(Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50")));

        // A second store opened on the same folder sees what the first one wrote — it really
        // is on disk, not in a list pretending to be.
        assertEquals(1, new FileBackedStudentRepository(tempDir).count());

        // JUnit created this folder for this test alone and deletes it afterwards, so the next
        // test asking for @TempDir gets a genuinely empty one. That is why a file-backed store
        // is still testable: nothing it writes outlives the test that wrote it.
        System.out.println("@TempDir     a throwaway folder, created for this test only");
    }
}
