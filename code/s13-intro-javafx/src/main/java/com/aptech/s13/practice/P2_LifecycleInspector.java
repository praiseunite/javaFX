package com.aptech.s13.practice;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Practice Exercise 2 (Medium): Lifecycle Diagnostics Inspector
 *
 * Tracks and logs timestamps, thread names, and state transitions
 * through the complete lifecycle pipeline: init -> start -> stop.
 */
public class P2_LifecycleInspector {

    public static record PhaseLog(String phase, String thread, boolean isFxThread) {}

    public static class App extends Application {
        private final List<PhaseLog> auditLog = new ArrayList<>();

        @Override
        public void init() {
            auditLog.add(new PhaseLog(
                "INIT",
                Thread.currentThread().getName(),
                Platform.isFxApplicationThread()
            ));
        }

        @Override
        public void start(Stage primaryStage) {
            auditLog.add(new PhaseLog(
                "START",
                Thread.currentThread().getName(),
                Platform.isFxApplicationThread()
            ));

            VBox root = new VBox(10);
            root.getChildren().add(new Label("Lifecycle Diagnostics Active"));

            Scene scene = new Scene(root, 360, 200);
            primaryStage.setTitle("Lifecycle Inspector");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        @Override
        public void stop() {
            auditLog.add(new PhaseLog(
                "STOP",
                Thread.currentThread().getName(),
                Platform.isFxApplicationThread()
            ));

            System.out.println("=== Lifecycle Diagnostics Report ===");
            for (PhaseLog log : auditLog) {
                System.out.printf("Phase: %-6s | Thread: %-26s | FX Thread: %s\n",
                        log.phase(), log.thread(), log.isFxThread());
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
