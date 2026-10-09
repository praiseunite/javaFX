package com.aptech.s09;

import com.aptech.s09.model.Student;
import com.aptech.s09.repository.H2StudentRepository;
import com.aptech.s09.repository.InMemoryStudentRepository;
import com.aptech.s09.repository.StudentRepository;
import com.aptech.s09.view.ConsoleView;

/**
 * Example 2 — one interface, two stores, and a method that cannot tell them apart.
 *
 * <p>The claim this program has to prove is not "interfaces are nice". It is that the same
 * code runs against a database and against a list, with nothing but the object it was handed
 * changing. {@link #report} is that code, and it is nine lines long.
 *
 * <p>The last section is the honest one. An in-memory store does not survive the program, and
 * that is not a flaw to apologise for: it is precisely what you want from a store whose job is
 * to last exactly as long as one test.
 */
public final class E02_Repository {

    public static void main(String[] args) throws Exception {
        Db.resetRegistry();

        StudentRepository h2 = new H2StudentRepository();
        // The memory store starts holding exactly what the database holds - nothing more.
        StudentRepository memory = new InMemoryStudentRepository(h2.findAll());

        System.out.println(Db.rule("One method, two stores"));
        System.out.println("  " + ConsoleView.HEADER);
        report("h2", h2);
        report("memory", memory);

        System.out.println(Db.rule("The same enrolment, into both"));
        Student nia = Student.unsaved("Nia Okoro", "nia@example.com", 1, null);
        Student inDb = h2.save(nia);
        Student inMem = memory.save(nia);
        System.out.println("  h2     gave the new student id " + inDb.id());
        System.out.println("  memory gave the new student id " + inMem.id());
        System.out.println("  h2     now holds " + h2.count() + " students");
        System.out.println("  memory now holds " + memory.count() + " students");
        System.out.println("  the two records are equal : " + inDb.equals(inMem));

        System.out.println(Db.rule("Where the two stores differ"));
        System.out.println("  a new h2 repository     holds " + new H2StudentRepository().count()
                + " students  <- it went back to the database");
        System.out.println("  a new memory repository holds " + new InMemoryStudentRepository().count()
                + " students  <- the list died with the last one");
        System.out.println();
        System.out.println("  That is the seam. Session 10's test suite uses the left column's");
        System.out.println("  interface and the right column's speed - and never needs the database.");

        System.out.println(Db.rule("What the caller had to know"));
        System.out.println("  to call report(): the interface StudentRepository, and nothing else.");
        System.out.println("  not: H2, jdbc, SQL, a URL, a password, or which package to import.");
    }

    /**
     * One method, written once, against the interface.
     *
     * <p>There is no {@code if (repo instanceof H2StudentRepository)} in here, and no way to
     * write one that would be worth reading. That is the test of whether an abstraction is
     * real: the code above it gets simpler, rather than gaining a branch.
     */
    private static void report(String label, StudentRepository repo) {
        System.out.println("  " + label + "  (" + repo.storeName() + ")");
        for (Student s : repo.findByCourse(1)) {
            System.out.println("    " + ConsoleView.row(s));
        }
        System.out.println("    name contains 'a' -> "
                + repo.findByNameContaining("a").size() + " match(es)");
    }
}
