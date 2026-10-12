package com.aptech.s13;

import java.util.List;

/**
 * Session 13 - Example 2: Chronological History & Key Milestones of JavaFX
 *
 * Traces the evolution of JavaFX from F3 to modern decoupled OpenJFX.
 */
public class E02_HistoryTimeline {

    public static record Milestone(String year, String version, String event, String technicalSignificance) {}

    public static void main(String[] args) {
        System.out.println("=== The Chronological History of JavaFX ===");

        List<Milestone> timeline = List.of(
            new Milestone(
                "2005",
                "F3 Project",
                "Chris Oliver creates 'Form Follows Function'",
                "Early prototype for a declarative, reactive GUI language on the JVM."
            ),
            new Milestone(
                "2007",
                "Sun Announcement",
                "Announced by Sun Microsystems at JavaOne 2007",
                "Positioned as rich internet application (RIA) platform to compete with Adobe Flash and Silverlight."
            ),
            new Milestone(
                "2008",
                "JavaFX 1.0",
                "Initial commercial release",
                "Used proprietary 'JavaFX Script' syntax. Required developers to learn a separate language."
            ),
            new Milestone(
                "2011",
                "JavaFX 2.0",
                "The Great Java Rewrite",
                "Scrapped JavaFX Script entirely! Rewrote the entire toolkit into pure, 100% standard Java APIs."
            ),
            new Milestone(
                "2014",
                "JavaFX 8",
                "Bundled into Oracle JDK 8",
                "Introduced the modern Modena dark/light theme, native 3D support, DatePicker, and rich CSS."
            ),
            new Milestone(
                "2018",
                "JavaFX 11 (OpenJFX)",
                "Decoupled from the JDK",
                "Spun off into open-source OpenJFX co-led by Oracle and Gluon. Independent 6-month release cadence."
            ),
            new Milestone(
                "2023-Present",
                "JavaFX 21+ LTS",
                "Modern Enterprise Desktop",
                "Full JDK 21/25 LTS compatibility, High-DPI screens, Apple Silicon ARM64, and jpackage desktop bundling."
            )
        );

        for (Milestone m : timeline) {
            System.out.printf("[%s] %-18s : %s\n", m.year(), m.version(), m.event());
            System.out.println("       Impact: " + m.technicalSignificance());
        }

        System.out.println("\nKey Takeaway for Students:");
        System.out.println("Older textbooks mention 'JavaFX 8 bundled in the JDK'. In modern Java (JDK 21+),");
        System.out.println("JavaFX is distributed via Maven Central or as an SDK library. You write 100% Java!");
    }
}
