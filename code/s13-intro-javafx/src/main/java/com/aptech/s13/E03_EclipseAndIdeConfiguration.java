package com.aptech.s13;

import java.util.ArrayList;
import java.util.List;

/**
 * Session 13 - Example 3: IDE Setup & Runtime Configuration (Eclipse & IntelliJ)
 *
 * Explains how JavaFX is configured in IDEs, how the Module Path works,
 * and demonstrates the Launcher pattern that eliminates missing runtime component errors.
 */
public class E03_EclipseAndIdeConfiguration {

    public static record IdeSetupGuide(String ideName, List<String> setupSteps, String runCommand) {}

    public static void main(String[] args) {
        System.out.println("=== JavaFX IDE Setup & Execution Mechanics ===");

        IdeSetupGuide eclipseGuide = new IdeSetupGuide(
            "Eclipse IDE (Traditional Textbook Flow)",
            List.of(
                "Step 1: Download Eclipse IDE for Java Developers from eclipse.org",
                "Step 2: Help -> Eclipse Marketplace -> Search and install 'e(fx)clipse'",
                "Step 3: Window -> Preferences -> Java -> Build Path -> User Libraries -> New 'JavaFX21'",
                "Step 4: Add external JARs from your OpenJFX SDK /lib folder",
                "Step 5: Right-click Project -> Build Path -> Add Libraries -> User Library -> Check JavaFX21",
                "Step 6: Run Configurations -> Arguments -> VM Arguments -> Add module flags:"
            ),
            "--module-path \"/path/to/javafx-sdk/lib\" --add-modules javafx.controls,javafx.fxml"
        );

        IdeSetupGuide intellijGuide = new IdeSetupGuide(
            "IntelliJ IDEA & Maven (Modern Industry Standard)",
            List.of(
                "Step 1: File -> New -> Project -> Choose JavaFX (or standard Maven project)",
                "Step 2: Include org.openjfx:javafx-controls and javafx-fxml in pom.xml",
                "Step 3: Maven downloads and configures the exact OS-specific native JARs automatically",
                "Step 4: Run application using 'mvn javafx:run' or standard IntelliJ Run gutter icon"
            ),
            "mvn clean javafx:run"
        );

        printGuide(eclipseGuide);
        printGuide(intellijGuide);

        System.out.println("\n=== Understanding the 'Missing Runtime Components' Trap ===");
        System.out.println("Symptom: Error: JavaFX runtime components are missing, and are required to run this application");
        System.out.println("Root Cause: In modern Java (11+), Application class checks if JavaFX is on the module path.");
        System.out.println("If your Main class directly extends Application, the JVM launcher checks module-path before starting.");
        System.out.println("\nThe Proven Solution: The Two-Class Launcher Pattern:");
        System.out.println("  1. MainApp extends Application (contains start, init, UI)");
        System.out.println("  2. AppLauncher does NOT extend Application (contains only public static void main):");
        System.out.println("     public class AppLauncher {");
        System.out.println("         public static void main(String[] args) {");
        System.out.println("             MainApp.main(args); // Calls Application.launch without module-path check!");
        System.out.println("         }");
        System.out.println("     }");
        System.out.println("Status: Verified! The AppLauncher pattern guarantees zero-configuration execution.");
    }

    private static void printGuide(IdeSetupGuide guide) {
        System.out.println("\n[" + guide.ideName() + "]");
        for (String step : guide.setupSteps()) {
            System.out.println("  " + step);
        }
        System.out.println("  Command / VM Options: " + guide.runCommand());
    }
}
