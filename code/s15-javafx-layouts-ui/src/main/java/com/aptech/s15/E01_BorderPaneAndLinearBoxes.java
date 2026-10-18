package com.aptech.s15;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 15 - Example 1: BorderPane and Linear Box Layouts (HBox & VBox)
 *
 * Demonstrates:
 * 1. BorderPane 5-region distribution (Top, Bottom, Left, Right, Center).
 * 2. Center expansion: Center region claims all remaining window space.
 * 3. HBox and VBox spacing, padding (Insets), and alignment (Pos).
 * 4. Priority.ALWAYS: Flexible element growth on window resize.
 * 5. Combining nested boxes into a cohesive modern application frame.
 */
public class E01_BorderPaneAndLinearBoxes {

    public static class LayoutApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX BorderPane, HBox & VBox Architecture ===");

            BorderPane root = new BorderPane();

            // 1. TOP REGION: Application Header Bar (HBox)
            HBox header = new HBox(12);
            header.setPadding(new Insets(12, 20, 12, 20));
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-background-color: #1E293B; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;");

            Label brand = new Label("Aptech Campus Portal");
            brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

            TextField searchField = new TextField();
            searchField.setPromptText("Search modules, students, reports...");
            searchField.setStyle("-fx-background-color: #0F172A; -fx-text-fill: #F8FAFC; -fx-border-color: #475569; -fx-border-radius: 4px;");
            // Give search field flexible growth
            HBox.setHgrow(searchField, Priority.ALWAYS);

            Button btnProfile = new Button("Admin Profile");
            btnProfile.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

            header.getChildren().addAll(brand, searchField, btnProfile);
            root.setTop(header);

            // 2. LEFT REGION: Side Navigation (VBox)
            VBox sidebar = new VBox(8);
            sidebar.setPadding(new Insets(16, 12, 16, 12));
            sidebar.setPrefWidth(180);
            sidebar.setStyle("-fx-background-color: #0F172A; -fx-border-color: #334155; -fx-border-width: 0 1 0 0;");

            String[] navItems = {"Dashboard", "Students", "Curriculum", "Examinations", "Settings"};
            for (int i = 0; i < navItems.length; i++) {
                Button navBtn = new Button(navItems[i]);
                navBtn.setMaxWidth(Double.MAX_VALUE);
                navBtn.setAlignment(Pos.CENTER_LEFT);
                if (i == 0) {
                    navBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");
                } else {
                    navBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8;");
                }
                sidebar.getChildren().add(navBtn);
            }
            root.setLeft(sidebar);

            // 3. CENTER REGION: Work Area with Auto-Expansion
            VBox centerContent = new VBox(16);
            centerContent.setPadding(new Insets(24));
            centerContent.setStyle("-fx-background-color: #0B1120;");

            Label centerTitle = new Label("Active Academic Term Overview");
            centerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

            Label centerDesc = new Label(
                "BorderPane allocates center region all unclaimed horizontal and vertical space.\n" +
                "When the application window is resized, Top and Bottom maintain their preferred heights,\n" +
                "Left and Right retain their preferred widths, and Center expands dynamically."
            );
            centerDesc.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");

            centerContent.getChildren().addAll(centerTitle, centerDesc);
            root.setCenter(centerContent);

            // 4. BOTTOM REGION: System Status Bar (HBox)
            HBox statusBar = new HBox(16);
            statusBar.setPadding(new Insets(6, 16, 6, 16));
            statusBar.setAlignment(Pos.CENTER_LEFT);
            statusBar.setStyle("-fx-background-color: #1E293B; -fx-border-color: #334155; -fx-border-width: 1 0 0 0;");

            Label statusMsg = new Label("System Status: Online | Database: Connected (Localhost:3306)");
            statusMsg.setStyle("-fx-text-fill: #10B981; -fx-font-size: 11px;");
            Label versionMsg = new Label("v2.4.0-FX21");
            versionMsg.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

            HBox.setHgrow(statusMsg, Priority.ALWAYS);
            statusBar.getChildren().addAll(statusMsg, versionMsg);
            root.setBottom(statusBar);

            // Verification diagnostics
            System.out.println("Root Pane Type: " + root.getClass().getSimpleName());
            System.out.println("Top Region Child Count: " + header.getChildren().size());
            System.out.println("Left Region Width: " + sidebar.getPrefWidth() + "px");
            System.out.println("Center Region Node: " + root.getCenter().getClass().getSimpleName());
            System.out.println("Bottom Region: " + root.getBottom().getClass().getSimpleName());

            Scene scene = new Scene(root, 760, 480);
            primaryStage.setTitle("Session 15 - BorderPane & Linear Boxes");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(LayoutApp.class, args);
    }
}
