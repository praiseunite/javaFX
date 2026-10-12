package com.aptech.s13;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Session 13 - Example 5: JavaFX Application Lifecycle & Threading Model
 *
 * Demonstrates the 3 lifecycle methods in Application:
 * 1. init()             -> Background/Launcher thread (pre-loading, NO UI access)
 * 2. start(Stage stage) -> JavaFX Application Thread (constructs & shows UI)
 * 3. stop()             -> JavaFX Application Thread (cleanup & teardown)
 */
public class E05_ApplicationLifecycle extends Application {

    private String databaseConnectionStatus = "NOT_CONNECTED";

    @Override
    public void init() throws Exception {
        // init() runs on a launcher/worker thread BEFORE the UI is created
        String threadName = Thread.currentThread().getName();
        boolean isFxThread = Platform.isFxApplicationThread();
        System.out.println("[Lifecycle: init()] Called on thread: " + threadName + " (Is FX Thread: " + isFxThread + ")");
        
        // Typical work in init: loading configuration, opening database connections
        this.databaseConnectionStatus = "CONNECTED_TO_STUDENT_REGISTRY";
        System.out.println("[Lifecycle: init()] Background resource pre-loaded: " + databaseConnectionStatus);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // start() runs exclusively on the JavaFX Application Thread
        String threadName = Thread.currentThread().getName();
        boolean isFxThread = Platform.isFxApplicationThread();
        System.out.println("[Lifecycle: start()] Called on thread: " + threadName + " (Is FX Thread: " + isFxThread + ")");

        // 1. Create UI controls
        Label statusLabel = new Label("System Ready: " + databaseConnectionStatus);

        // 2. Place in root layout container
        StackPane root = new StackPane(statusLabel);

        // 3. Create Scene with root and dimensions
        Scene scene = new Scene(root, 400, 200);

        // 4. Configure Stage and show
        primaryStage.setTitle("JavaFX Lifecycle Demonstration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // stop() runs when the application shuts down
        String threadName = Thread.currentThread().getName();
        boolean isFxThread = Platform.isFxApplicationThread();
        System.out.println("[Lifecycle: stop()] Called on thread: " + threadName + " (Is FX Thread: " + isFxThread + ")");

        // Cleanup resources
        this.databaseConnectionStatus = "DISCONNECTED";
        System.out.println("[Lifecycle: stop()] Cleaned up resources. DB status: " + databaseConnectionStatus);
    }

    public static void main(String[] args) {
        System.out.println("[Main] Initiating JavaFX application launch...");
        Application.launch(E05_ApplicationLifecycle.class, args);
        System.out.println("[Main] JavaFX application execution finished.");
    }
}
