package com.aptech.s15;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Example 6: JavaFX PieChart Data Analytics
 *
 * Demonstrates:
 * 1. PieChart architectural model: PieChart.Data(name, value).
 * 2. ObservableList<PieChart.Data> backing: Modifying collection updates the chart automatically.
 * 3. Configuration properties: setTitle(), setClockwise(), setLabelsVisible(), setLegendVisible().
 * 4. Dynamic runtime updates: Adding/modifying slices on button click.
 * 5. Calculating percentages across data slices for dashboard metrics.
 */
public class E06_PieChartAnalytics {

    public static class ChartApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX PieChart Analytics Architecture ===");

            VBox root = new VBox(16);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: #0F172A;");

            // 1. Initialize Observable Data Collection
            ObservableList<PieChart.Data> courseData = FXCollections.observableArrayList(
                new PieChart.Data("Cloud Computing & DevOps", 340),
                new PieChart.Data("Advanced Java & Spring Boot", 480),
                new PieChart.Data("Full-Stack Web Engineering", 520),
                new PieChart.Data("Data Science & AI", 290)
            );

            // 2. Instantiate and Configure PieChart
            PieChart pieChart = new PieChart(courseData);
            pieChart.setTitle("Aptech Enrollment Distribution by Discipline (2026)");
            pieChart.setClockwise(true);
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setPrefSize(560, 380);

            // 3. Interactive Data Controls (Add New Stream)
            HBox controls = new HBox(12);
            controls.setAlignment(Pos.CENTER);

            Button btnAddStream = new Button("Add CyberSecurity Stream");
            btnAddStream.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

            Button btnUpdateJava = new Button("Surge Java Enrollment (+150)");
            btnUpdateJava.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");

            Label statusLabel = new Label("Total Slices: " + courseData.size());
            statusLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

            btnAddStream.setOnAction(e -> {
                courseData.add(new PieChart.Data("CyberSecurity & Ethical Hacking", 210));
                statusLabel.setText("Total Slices: " + courseData.size() + " (CyberSecurity Added)");
                btnAddStream.setDisable(true);
            });

            btnUpdateJava.setOnAction(e -> {
                for (PieChart.Data d : courseData) {
                    if (d.getName().contains("Java")) {
                        d.setPieValue(d.getPieValue() + 150);
                        statusLabel.setText("Java count surged to: " + (int) d.getPieValue());
                        break;
                    }
                }
            });

            controls.getChildren().addAll(btnAddStream, btnUpdateJava, statusLabel);

            root.getChildren().addAll(pieChart, controls);

            // Calculate total and print breakdown
            double totalStudents = 0;
            for (PieChart.Data d : courseData) {
                totalStudents += d.getPieValue();
            }
            System.out.println("PieChart Title: " + pieChart.getTitle());
            System.out.println("Total Candidates Enrolled: " + (int) totalStudents);
            for (PieChart.Data d : courseData) {
                double pct = (d.getPieValue() / totalStudents) * 100.0;
                System.out.println(String.format(" - %s: %.0f students (%.1f%%)", d.getName(), d.getPieValue(), pct));
            }

            Scene scene = new Scene(root, 640, 480);
            primaryStage.setTitle("Session 15 - PieChart Analytics");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(ChartApp.class, args);
    }
}
