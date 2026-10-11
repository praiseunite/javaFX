One jar, vendored so this session needs no download and works with no internet.

junit-platform-console-standalone-1.14.4.jar
    The whole of JUnit 5 in a single file: the @Test annotations, the assertions, the test
    engine that runs them, and the command-line launcher. That is why there is one file here
    and not five - "add this jar" instead of a dependency lecture.

    Version 1.14.4 is the JUnit Platform 1.x line, which pairs with JUnit Jupiter 5.14.4.
    (JUnit 6.x exists and is newer; this course stays on JUnit 5, which is what the roadmap
    names and what you will meet in most workplaces.)

    Where it came from, so the copy is checkable:
        https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.14.4/
    and its published SHA-256 is
        7c6968cbcaf4301c729f202b23b7d736c5d88be625fc7d27ad5d746146a8bc28
    Verify a jar you download yourself with `sha256sum` (Linux/macOS) or
    `Get-FileHash -Algorithm SHA256` (PowerShell). Do this for anything you ship.

It is also runnable, which is how Session 10 runs the suites without an IDE:
    java -jar lib/junit-platform-console-standalone-1.14.4.jar
        prints its own help, including the full list of --select-* options.
    java -jar lib/junit-platform-console-standalone-1.14.4.jar execute \
        --class-path out:lib/junit-platform-console-standalone-1.14.4.jar \
        --select-class=com.aptech.s10.tests.E01_FirstTest
        runs one test class. Note `--disable-ansi-colors` if you are piping the output
        anywhere that is not a terminal, including into a file.

Adding it in IntelliJ:
    File -> Project Structure -> Libraries -> + -> Java -> select the jar in this folder.
    Without this step every `import org.junit` is underlined red in the editor even though
    the command line compiles perfectly. It is a false alarm, and a costly one if you do
    not know it is one.

Adding it on the command line:
    put it on both the javac and the java classpath, separated from other entries by ; on
    Windows and : everywhere else.

With Maven, the junit-jupiter dependency in pom.xml does the same job and this copy is unused.
