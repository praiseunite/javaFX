package com.aptech.s15;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Session 15 - Example 5: JavaFX UI Controls and Observable Property Binding
 *
 * Demonstrates:
 * 1. Essential Form Controls: ComboBox, RadioButton, ToggleGroup, CheckBox, DatePicker.
 * 2. Range and Progress Controls: Slider and ProgressBar synchronization.
 * 3. Observable Properties: Binding slider value to progress bar and real-time labels.
 * 4. Mutual exclusion with ToggleGroup for radio buttons.
 * 5. Action event dispatching and form validation readout.
 */
public class E05_UiControlsAndDataBinding {

    public static class ControlsApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX UI Controls & Reactive Data Binding ===");

            VBox root = new VBox(16);
            root.setPadding(new Insets(24));
            root.setStyle("-fx-background-color: #0F172A;");

            Label title = new Label("Student Enrollment & Coursework Configurator");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            GridPane form = new GridPane();
            form.setHgap(12);
            form.setVgap(12);

            // 1. Module Selector: ComboBox
            Label lblTrack = new Label("Specialization Track:");
            lblTrack.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");

            ComboBox<String> cmbTrack = new ComboBox<>(FXCollections.observableArrayList(
                "ADSE - Advanced Java & Cloud Architecture",
                "CPISM - Information Systems Management",
                "DISM - Database Systems & Analytics",
                "ACCP - Enterprise Application Engineering"
            ));
            cmbTrack.setValue("ADSE - Advanced Java & Cloud Architecture");
            cmbTrack.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white;");
            form.add(lblTrack, 0, 0);
            form.add(cmbTrack, 1, 0);

            // 2. Study Mode: RadioButtons in a ToggleGroup
            Label lblMode = new Label("Study Mode:");
            lblMode.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");

            ToggleGroup modeGroup = new ToggleGroup();
            RadioButton rbFullTime = new RadioButton("Full-Time On-Campus");
            RadioButton rbPartTime = new RadioButton("Part-Time Weekend");
            RadioButton rbOnline = new RadioButton("Virtual Distance Learning");

            rbFullTime.setToggleGroup(modeGroup);
            rbPartTime.setToggleGroup(modeGroup);
            rbOnline.setToggleGroup(modeGroup);
            rbFullTime.setSelected(true);

            rbFullTime.setStyle("-fx-text-fill: #E2E8F0;");
            rbPartTime.setStyle("-fx-text-fill: #E2E8F0;");
            rbOnline.setStyle("-fx-text-fill: #E2E8F0;");

            HBox radioBox = new HBox(12, rbFullTime, rbPartTime, rbOnline);
            form.add(lblMode, 0, 1);
            form.add(radioBox, 1, 1);

            // 3. Admission Date: DatePicker
            Label lblDate = new Label("Commencement Date:");
            lblDate.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            DatePicker datePicker = new DatePicker(LocalDate.of(2026, 11, 1));
            datePicker.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white;");
            form.add(lblDate, 0, 2);
            form.add(datePicker, 1, 2);

            // 4. Scholarship Level: Slider & ProgressBar synchronization
            Label lblScholarship = new Label("Scholarship Allocation:");
            lblScholarship.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");

            Slider slider = new Slider(0, 100, 25);
            slider.setPrefWidth(200);

            ProgressBar progressBar = new ProgressBar(0.25);
            progressBar.setPrefWidth(200);
            progressBar.setStyle("-fx-accent: #10B981;");

            Label lblPercent = new Label("25% Aid");
            lblPercent.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");

            // Reactive property listener connecting Slider to ProgressBar
            slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                double pct = newVal.doubleValue() / 100.0;
                progressBar.setProgress(pct);
                lblPercent.setText(String.format("%.0f%% Aid", newVal.doubleValue()));
            });

            HBox sliderBox = new HBox(12, slider, progressBar, lblPercent);
            sliderBox.setAlignment(Pos.CENTER_LEFT);
            form.add(lblScholarship, 0, 3);
            form.add(sliderBox, 1, 3);

            // 5. Terms CheckBox
            CheckBox chkTerms = new CheckBox("Candidate has submitted verified identity documents");
            chkTerms.setSelected(true);
            chkTerms.setStyle("-fx-text-fill: #94A3B8;");
            form.add(chkTerms, 1, 4);

            // 6. Action Button & Feedback Label
            Button btnSubmit = new Button("Validate & Commit Enrollment");
            btnSubmit.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20;");

            Label feedback = new Label("Ready to record student enrollment.");
            feedback.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 12px;");

            btnSubmit.setOnAction(e -> {
                feedback.setText("Recorded: " + cmbTrack.getValue() + " | Scholarship: " + lblPercent.getText());
                System.out.println("[Action] Form committed: " + cmbTrack.getValue() + " | Aid: " + slider.getValue());
            });

            root.getChildren().addAll(title, form, btnSubmit, feedback);

            // Print diagnostics
            System.out.println("Track Selected: " + cmbTrack.getValue());
            System.out.println("Selected Toggle: " + ((RadioButton) modeGroup.getSelectedToggle()).getText());
            System.out.println("Admission Date: " + datePicker.getValue());
            System.out.println("Initial Slider Value: " + slider.getValue() + "%");
            System.out.println("ProgressBar Initial: " + progressBar.getProgress());

            Scene scene = new Scene(root, 720, 420);
            primaryStage.setTitle("Session 15 - UI Controls & Binding");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(ControlsApp.class, args);
    }
}
