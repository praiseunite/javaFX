package com.aptech.s13.practice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Practice Exercise 1 (Easy): Minimal Stage Configuration
 *
 * Configures window title, fixed dimensions (400x250), disables window resizing,
 * and sets a centered information label.
 */
public class P1_MinimalStage {

    public static class App extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Practice 1: Configuring Minimal Stage ===");

            Label messageLabel = new Label("Student Registry - Read-Only View");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            StackPane root = new StackPane(messageLabel);
            Scene scene = new Scene(root, 400, 250);

            primaryStage.setTitle("Student Records - Read Only View");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            System.out.println("Window Title: " + primaryStage.getTitle());
            System.out.println("Dimensions: " + (int)scene.getWidth() + "x" + (int)scene.getHeight());
            System.out.println("Is Resizable: " + primaryStage.isResizable());

            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
