package com.aptech.s15.practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Practice Exercise 1 (Easy): Themed Course Product Card
 *
 * Requirements:
 * - Build a cohesive, modern course card using VBox, HBox, and CSS styling.
 * - Display a category badge, course title, curriculum bullet points, fee tag, and an interactive button.
 * - Implement hover feedback and dynamic button action state.
 */
public class P1_ThemedProductCard {

    public static class CardApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Practice 1: Themed Course Product Card ===");

            VBox screen = new VBox();
            screen.setAlignment(Pos.CENTER);
            screen.setPadding(new Insets(30));
            screen.setStyle("-fx-background-color: #0F172A;");

            // Course Card Container
            VBox card = new VBox(14);
            card.setPrefWidth(340);
            card.setPadding(new Insets(24));
            card.setStyle(
                "-fx-background-color: #1E293B; " +
                "-fx-border-color: #334155; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px;"
            );

            // Badge Row
            HBox badgeRow = new HBox(8);
            Label badge = new Label("APTECH CERTIFIED");
            badge.setStyle(
                "-fx-background-color: #0284C7; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 10px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 3 8 3 8; " +
                "-fx-background-radius: 10px;"
            );

            Label duration = new Label("6 Months | 120 Hours");
            duration.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
            badgeRow.getChildren().addAll(badge, duration);

            // Title & Description
            Label title = new Label("Java Enterprise & Cloud Architecture");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

            Label description = new Label(
                "Master modern Java 21, JavaFX desktop engineering, Spring Boot microservices, " +
                "and cloud containerization with hands-on enterprise projects."
            );
            description.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

            // Price & Enrollment CTA
            HBox footer = new HBox(12);
            footer.setAlignment(Pos.CENTER_LEFT);

            VBox priceBox = new VBox(2);
            Label priceLabel = new Label("Tuition Fee");
            priceLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px;");
            Label priceVal = new Label("$1,250 USD");
            priceVal.setStyle("-fx-text-fill: #10B981; -fx-font-size: 16px; -fx-font-weight: bold;");
            priceBox.getChildren().addAll(priceLabel, priceVal);

            Button btnEnroll = new Button("Enroll Now");
            btnEnroll.setStyle(
                "-fx-background-color: #2563EB; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 18 8 18; " +
                "-fx-background-radius: 6px;"
            );
            HBox.setHgrow(priceBox, Priority.ALWAYS);

            Label feedback = new Label();
            feedback.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 11px;");

            btnEnroll.setOnAction(e -> {
                feedback.setText("Seat reserved for Java Enterprise!");
                btnEnroll.setText("Enrolled ✓");
                btnEnroll.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");
                System.out.println("[Practice 1] Candidate enrolled in Java Enterprise & Cloud.");
            });

            footer.getChildren().addAll(priceBox, btnEnroll);
            card.getChildren().addAll(badgeRow, title, description, footer, feedback);
            screen.getChildren().add(card);

            System.out.println("Product Card Component Configured Successfully.");
            Scene scene = new Scene(screen, 460, 420);
            primaryStage.setTitle("Practice 1 - Themed Course Card");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(CardApp.class, args);
    }
}
