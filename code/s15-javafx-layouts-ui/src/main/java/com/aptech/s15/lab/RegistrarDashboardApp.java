package com.aptech.s15.lab;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;

/**
 * Session 15 Guided Lab: Aptech Enterprise Registrar Dashboard & Analytics Portal
 *
 * Architecture:
 * - Root: BorderPane (5-zone corporate desktop application frame)
 * - Top: Header Bar with branding, search field (Priority.ALWAYS), user badge
 * - Left: Navigation sidebar with active state markers
 * - Center: Executive Dashboard featuring:
 *     1. KPI Stat Cards (Total Students, Active Courses, Pass Rate)
 *     2. SplitPane layout: Left: GridPane Rapid Registration Form | Right: PieChart & BarChart Visualizations
 * - Bottom: Real-time system telemetry status bar
 * - CSS Skinning: External dashboard.css styling with dark theme, hover accents, and modern typography.
 */
public class RegistrarDashboardApp {

    public static class DashboardFxApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=======================================================");
            System.out.println(" APTECH ENTERPRISE REGISTRAR DASHBOARD & ANALYTICS");
            System.out.println(" Session 15 Capstone Guided Laboratory Architecture");
            System.out.println("=======================================================");

            BorderPane root = new BorderPane();
            root.getStyleClass().add("root");

            // -------------------------------------------------------------
            // 1. TOP ZONE: Header Navigation Bar
            // -------------------------------------------------------------
            HBox header = new HBox(16);
            header.getStyleClass().add("header-bar");

            Label brand = new Label("APTECH REGISTRAR PORTAL");
            brand.getStyleClass().add("brand-title");

            Label badge = new Label("ENTERPRISE v2.4");
            badge.getStyleClass().add("brand-badge");

            TextField searchField = new TextField();
            searchField.setPromptText("Search students, enrollments, examinations...");
            searchField.getStyleClass().add("text-field");
            HBox.setHgrow(searchField, Priority.ALWAYS);

            Button btnProfile = new Button("Academic Officer (Admin)");
            btnProfile.getStyleClass().add("btn-secondary");

            header.getChildren().addAll(brand, badge, searchField, btnProfile);
            root.setTop(header);

            // -------------------------------------------------------------
            // 2. LEFT ZONE: Side Navigation Menu
            // -------------------------------------------------------------
            VBox sidebar = new VBox(8);
            sidebar.getStyleClass().add("sidebar");
            sidebar.setPrefWidth(210);

            String[] menuItems = {
                "Executive Overview",
                "Enrollment Desk",
                "Academic Modules",
                "Examination Board",
                "Graduation Analytics",
                "System Settings"
            };

            for (int i = 0; i < menuItems.length; i++) {
                Button navBtn = new Button(menuItems[i]);
                navBtn.setMaxWidth(Double.MAX_VALUE);
                if (i == 0) {
                    navBtn.getStyleClass().addAll("nav-button", "nav-button-active");
                } else {
                    navBtn.getStyleClass().add("nav-button");
                }
                sidebar.getChildren().add(navBtn);
            }
            root.setLeft(sidebar);

            // -------------------------------------------------------------
            // 3. CENTER ZONE: KPI Cards, Registration Form & Analytics
            // -------------------------------------------------------------
            VBox centerArea = new VBox(20);
            centerArea.setPadding(new Insets(20));

            // 3A. KPI Overview Row (3 Metric Cards)
            HBox kpiRow = new HBox(16);

            VBox card1 = createKpiCard("TOTAL CANDIDATES", "1,842", "+14.2% YoY Growth");
            VBox card2 = createKpiCard("ACTIVE DIPLOMA TRACKS", "28 Tracks", "4 Centers Online");
            VBox card3 = createKpiCard("CERTIFICATION PASS RATE", "94.6%", "+3.1% Target Met");

            HBox.setHgrow(card1, Priority.ALWAYS);
            HBox.setHgrow(card2, Priority.ALWAYS);
            HBox.setHgrow(card3, Priority.ALWAYS);
            kpiRow.getChildren().addAll(card1, card2, card3);

