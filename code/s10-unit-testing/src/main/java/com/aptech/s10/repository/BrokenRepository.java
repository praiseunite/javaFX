package com.aptech.s10.repository;

import com.aptech.s10.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Store number three: <strong>deliberately wrong</strong>. This class is the lesson, not a bug.
 *
 * <p>It holds a real {@link InMemoryStudentRepository} and forwards almost everything to it, so
 * the good store stays properly encapsulated and this file stays short enough to read in one
 * go. Exactly three methods are sabotaged, each in a way a careless implementation really would
 * be:
 *
 * <ol>
 *   <li>{@link #findByNameContaining} compares case-sensitively. Searching "LOVELACE" finds
 *       nothing, although Ada Lovelace is right there.</li>
 *   <li>{@link #deleteById} always answers {@code true}, even when it removed nothing. A caller
 *       that trusts it reports a deletion that never happened.</li>
 *   <li>{@link #save} quietly <em>inserts</em> a student whose id is unknown instead of refusing.
 *       An update to a student who does not exist becomes a silent second copy.</li>
 * </ol>
 *
 * <p>Everything else is correct — which matters, because it means most of the suite still
 * passes. A suite that goes <em>entirely</em> red tells you nothing; a suite where three named
 * behaviours fail and the rest pass has told you precisely where the fault is. Example 5 points
 * four tests at this store on purpose, and the failure messages name the <em>behaviour</em> that
 * broke, not the line of code. That is the difference between a test suite and a stack trace.
 */
public final class BrokenRepository implements StudentRepository {

    private final InMemoryStudentRepository real = new InMemoryStudentRepository();

    @Override
    public Optional<Student> findById(int id) {
        return real.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return real.findAll();
    }

    @Override
    public List<Student> findByCourse(int courseId) {
        return real.findByCourse(courseId);
    }

    /** Fault 1: the case is not folded, so a search is only as good as its capitals. */
    @Override
    public List<Student> findByNameContaining(String fragment) {
        return real.findAll().stream()
                .filter(s -> s.name().contains(fragment))
                .toList();
    }

    /** Fault 3: an unknown id is inserted with a fresh id instead of being refused. */
    @Override
    public Student save(Student student) {
        if (student.id() != 0 && real.findById(student.id()).isEmpty()) {
            return real.save(Student.unsaved(student.name(), student.email(),
                    student.courseId(), student.mark()));
        }
        return real.save(student);
    }

    /** Fault 2: it reports success whether or not there was anything to delete. */
    @Override
    public boolean deleteById(int id) {
        real.deleteById(id);
        return true;
    }

    @Override
    public int count() {
        return real.count();
    }

    @Override
    public String storeName() {
        return "broken";
    }
}
