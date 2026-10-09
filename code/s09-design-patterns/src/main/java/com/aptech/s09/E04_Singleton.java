package com.aptech.s09;

import com.aptech.s09.config.AppConfig;
import com.aptech.s09.repository.RepositoryFactory;
import com.aptech.s09.repository.StudentRepository;

/**
 * Example 4 — proving there is one, and being honest about the price.
 *
 * <p>The first section is the proof: two calls, one object. The second shows why that is
 * worth anything — two different parts of the program read the same decision and cannot
 * disagree about it. The third is the part most lessons leave out.
 */
public final class E04_Singleton {

    public static void main(String[] args) {
        System.out.println(Db.rule("Two calls, one object"));

        AppConfig first = AppConfig.get();
        AppConfig second = AppConfig.get();

        System.out.println("  first == second            : " + (first == second));
        System.out.println("  the same object            : "
                + (System.identityHashCode(first) == System.identityHashCode(second)));
        System.out.println("  first  : " + first);
        System.out.println("  second : " + second);
        System.out.println();
        System.out.println("  Both lines printed the same values because there is one");
        System.out.println("  AppConfig, not two that happen to agree.");

        System.out.println(Db.rule("One decision, read from two places"));
        StudentRepository fromFactory = RepositoryFactory.createDefault();
        StudentRepository fromConfig = RepositoryFactory.create(AppConfig.get().studentStore());
        System.out.println("  RepositoryFactory.createDefault()             -> "
                + fromFactory.getClass().getSimpleName());
        System.out.println("  RepositoryFactory.create(AppConfig.get()...) -> "
                + fromConfig.getClass().getSimpleName());
        System.out.println("  They agree because there is one AppConfig for both to read.");

        System.out.println(Db.rule("What this costs"));
        System.out.println("""
                  - a test cannot hand this class different values. It has to set system
                    properties before the class is first loaded - ordering you cannot see
                    from the test, and a failure that depends on test execution order
                  - there can never be two of them, not even for one test that wants to
                    compare "before" with "after"
                  - the constructor is private, so nothing can wrap it, decorate it, or take
                    it as a parameter where an interface was expected
                """.stripTrailing());

        System.out.println(Db.rule("When a singleton is the right answer"));
        System.out.println("  Here:  the values are read once at startup and never change.");
        System.out.println("  Not here: a Connection, a repository, a fee policy - anything a test");
        System.out.println("         would want two of, or want to replace with a stand-in.");

        System.out.println(Db.rule("The same program, configured without a rebuild"));
        System.out.println("  java -Dregistry.store=h2 -Dregistry.locale=fr com.aptech.s09.E04_Singleton");
        System.out.println("  AppConfig reads those properties in its constructor. No other class");
        System.out.println("  is told, and nothing is recompiled.");
    }
}
