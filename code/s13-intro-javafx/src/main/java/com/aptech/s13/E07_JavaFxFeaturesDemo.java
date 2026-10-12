package com.aptech.s13;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Session 13 - Example 7: Core Features of JavaFX
 *
 * Demonstrates:
 * 1. Observable Properties & Change Listeners (Reactive UI)
 * 2. Unidirectional Property Binding
 * 3. Safe Multi-threaded UI updates via Platform.runLater()
 */
public class E07_JavaFxFeaturesDemo {

    public static void main(String[] args) {
        System.out.println("=== JavaFX Core Features: Properties, Binding & Concurrency ===\n");

        // 1. Observable Properties & Change Listeners
        System.out.println("--- 1. Observable Properties & Listeners ---");
        StringProperty studentName = new SimpleStringProperty("Initial State");

        studentName.addListener((observable, oldValue, newValue) -> {
            System.out.println("  [Listener Triggered] Property changed from '" + oldValue + "' -> '" + newValue + "'");
        });

        System.out.println("Setting studentName to 'Ada Lovelace':");
        studentName.set("Ada Lovelace");

        System.out.println("Setting studentName to 'Alan Turing':");
        studentName.set("Alan Turing");

        // 2. Unidirectional Property Binding
        System.out.println("\n--- 2. Unidirectional Binding ---");
        StringProperty sourceProperty = new SimpleStringProperty("Source Value A");
        StringProperty targetDisplay = new SimpleStringProperty("Old Display");

        System.out.println("Binding targetDisplay to sourceProperty...");
        targetDisplay.bind(sourceProperty);
        System.out.println("  targetDisplay value after bind: " + targetDisplay.get());

        System.out.println("Updating sourceProperty to 'Source Value B'...");
        sourceProperty.set("Source Value B");
        System.out.println("  targetDisplay automatically updated: " + targetDisplay.get());

        // 3. Concurrency & Platform.runLater
        System.out.println("\n--- 3. Concurrency & Platform.runLater ---");
        System.out.println("Main Thread Name: " + Thread.currentThread().getName());
        System.out.println("Background worker simulating network download or database fetch...");

        Thread backgroundWorker = new Thread(() -> {
            String workerName = Thread.currentThread().getName();
            System.out.println("  [Background Worker: " + workerName + "] Fetching student records from database...");

            // Simulate background work
            String loadedData = "Fetched 15 student records from H2 DB";

            // Golden Rule: Never touch the UI from a background worker thread!
            // Hand the update to Platform.runLater() to execute on the JavaFX Application Thread:
            Platform.runLater(() -> {
                String currentThread = Thread.currentThread().getName();
                boolean isFxThread = Platform.isFxApplicationThread();
                System.out.println("  [Platform.runLater] Updating UI status label on: " + currentThread + " (Is FX: " + isFxThread + ")");
                System.out.println("  [Platform.runLater] UI updated with: '" + loadedData + "'");
            });
        }, "Database-Worker-Thread");

        backgroundWorker.start();
        try {
            backgroundWorker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nSummary:");
        System.out.println("JavaFX eliminates manual observer boilerplate with Properties and guarantees");
        System.out.println("rock-solid thread safety with Platform.runLater().");
    }
}
