package com.aptech.s15;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Session 15 - Example 3: FlowPane, TilePane, StackPane & AnchorPane
 *
 * Demonstrates:
 * 1. FlowPane: Wrapping consecutive children horizontally when boundary is reached.
 * 2. TilePane: Enforcing uniform rectangular cells for all children regardless of prefSize.
 * 3. StackPane: Layering nodes in Z-order (back to front), overlaying text on badges.
 * 4. AnchorPane: Pinning controls to specific pixel offsets from container edges.
 */
public class E03_FlowTileStackAnchorPanes {

    public static class MultiPaneApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX FlowPane, TilePane, StackPane & AnchorPane ===");

            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setStyle("-fx-background-color: #0F172A;");

            // 1. FLOWPANE: Tag Cloud wrapping buttons
            Label flowTitle = new Label("1. FlowPane (Tag Cloud Wrapping)");
            flowTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 8, 8);
            flowPane.setPadding(new Insets(10));
            flowPane.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 6px;");

            String[] tags = {"JavaFX", "CSS Skinning", "GridPane", "Charts", "Enterprise UI", "FXML", "Responsive", "Prism D3D"};
            for (String tag : tags) {
                Button tagBtn = new Button("#" + tag);
                tagBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: #E2E8F0; -fx-font-size: 11px;");
                flowPane.getChildren().add(tagBtn);
            }

            // 2. TILEPANE: Uniform metric tiles
            Label tileTitle = new Label("2. TilePane (Uniform Grid Cells)");
            tileTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            TilePane tilePane = new TilePane(10, 10);
            tilePane.setPrefColumns(3);
            tilePane.setPadding(new Insets(10));
            tilePane.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 6px;");

            for (int i = 1; i <= 3; i++) {
                VBox card = new VBox(4);
                card.setPadding(new Insets(8));
                card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-background-color: #0F172A; -fx-border-color: #475569; -fx-border-radius: 4px;");
                Label val = new Label("KPI " + (i * 24));
                val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
                Label desc = new Label("Metric Tile " + i);
                desc.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
                card.getChildren().addAll(val, desc);
                tilePane.getChildren().add(card);
            }

            // 3. STACKPANE: Layering badge over background
            Label stackTitle = new Label("3. StackPane (Z-Order Layering)");
            stackTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            StackPane stackPane = new StackPane();
            Rectangle bgBox = new Rectangle(280, 50);
            bgBox.setFill(Color.DODGERBLUE);
            bgBox.setArcWidth(10);
            bgBox.setArcHeight(10);

            Label badgeText = new Label("StackPane: Background Shape + Text Layered");
            badgeText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            stackPane.getChildren().addAll(bgBox, badgeText);

            // 4. ANCHORPANE: Docked corner controls
            Label anchorTitle = new Label("4. AnchorPane (Edge-Pinned Docking)");
            anchorTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefHeight(60);
            anchorPane.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 6px;");

            Button pinTopLeft = new Button("Pinned Top-Left");
            pinTopLeft.setStyle("-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-size: 11px;");
            AnchorPane.setTopAnchor(pinTopLeft, 10.0);
            AnchorPane.setLeftAnchor(pinTopLeft, 10.0);

            Button pinBottomRight = new Button("Pinned Bottom-Right");
            pinBottomRight.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 11px;");
            AnchorPane.setBottomAnchor(pinBottomRight, 10.0);
            AnchorPane.setRightAnchor(pinBottomRight, 10.0);

            anchorPane.getChildren().addAll(pinTopLeft, pinBottomRight);

            // Assemble into main layout
            mainContainer.getChildren().addAll(
                flowTitle, flowPane,
                tileTitle, tilePane,
                stackTitle, stackPane,
                anchorTitle, anchorPane
            );

            // Print diagnostics
            System.out.println("FlowPane Children Count: " + flowPane.getChildren().size());
            System.out.println("TilePane Columns: " + tilePane.getPrefColumns());
            System.out.println("StackPane Layers Count: " + stackPane.getChildren().size());
            System.out.println("AnchorPane TopAnchor: " + AnchorPane.getTopAnchor(pinTopLeft));
            System.out.println("AnchorPane RightAnchor: " + AnchorPane.getRightAnchor(pinBottomRight));

            Scene scene = new Scene(mainContainer, 640, 560);
            primaryStage.setTitle("Session 15 - Flow, Tile, Stack & Anchor Panes");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(MultiPaneApp.class, args);
    }
}
