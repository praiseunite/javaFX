package com.aptech.s13.practice;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Practice Exercise 3 (Challenge): Dynamic Scene Graph Manipulation
 *
 * Demonstrates runtime Scene Graph modification:
 * Appending course item cards dynamically to a layout container,
 * updating an observable badge counter, and clearing nodes safely.
 */
public class P3_DynamicSceneGraph {

    public static class App extends Application {
        private final IntegerProperty courseCount = new SimpleIntegerProperty(0);

        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Practice 3: Dynamic Scene Graph Modification ===");

            Label titleLabel = new Label("Course Enrollment Manager");
            Label countLabel = new Label("Enrolled Courses: 0");

            // Reactively update count label whenever courseCount property changes
            courseCount.addListener((obs, oldVal, newVal) -> {
                countLabel.setText("Enrolled Courses: " + newVal);
                System.out.println("  [Scene Graph Update] Enrolled course count updated to: " + newVal);
            });

            VBox courseListBox = new VBox(8);
            courseListBox.setId("course-list");

            Button addCourseBtn = new Button("Add Course");
            Button clearCoursesBtn = new Button("Clear All");

            String[] sampleCourses = {
                "CP-101: Core Java Programming",
                "DB-201: Relational Databases & JDBC",
                "FX-301: JavaFX GUI & UI Architecture",
                "AI-401: AI-Assisted Development & Testing"
            };

            addCourseBtn.setOnAction(e -> {
                int current = courseCount.get();
                if (current < sampleCourses.length) {
                    String courseTitle = sampleCourses[current];
                    Label courseItem = new Label((current + 1) + ". " + courseTitle);
                    courseItem.setId("course-" + (current + 1));
                    courseItem.setStyle("-fx-padding: 6px; -fx-background-color: #f1f5f9;");
                    courseListBox.getChildren().add(courseItem);
                    courseCount.set(current + 1);
                    System.out.println("  [Node Added] Appended child: " + courseTitle);
                } else {
                    System.out.println("  [Notice] All sample courses already added.");
                }
            });

            clearCoursesBtn.setOnAction(e -> {
                courseListBox.getChildren().clear();
                courseCount.set(0);
                System.out.println("  [Nodes Cleared] Reset Scene Graph course list.");
            });

            HBox buttonBar = new HBox(10, addCourseBtn, clearCoursesBtn);
            buttonBar.setAlignment(Pos.CENTER_LEFT);

            VBox root = new VBox(14, titleLabel, countLabel, buttonBar, courseListBox);
            root.setAlignment(Pos.TOP_LEFT);

            Scene scene = new Scene(root, 480, 320);
            primaryStage.setTitle("Dynamic Scene Graph Manager");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Simulate user clicking Add Course twice
            System.out.println("\nSimulating user adding two courses:");
            addCourseBtn.fire();
            addCourseBtn.fire();
            System.out.println("Total children in courseListBox: " + courseListBox.getChildren().size());
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
