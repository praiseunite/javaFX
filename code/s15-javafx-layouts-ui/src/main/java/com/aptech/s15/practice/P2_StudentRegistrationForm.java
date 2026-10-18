package com.aptech.s15.practice;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Practice Exercise 2 (Medium): Student Registration Form with Validation
 *
 * Requirements:
 * - Build a structured multi-field registration form using GridPane.
 * - Collect Candidate Name, Email Address, Degree Specialization, and Study Level.
 * - Perform field verification (checks that inputs are non-blank).
 * - Render an interactive validation readout with student summary confirmation.
 */
public class P2_StudentRegistrationForm {

    public static class FormApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Practice 2: Student Registration Form with Validation ===");

            VBox root = new VBox(18);
            root.setPadding(new Insets(24));
            root.setStyle("-fx-background-color: #0F172A;");

            Label title = new Label("Candidate Admissions Processing Desk");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            GridPane grid = new GridPane();
            grid.setHgap(14);
            grid.setVgap(14);

            // Row 0: Full Name
            Label lblName = new Label("Student Name:");
            lblName.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            TextField txtName = new TextField("Ada Lovelace");
            txtName.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white; -fx-border-color: #475569;");
            GridPane.setHgrow(txtName, Priority.ALWAYS);
            grid.add(lblName, 0, 0);
            grid.add(txtName, 1, 0);

            // Row 1: Email
            Label lblEmail = new Label("Email Address:");
            lblEmail.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            TextField txtEmail = new TextField("ada.lovelace@aptech-edu.org");
            txtEmail.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white; -fx-border-color: #475569;");
            grid.add(lblEmail, 0, 1);
            grid.add(txtEmail, 1, 1);

            // Row 2: Specialization
            Label lblTrack = new Label("Program Track:");
            lblTrack.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            ComboBox<String> cmbTrack = new ComboBox<>(FXCollections.observableArrayList(
                "Software Engineering (ADSE)",
                "Network Security & CyberOps",
                "Data Science & Predictive Analytics"
            ));
            cmbTrack.setValue("Software Engineering (ADSE)");
            cmbTrack.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white;");
            grid.add(lblTrack, 0, 2);
            grid.add(cmbTrack, 1, 2);

            // Row 3: Study Level Radios
            Label lblLevel = new Label("Academic Level:");
            lblLevel.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");

            ToggleGroup levelGroup = new ToggleGroup();
            RadioButton rbDiploma = new RadioButton("Diploma");
            RadioButton rbHigher = new RadioButton("Higher Diploma");
            RadioButton rbDegree = new RadioButton("Degree Transfer");

            rbDiploma.setToggleGroup(levelGroup);
            rbHigher.setToggleGroup(levelGroup);
            rbDegree.setToggleGroup(levelGroup);
            rbHigher.setSelected(true);

            rbDiploma.setStyle("-fx-text-fill: #E2E8F0;");
            rbHigher.setStyle("-fx-text-fill: #E2E8F0;");
            rbDegree.setStyle("-fx-text-fill: #E2E8F0;");

            HBox radios = new HBox(12, rbDiploma, rbHigher, rbDegree);
            grid.add(lblLevel, 0, 3);
            grid.add(radios, 1, 3);

            // Row 4: Terms Checkbox
            CheckBox chkTerms = new CheckBox("I confirm that the applicant holds prerequisite O-Level credentials");
            chkTerms.setSelected(true);
            chkTerms.setStyle("-fx-text-fill: #94A3B8;");
            grid.add(chkTerms, 1, 4);

            // Row 5: Action Button
            Button btnSubmit = new Button("Validate & Register Candidate");
            btnSubmit.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20;");
            GridPane.setHalignment(btnSubmit, HPos.RIGHT);
            grid.add(btnSubmit, 1, 5);

            // Confirmation Box
            VBox summaryBox = new VBox(6);
            summaryBox.setPadding(new Insets(12));
            summaryBox.setStyle("-fx-background-color: #1E293B; -fx-border-color: #334155; -fx-border-radius: 6px;");

            Label summaryTitle = new Label("Registration Status: Ready for validation");
            summaryTitle.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold;");
            Label summaryDetail = new Label("Fill candidate credentials and press Register.");
            summaryDetail.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");

            summaryBox.getChildren().addAll(summaryTitle, summaryDetail);

            btnSubmit.setOnAction(e -> {
                String name = txtName.getText().trim();
                String email = txtEmail.getText().trim();
                if (name.isEmpty() || email.isEmpty()) {
                    summaryTitle.setText("Validation Error: Missing required fields!");
                    summaryTitle.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    summaryDetail.setText("Please enter both Candidate Name and Email Address.");
                } else if (!chkTerms.isSelected()) {
                    summaryTitle.setText("Validation Error: Verification check required!");
                    summaryTitle.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    summaryDetail.setText("Prerequisite credential confirmation box must be checked.");
                } else {
                    RadioButton selectedLevel = (RadioButton) levelGroup.getSelectedToggle();
                    String levelStr = selectedLevel != null ? selectedLevel.getText() : "Standard";
                    summaryTitle.setText("Candidate Successfully Registered ✓");
                    summaryTitle.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    summaryDetail.setText(
                        "Student: " + name + " (" + email + ")\n" +
                        "Program: " + cmbTrack.getValue() + " | Level: " + levelStr
                    );
                    System.out.println("[Practice 2] Successfully registered: " + name + " | " + cmbTrack.getValue());
                }
            });

            root.getChildren().addAll(title, grid, summaryBox);

            System.out.println("Student Registration Form Configured.");
            Scene scene = new Scene(root, 640, 480);
            primaryStage.setTitle("Practice 2 - Student Registration Form");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(FormApp.class, args);
    }
}
