package com.aptech.s15;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Session 15 - Example 2: GridPane 2D Matrix Form Layout
 *
 * Demonstrates:
 * 1. Cell coordinate system: add(Node, columnIndex, rowIndex).
 * 2. Column spans and row spans for spanning wide fields across columns.
 * 3. Horizontal and vertical gaps (hgap, vgap) and outer padding.
 * 4. Priority.ALWAYS applied to columns to create responsive input fields.
 * 5. Alignment of individual controls within cells (HPos.RIGHT, Pos.CENTER).
 */
public class E02_GridPaneFormLayout {

    public static class FormApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX GridPane Form Architecture ===");

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(24));
            grid.setHgap(14);
            grid.setVgap(14);
            grid.setAlignment(Pos.CENTER);
            grid.setStyle("-fx-background-color: #0F172A;");

            // Row 0: Form Header spanning both columns
            Label header = new Label("Student Registration Record");
            header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");
            // Span 2 columns, 1 row: add(node, col, row, colspan, rowspan)
            grid.add(header, 0, 0, 2, 1);
            GridPane.setHalignment(header, HPos.CENTER);

            // Row 1: Full Name
            Label lblName = new Label("Full Legal Name:");
            lblName.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            TextField txtName = new TextField();
            txtName.setPromptText("e.g. John Doe");
            txtName.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white; -fx-border-color: #475569;");
            GridPane.setHgrow(txtName, Priority.ALWAYS);
            grid.add(lblName, 0, 1);
            grid.add(txtName, 1, 1);

            // Row 2: Student ID (Roll No)
            Label lblId = new Label("Aptech Roll No:");
            lblId.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            TextField txtId = new TextField();
            txtId.setPromptText("APT-2026-9042");
            txtId.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white; -fx-border-color: #475569;");
            grid.add(lblId, 0, 2);
            grid.add(txtId, 1, 2);

            // Row 3: Security Passcode
            Label lblPass = new Label("Portal Passcode:");
            lblPass.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
            TextField txtPass = new TextField();
            txtPass.setPromptText("Enter secure pin");
            txtPass.setStyle("-fx-background-color: #1E293B; -fx-text-fill: white; -fx-border-color: #475569;");
            grid.add(lblPass, 0, 3);
            grid.add(txtPass, 1, 3);

            // Row 4: Submit Button aligned to the right
            Button btnSave = new Button("Register Student");
            btnSave.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20;");
            grid.add(btnSave, 1, 4);
            GridPane.setHalignment(btnSave, HPos.RIGHT);

            // Diagnostics output
            System.out.println("GridPane HGap: " + grid.getHgap() + "px | VGap: " + grid.getVgap() + "px");
            System.out.println("Grid Children Count: " + grid.getChildren().size());
            System.out.println("Header ColSpan: " + GridPane.getColumnSpan(header));
            System.out.println("Button Halignment: " + GridPane.getHalignment(btnSave));

            Scene scene = new Scene(grid, 520, 320);
            primaryStage.setTitle("Session 15 - GridPane Form Layout");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(FormApp.class, args);
    }
}
