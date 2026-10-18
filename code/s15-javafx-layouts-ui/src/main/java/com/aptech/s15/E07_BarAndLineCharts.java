package com.aptech.s15;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Example 7: JavaFX BarChart and LineChart (XYChart Architecture)
 *
 * Demonstrates:
 * 1. XYChart hierarchy: CategoryAxis (discrete X domain) & NumberAxis (continuous Y range).
 * 2. BarChart<String, Number>: Multi-series comparison across academic quarters.
 * 3. LineChart<String, Number>: Continuous performance and graduation trend analysis.
 * 4. XYChart.Series and XYChart.Data structures.
 * 5. Formatting bar gap, category gap, and symbol indicators.
 */
public class E07_BarAndLineCharts {

    public static class XYChartApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX XYChart: BarChart & LineChart Architecture ===");

            VBox root = new VBox(20);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #0F172A;");

            Label title = new Label("Academic Performance & Trajectory Analytics (2025 - 2026)");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            HBox chartsRow = new HBox(16);
            chartsRow.setAlignment(Pos.CENTER);

            // -------------------------------------------------------------
            // 1. BARCHART: Enrollment per Quarter (2025 vs 2026)
            // -------------------------------------------------------------
            CategoryAxis barXAxis = new CategoryAxis();
            barXAxis.setLabel("Academic Quarter");

            NumberAxis barYAxis = new NumberAxis("Enrolled Candidates", 0, 600, 100);

            BarChart<String, Number> barChart = new BarChart<>(barXAxis, barYAxis);
            barChart.setTitle("Quarterly Enrollment Comparison");
            barChart.setCategoryGap(20);
            barChart.setBarGap(6);
            barChart.setPrefSize(420, 360);

            // Series 1: 2025 Cohort
            XYChart.Series<String, Number> series2025 = new XYChart.Series<>();
            series2025.setName("Cohort 2025");
            series2025.getData().add(new XYChart.Data<>("Q1", 280));
            series2025.getData().add(new XYChart.Data<>("Q2", 340));
            series2025.getData().add(new XYChart.Data<>("Q3", 410));
            series2025.getData().add(new XYChart.Data<>("Q4", 450));

            // Series 2: 2026 Cohort
            XYChart.Series<String, Number> series2026 = new XYChart.Series<>();
            series2026.setName("Cohort 2026");
            series2026.getData().add(new XYChart.Data<>("Q1", 360));
            series2026.getData().add(new XYChart.Data<>("Q2", 420));
            series2026.getData().add(new XYChart.Data<>("Q3", 510));
            series2026.getData().add(new XYChart.Data<>("Q4", 580));

            barChart.getData().addAll(FXCollections.observableArrayList(series2025, series2026));

            // -------------------------------------------------------------
            // 2. LINECHART: Examination Pass Rates over Time
            // -------------------------------------------------------------
            CategoryAxis lineXAxis = new CategoryAxis();
            lineXAxis.setLabel("Assessment Cycle");

            NumberAxis lineYAxis = new NumberAxis("Average Pass Rate (%)", 60, 100, 10);

            LineChart<String, Number> lineChart = new LineChart<>(lineXAxis, lineYAxis);
            lineChart.setTitle("Assessment Mastery Trajectory");
            lineChart.setPrefSize(420, 360);

            XYChart.Series<String, Number> passRateSeries = new XYChart.Series<>();
            passRateSeries.setName("Java Certification Pass %");
            passRateSeries.getData().add(new XYChart.Data<>("Cycle 1", 74));
            passRateSeries.getData().add(new XYChart.Data<>("Cycle 2", 82));
            passRateSeries.getData().add(new XYChart.Data<>("Cycle 3", 89));
            passRateSeries.getData().add(new XYChart.Data<>("Cycle 4", 94));

            lineChart.getData().add(passRateSeries);

            chartsRow.getChildren().addAll(barChart, lineChart);
            root.getChildren().addAll(title, chartsRow);

            // Diagnostics readout
            System.out.println("BarChart Title: " + barChart.getTitle());
            System.out.println("BarChart Series Count: " + barChart.getData().size());
            System.out.println("BarChart X-Axis Label: " + barXAxis.getLabel());
            System.out.println("LineChart Title: " + lineChart.getTitle());
            System.out.println("LineChart Final Cycle Pass Rate: " + passRateSeries.getData().get(3).getYValue() + "%");

            Scene scene = new Scene(root, 900, 480);
            primaryStage.setTitle("Session 15 - BarChart & LineChart Analytics");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(XYChartApp.class, args);
    }
}
