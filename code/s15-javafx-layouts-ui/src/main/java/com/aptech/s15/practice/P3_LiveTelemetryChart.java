package com.aptech.s15.practice;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Practice Exercise 3 (Challenge): Live Telemetry Dashboard & Dynamic Charts
 *
 * Requirements:
 * - Build a multi-chart telemetry center with LineChart (server memory & CPU utilization)
 *   and PieChart (storage allocation).
 * - Implement dynamic data appending: Clicking "Simulate Live Telemetry Tick" pushes new
 *   metrics to the line chart series and dynamically adjusts pie slices.
 * - Manage category axes, numeric ranges, and live diagnostic readouts.
 */
public class P3_LiveTelemetryChart {

    public static class TelemetryApp extends Application {
        private int tickCount = 4;

        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Practice 3: Live Telemetry Dashboard & Dynamic Charts ===");

            VBox root = new VBox(18);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #0F172A;");

            Label header = new Label("Campus Datacenter Live Telemetry Stream");
            header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            HBox chartsBox = new HBox(16);
            chartsBox.setAlignment(Pos.CENTER);

            // 1. LineChart: CPU & Memory Load over Time
            CategoryAxis timeAxis = new CategoryAxis();
            timeAxis.setLabel("Clock Tick (Seconds)");

            NumberAxis loadAxis = new NumberAxis("Utilization (%)", 0, 100, 10);

            LineChart<String, Number> telemetryChart = new LineChart<>(timeAxis, loadAxis);
            telemetryChart.setTitle("Cluster CPU Utilization");
            telemetryChart.setPrefSize(440, 320);

            XYChart.Series<String, Number> cpuSeries = new XYChart.Series<>();
            cpuSeries.setName("Node-Alpha CPU %");
            cpuSeries.getData().add(new XYChart.Data<>("10:00:00", 38));
            cpuSeries.getData().add(new XYChart.Data<>("10:00:05", 44));
            cpuSeries.getData().add(new XYChart.Data<>("10:00:10", 62));
            cpuSeries.getData().add(new XYChart.Data<>("10:00:15", 58));

            telemetryChart.getData().add(cpuSeries);

            // 2. PieChart: Datacenter Resource Distribution
            ObservableList<PieChart.Data> resourceData = FXCollections.observableArrayList(
                new PieChart.Data("Active JVM Heaps", 45),
                new PieChart.Data("Database Buffer Pool", 30),
                new PieChart.Data("System OS Reserved", 25)
            );

            PieChart resourcePie = new PieChart(resourceData);
            resourcePie.setTitle("Memory Allocation");
            resourcePie.setPrefSize(380, 320);

            chartsBox.getChildren().addAll(telemetryChart, resourcePie);

            // 3. Control Panel: Push Live Telemetry Tick
            HBox controlBar = new HBox(14);
            controlBar.setAlignment(Pos.CENTER_LEFT);
            controlBar.setPadding(new Insets(10, 14, 10, 14));
            controlBar.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8px;");

            Button btnTick = new Button("⚡ Simulate Telemetry Pulse");
            btnTick.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16;");

            Label statusLabel = new Label("Telemetry status: Listening on UDP:9020 | Ticks logged: 4");
            statusLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 11px;");
            HBox.setHgrow(statusLabel, Priority.ALWAYS);

            btnTick.setOnAction(e -> {
                tickCount++;
                int simulatedLoad = 40 + (int) (Math.random() * 45);
                String timestamp = String.format("10:00:%02d", tickCount * 5);
                cpuSeries.getData().add(new XYChart.Data<>(timestamp, simulatedLoad));

                // Update pie slices
                resourceData.get(0).setPieValue(simulatedLoad);

                statusLabel.setText(String.format("Logged pulse [%s]: CPU load %d%% | Total ticks: %d", timestamp, simulatedLoad, tickCount));
                System.out.println("[Telemetry Tick] Appended " + timestamp + " -> " + simulatedLoad + "%");
            });

            controlBar.getChildren().addAll(btnTick, statusLabel);

            root.getChildren().addAll(header, chartsBox, controlBar);

            System.out.println("Live Telemetry Dashboard configured with 2 chart visualizers.");
            Scene scene = new Scene(root, 880, 480);
            primaryStage.setTitle("Practice 3 - Live Telemetry Chart");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(TelemetryApp.class, args);
    }
}
