Two jars, both vendored so this assignment needs no download and works with no internet.

h2-2.3.232.jar
    The H2 JDBC driver, for R2. With Maven (pom.xml already lists it) the jar is fetched
    automatically and this copy is unused.

junit-platform-console-standalone-1.14.4.jar
    JUnit 5 for Assignment A5 Part 2, for the same reason: one file, no download. It is the
    whole of JUnit in a single jar - the @Test annotations, the assertions, the test engine
    and the command-line launcher - which is why there is one file here and not five.
    It is also runnable: `java -jar lib/junit-platform-console-standalone-1.14.4.jar` prints
    its own help, and `execute` runs a suite without an IDE. With Maven, the junit-jupiter
    dependency in pom.xml does the same job and this copy is unused.

Adding them in IntelliJ:
    File -> Project Structure -> Libraries -> + -> Java -> select the files in this folder.

Adding them on the command line:
    put both on the classpath, separated by ; on Windows and : elsewhere.