            // 3B. Main Work Area: Left Form + Right Analytics
            HBox workspace = new HBox(18);

            // Left Panel: Student Fast Registration Form (GridPane)
            VBox formCard = new VBox(12);
            formCard.getStyleClass().add("chart-container");
            formCard.setPrefWidth(420);

            Label formTitle = new Label("Rapid Student Enrollment");
            formTitle.getStyleClass().add("chart-title");

            GridPane formGrid = new GridPane();
            formGrid.setHgap(10);
            formGrid.setVgap(12);

            // Row 0: Full Name
            Label lblName = new Label("Full Name:");
            lblName.setStyle("-fx-text-fill: #94A3B8;");
            TextField txtName = new TextField();
            txtName.setPromptText("First and Last Legal Name");
            txtName.getStyleClass().add("text-field");
            GridPane.setHgrow(txtName, Priority.ALWAYS);
            formGrid.add(lblName, 0, 0);
            formGrid.add(txtName, 1, 0);

            // Row 1: Course Track
            Label lblTrack = new Label("Track:");
            lblTrack.setStyle("-fx-text-fill: #94A3B8;");
            ComboBox<String> cmbTrack = new ComboBox<>(FXCollections.observableArrayList(
                "ADSE - Advanced Java & Cloud",
                "CPISM - Systems Management",
                "DISM - Database Systems",
                "AI & Machine Learning"
            ));
            cmbTrack.setValue("ADSE - Advanced Java & Cloud");
            cmbTrack.getStyleClass().add("combo-box");
            formGrid.add(lblTrack, 0, 1);
            formGrid.add(cmbTrack, 1, 1);

            // Row 2: Study Mode Radio Buttons
            Label lblMode = new Label("Study Mode:");
            lblMode.setStyle("-fx-text-fill: #94A3B8;");
            ToggleGroup modeGroup = new ToggleGroup();
            RadioButton rbDay = new RadioButton("Full-Time");
            RadioButton rbEve = new RadioButton("Weekend");
            rbDay.setToggleGroup(modeGroup);
            rbEve.setToggleGroup(modeGroup);
            rbDay.setSelected(true);
            rbDay.setStyle("-fx-text-fill: #E2E8F0;");
            rbEve.setStyle("-fx-text-fill: #E2E8F0;");
            HBox modes = new HBox(12, rbDay, rbEve);
            formGrid.add(lblMode, 0, 2);
            formGrid.add(modes, 1, 2);

            // Row 3: Admission Date
            Label lblDate = new Label("Start Date:");
            lblDate.setStyle("-fx-text-fill: #94A3B8;");
            DatePicker datePicker = new DatePicker(LocalDate.of(2026, 11, 2));
            datePicker.getStyleClass().add("date-picker");
            formGrid.add(lblDate, 0, 3);
            formGrid.add(datePicker, 1, 3);

            // Row 4: Submit Button
            Button btnEnroll = new Button("Commit Enrollment");
            btnEnroll.getStyleClass().add("btn-primary");
            GridPane.setHalignment(btnEnroll, HPos.RIGHT);
            formGrid.add(btnEnroll, 1, 4);

            Label formFeedback = new Label("All fields ready for data entry.");
            formFeedback.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 11px;");

            btnEnroll.setOnAction(e -> {
                String name = txtName.getText().trim().isEmpty() ? "Candidate" : txtName.getText().trim();
                formFeedback.setText("Enrolled " + name + " in " + cmbTrack.getValue());
                System.out.println("[Enrollment Desk] Committed record: " + name + " (" + cmbTrack.getValue() + ")");
            });

            formCard.getChildren().addAll(formTitle, formGrid, formFeedback);

            // Right Panel: PieChart + BarChart Data Visualizations
            VBox analyticsCard = new VBox(14);
            analyticsCard.getStyleClass().add("chart-container");
            HBox.setHgrow(analyticsCard, Priority.ALWAYS);

