package com.aptech.a5;

import com.aptech.a5.model.Student;
import com.aptech.a5.repository.H2StudentRepository;
import com.aptech.a5.repository.InMemoryStudentRepository;
import com.aptech.a5.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A5 Part 2 — the suite.   &lt;&lt;&lt; WRITE THE FIVE TESTS MARKED TODO &gt;&gt;&gt;
 *
 * <p>This one file runs against <strong>both</strong> of your stores. That is not a trick and it is
 * not extra work: {@link #stores()} hands every test method two stores in turn, so a test written
 * once is a test run twice. Add a third implementation one day and every test below starts checking
 * it without a line changing.
 *
 * <p>And here is the sentence the whole assignment hangs on:
 *
 * <blockquote>If a test passes against the in-memory store and fails against H2, the test was
 * describing the in-memory store — not the interface you wrote down.</blockquote>
 *
 * <p>That is the only way to find out whether the contract you published in
 * {@code StudentRepository} is the contract you actually implemented in R1 and R2.
 *
 * <h2>What is already done for you</h2>
 * <ul>
 *   <li>{@link #stores()} — the {@code @MethodSource} that supplies both stores, with a name for
 *       each so a failure tells you <em>which</em> store broke.</li>
 *   <li>{@link #anIdNobodyHasIsAnEmptyOptional} — one finished test, so you can see the shape.
 *       Read it before writing anything.</li>
 *   <li>{@link #ada()} and friends — the test data, built in code. No SQL, no seeding script, no
 *       {@code data/} folder. The suite must run on a machine that has never opened the registry.</li>
 * </ul>
 *
 * <h2>What is left to do</h2>
 * <p>Five tests, each marked {@code TODO Part 2}. Each one is a sentence about behaviour, not a
 * line of code being covered. Run the suite after every one: the first three will be red until R1
 * and R2 are finished, and that red is useful — it names the method that is still a stub.
 *
 * <h2>The other two requirements</h2>
 * <p>Two things this file cannot do for you, because they are the marked work:
 *
 * <ol>
 *   <li><strong>A test that fails on purpose.</strong> Break one method of your own store — change
 *       a {@code >=} to a {@code >}, make the search case-sensitive, return {@code true} from a
 *       delete that deleted nothing — and confirm something here goes red. Then write down which
 *       test caught it and what the failure message said. Session 10's Example 5 is this exercise
 *       done for you; do it against your own code.</li>
 *   <li><strong>AI-generated test cases, each one reviewed.</strong> Ask for tests of
 *       {@code StudentRepository}, then read every line before you keep it. The shapes to watch for
 *       are in Session 10, Part 8 — a test that asserts nothing, a test that swallows its own
 *       failure, and a test that checks what your code <em>does</em> rather than what it
 *       <em>should</em> do. Keep the ones you can justify, and say in your submission why you threw
 *       the rest away. A green suite you cannot explain scores nothing.</li>
 * </ol>
 *
 * <h2>Running it</h2>
 * <pre><code class="lang-bash"># without Maven, using the two vendored jars - main classes first, then the tests
 * javac -encoding UTF-8 -cp "lib/h2-2.3.232.jar:lib/junit-platform-console-standalone-1.14.4.jar" \
 *       -d target/classes $(find src/main/java -name "*.java")
 * javac -encoding UTF-8 -cp "target/classes:lib/h2-2.3.232.jar:lib/junit-platform-console-standalone-1.14.4.jar" \
 *       -d target/test-classes $(find src/test/java -name "*.java")
 * java -jar lib/junit-platform-console-standalone-1.14.4.jar execute \
 *       --class-path "target/classes:target/test-classes:lib/h2-2.3.232.jar" \
 *       --select-class=com.aptech.a5.StudentRepositoryTest</code></pre>
 *
 * <p>On Windows the classpath separator is <code>;</code> not <code>:</code>, and
 * <code>$(find ...)</code> is not a thing &mdash; list the files, or use
 * <code>for /r src\test\java %f in (*.java) do @echo %f</code>. In IntelliJ the green arrow in the
 * gutter does all of it &mdash; but do it by hand once, so you know what the arrow is doing.
 */
class StudentRepositoryTest {

    // ------------------------------------------------------------------ the two stores

    /**
     * Both stores, with a name each. <strong>GIVEN — do not change it.</strong>
     *
     * <p>Two details are worth reading rather than skimming:
     *
     * <ul>
     *   <li>{@link Db#reset(String)} is called here, so each test method gets a brand-new database
     *       with fresh ids. Without it a test would inherit the one before it, and a suite that
     *       passes alone and fails together is worse than no suite.</li>
     *   <li>{@code Db.reset} also <em>seeds</em> eight students, because the reports need something
     *       to print. The suite does not want them, so {@link #withNobodyInIt} empties the store —
     *       <strong>through the interface</strong>, using only {@code findAll} and
     *       {@code deleteById}. A test that reached behind the interface to clear a table would be
     *       testing something other than your implementation.</li>
     * </ul>
     *
     * <p>This method is called again for every test method in the class, so the two stores are
     * rebuilt rather than shared. That is deliberate: shared fixtures are how tests start lying.
     */
    static Stream<Arguments> stores() {
        Db.reset(Db.MEM_URL);

        return Stream.of(
                Arguments.of(Named.of("in-memory", withNobodyInIt(new InMemoryStudentRepository()))),
                Arguments.of(Named.of("H2", withNobodyInIt(new H2StudentRepository(Db.MEM_URL)))));
    }

    /** Empties a store using only the methods {@link StudentRepository} declares. */
    private static StudentRepository withNobodyInIt(StudentRepository store) {
        for (Student student : store.findAll()) {
            store.deleteById(student.id());
        }
        return store;
    }

    // ------------------------------------------------------------------ the test data

    private static Student ada() {
        return Student.unsaved("Ada Lovelace", "ada@example.com", 1, new BigDecimal("88.50"));
    }

    private static Student grace() {
        return Student.unsaved("Grace Hopper", "grace@example.com", 1, new BigDecimal("91.00"));
    }

    /** Nobody has marked Margaret yet. Her mark is null, and null is not zero. */
    private static Student margaret() {
        return Student.unsaved("Margaret Hamilton", "margaret@example.com", 2, null);
    }

    // ------------------------------------------------------------------ the worked example

    /**
     * A finished test, so the shape is not a guess. Everything below follows this pattern:
     * arrange the data, call one method, assert one thing, and put a sentence in the third
     * argument so a failure tells you what was expected <em>and why</em>.
     *
     * <p>The name says what the code should do, not what the code does. If you ever find yourself
     * naming a test after what you observed rather than what you intended, stop — you are about to
     * write down a bug as if it were a requirement.
     */
    @ParameterizedTest(name = "{0}: an id nobody has is an empty Optional")
    @MethodSource("stores")
    @DisplayName("an id nobody has is an empty Optional, not an exception")
    void anIdNobodyHasIsAnEmptyOptional(StudentRepository store) {
        // Absence is normal, and the interface says so: Optional, not null and not a throw. If this
        // test fails with an exception instead of an assertion, R1 or R2 has decided that asking
        // for somebody who is not there is an error — and every caller now needs a try/catch to ask
        // a simple question.
        assertTrue(store.findById(9999).isEmpty());
    }

    // ------------------------------------------------------------------ TODO Part 2 (1/5)

    @ParameterizedTest(name = "{0}: save() gives an inserted student an id and hands it back")
    @MethodSource("stores")
    @DisplayName("TODO: save() gives an inserted student an id and hands it back")
    void insertAssignsAnId(StudentRepository store) {
        // TODO Part 2 — the caller must never invent an id.
        //   save(ada())  ->  the returned Student has an id that is not 0
        //                    findById(that id) finds exactly the student you saved
        //                    count() is 1
        // Compare whole Students, not fields: Student is a record with a real equals(), so
        // assertEquals(stored, store.findById(stored.id()).orElseThrow()) checks everything at once.
        throw new UnsupportedOperationException("TODO Part 2: insertAssignsAnId");
    }

    // ------------------------------------------------------------------ TODO Part 2 (2/5)

    @ParameterizedTest(name = "{0}: saving a student who is already there updates, and does not add")
    @MethodSource("stores")
    @DisplayName("TODO: saving a known id updates instead of inserting a second student")
    void saveUpdatesWhenTheIdIsKnown(StudentRepository store) {
        // TODO Part 2 — the bug this catches is a real one: an update to somebody who was deleted
        // last week quietly becoming a brand-new student with the same name.
        //   save ada, then save the same student with a different mark
        //   assert the id did not change, the count is still 1, and the mark is the new one
        //
        // Then answer the harder half, which is Part 2's honest bug hunt: what does YOUR store do
        // when you save an id that nobody has? Write that test too. Saving an unknown id should be
        // refused — assertThrows(IllegalArgumentException.class, ...) — and the count must be
        // unchanged afterwards, so a refused save cannot have quietly added anybody.
        throw new UnsupportedOperationException("TODO Part 2: saveUpdatesWhenTheIdIsKnown");
    }

    // ------------------------------------------------------------------ TODO Part 2 (3/5)

    @ParameterizedTest(name = "{0}: the search does not care about capital letters")
    @MethodSource("stores")
    @DisplayName("TODO: findByNameContaining ignores case, in every direction")
    void searchIgnoresCapitalLetters(StudentRepository store) {
        // TODO Part 2 — save ada, then search for each spelling below and expect to find her once:
        //     "lovelace", "LOVELACE", "LoveLace", and a fragment like "Ada"
        // And expect findByNameContaining("Turing") to be empty: a search that returns everybody
        // is the same as a search that returns nobody.
        //
        // When you lowercase in the implementation, use Locale.ROOT. The default locale is a trap:
        // in Turkish, "I".toLowerCase() is not "i", so a search that works on your laptop fails on
        // a machine set to tr-TR. The comment is in R1's TODO for this reason.
        throw new UnsupportedOperationException("TODO Part 2: searchIgnoresCapitalLetters");
    }

    // ------------------------------------------------------------------ TODO Part 2 (4/5)

    @ParameterizedTest(name = "{0}: deleteById says whether it actually removed anyone")
    @MethodSource("stores")
    @DisplayName("TODO: deleteById reports honestly, in both directions")
    void deleteReportsHonestly(StudentRepository store) {
        // TODO Part 2 — the return value is a promise, and a caller will believe it.
        //   save ada, delete her id        -> true   (she was there)
        //   delete the same id again       -> false  (she is gone)
        //   delete 9999, nobody's id       -> false
        //   count() is 0 at the end
        //
        // A store that always returns true makes a caller tell a user their record is deleted when
        // it is still there. That failure never throws, which is exactly why it needs a test.
        throw new UnsupportedOperationException("TODO Part 2: deleteReportsHonestly");
    }

    // ------------------------------------------------------------------ TODO Part 2 (5/5)

    @ParameterizedTest(name = "{0}: a null mark comes back null, not zero")
    @MethodSource("stores")
    @DisplayName("TODO: a null mark survives the round trip as null")
    void aNullMarkStaysNull(StudentRepository store) {
        // TODO Part 2 — this is the single most valuable test in the assignment, because it is the
        // bug that H2 hands you for free.
        //   save margaret()  (her mark is null), then read her back by id
        //   assertNull(back.mark())        -- not zero
        //   assertNull(back.courseId())    -- not zero either
        //   assertEquals("-", back.grade()) -- and so the report says "not marked", not "failed"
        //
        // In R2 the trap is rs.getBigDecimal("mark") followed by rs.wasNull(). Without that check a
        // student who has not been marked yet reads as 0, and the report fails somebody who was
        // never assessed. In R1 the same trap is a getOrDefault or a ternary written carelessly.
        // Both stores must answer the same way, which is the whole reason this suite exists.
        throw new UnsupportedOperationException("TODO Part 2: aNullMarkStaysNull");
    }
}
