package com.aptech.s13.lab;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 13 - Guided Lab: First JavaFX Desktop Application
 *
 * "The Aptech Student Welcome Portal"
 * Constructs a fully interactive window with header labels, an input field,
 * dynamic greeting buttons, and event handling.
 */
public class WelcomeApp {

    public static class App extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Starting Guided Lab: WelcomeApp ===");

            // 1. Header Labels
            Label headerTitle = new Label("Aptech Computer Education");
            headerTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0ea5e9;");

            Label subTitle = new Label("Building Rich Java Applications with JavaFX");
            subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

            // 2. Input Section
            Label namePrompt = new Label("Enter Student Name:");
            TextField nameField = new TextField();
            nameField.setPromptText("e.g. Chinelo Okonkwo");

            HBox inputRow = new HBox(10, namePrompt, nameField);
            inputRow.setAlignment(Pos.CENTER_LEFT);

            // 3. Action Buttons & Feedback Banner
            Button greetButton = new Button("Greet Student");
            Button clearButton = new Button("Clear");

            Label feedbackLabel = new Label("Status: Enter your name and click Greet.");
            feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");

            // Event Handling
            greetButton.setOnAction(e -> {
                String input = nameField.getText().trim();
                if (input.isEmpty()) {
                    feedbackLabel.setText("Please enter a valid student name first!");
                    feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444;");
                } else {
                    feedbackLabel.setText("Welcome to JavaFX, " + input + "! Your journey has begun.");
                    feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");
                    System.out.println("[Event Action] Greeted student: " + input);
                }
            });

            clearButton.setOnAction(e -> {
                nameField.setText("");
                feedbackLabel.setText("Status: Form cleared.");
                feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                System.out.println("[Event Action] Form reset.");
            });

            HBox buttonRow = new HBox(12, greetButton, clearButton);
            buttonRow.setAlignment(Pos.CENTER_LEFT);

            // 4. Assemble Root Layout
            VBox root = new VBox(15, headerTitle, subTitle, inputRow, buttonRow, feedbackLabel);
            root.setAlignment(Pos.TOP_LEFT);
            root.setStyle("-fx-padding: 24px; -fx-background-color: #ffffff;");

            // 5. Create Scene & Configure Stage
            Scene scene = new Scene(root, 520, 280);
            primaryStage.setTitle("Aptech Student Welcome Portal");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Simulate user interaction for verification
            System.out.println("\nSimulating user interaction:");
            nameField.setText("Chinelo Okonkwo");
            greetButton.fire();
            System.out.println("Feedback banner text: '" + feedbackLabel.getText() + "'");
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