            Label analyticsTitle = new Label("Discipline Distribution & Quarterly Performance");
            analyticsTitle.getStyleClass().add("chart-title");

            HBox chartsRow = new HBox(14);
            chartsRow.setAlignment(Pos.CENTER);

            // PieChart
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Java / Cloud", 620),
                new PieChart.Data("Web Engineering", 480),
                new PieChart.Data("Data / AI", 410),
                new PieChart.Data("CyberSecurity", 332)
            );
            PieChart pieChart = new PieChart(pieData);
            pieChart.setTitle("Track Distribution");
            pieChart.setPrefSize(260, 220);

            // BarChart
            CategoryAxis bX = new CategoryAxis();
            bX.setLabel("Quarter");
            NumberAxis bY = new NumberAxis("Pass %", 50, 100, 10);
            BarChart<String, Number> barChart = new BarChart<>(bX, bY);
            barChart.setTitle("Pass Rate %");
            barChart.setPrefSize(280, 220);

            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName("2026 Cohort");
            s.getData().add(new XYChart.Data<>("Q1", 86));
            s.getData().add(new XYChart.Data<>("Q2", 91));
            s.getData().add(new XYChart.Data<>("Q3", 94));
            barChart.getData().add(s);

            chartsRow.getChildren().addAll(pieChart, barChart);
            analyticsCard.getChildren().addAll(analyticsTitle, chartsRow);

            workspace.getChildren().addAll(formCard, analyticsCard);
            centerArea.getChildren().addAll(kpiRow, workspace);
            root.setCenter(centerArea);

            // -------------------------------------------------------------
            // 4. BOTTOM ZONE: Real-time Telemetry Status Bar
            // -------------------------------------------------------------
            HBox statusBar = new HBox(14);
            statusBar.setPadding(new Insets(6, 16, 6, 16));
            statusBar.setStyle("-fx-background-color: #1E293B; -fx-border-color: #334155; -fx-border-width: 1 0 0 0;");

            Label statusIndicator = new Label("Service Engine: RUNNING (Socket 8080) | DB: Connected");
            statusIndicator.setStyle("-fx-text-fill: #10B981; -fx-font-size: 11px;");
            HBox.setHgrow(statusIndicator, Priority.ALWAYS);

            Label nodeInfo = new Label("Cluster Node: Lagos-HQ-01 | Latency: 4ms");
            nodeInfo.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

            statusBar.getChildren().addAll(statusIndicator, nodeInfo);
            root.setBottom(statusBar);

            Scene scene = new Scene(root, 1080, 680);

            URL cssUrl = getClass().getResource("/dashboard.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Applied Portal CSS Stylesheet: " + cssUrl.toExternalForm());
            }

            // Print system startup telemetry
            System.out.println("Dashboard Window Initialized: 1080x680 px");
            System.out.println("KPI Cards Rendered: " + kpiRow.getChildren().size());
            System.out.println("Navigation Sidebar Elements: " + sidebar.getChildren().size());
            System.out.println("Analytics PieChart Slices: " + pieData.size());
            System.out.println("Quarterly BarChart Data Points: " + s.getData().size());
            System.out.println("Status: Ready for Registrar Operative Tasks.");

            primaryStage.setTitle("Aptech Enterprise Registrar Portal - Session 15 Capstone");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        private VBox createKpiCard(String title, String value, String delta) {
            VBox card = new VBox(6);
            card.getStyleClass().add("kpi-card");

            Label lblTitle = new Label(title);
            lblTitle.getStyleClass().add("kpi-label");

            Label lblVal = new Label(value);
            lblVal.getStyleClass().add("kpi-number");

            Label lblDelta = new Label(delta);
            lblDelta.getStyleClass().add("kpi-delta");

            card.getChildren().addAll(lblTitle, lblVal, lblDelta);
            return card;
        }
    }

    public static void main(String[] args) {
        Application.launch(DashboardFxApp.class, args);
    }
}
