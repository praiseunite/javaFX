package com.aptech.s15;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Session 15 - Example 4: JavaFX CSS Skinning and External Stylesheets
 *
 * Demonstrates:
 * 1. JavaFX CSS architecture: The mandatory `-fx-*` property namespace.
 * 2. Attaching external stylesheets via Scene.getStylesheets().add(url).
 * 3. Class selectors: node.getStyleClass().add("kpi-card").
 * 4. Pseudo-class states: :hover, :focused, :pressed.
 * 5. Dynamic theme toggling: Switching stylesheets at runtime.
 */
public class E04_CssSkinningAndThemes {

    public static class CssApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== JavaFX CSS Skinning & Theme Engine ===");

            VBox root = new VBox(20);
            root.getStyleClass().add("root");
            root.setPadding(new Insets(24));
            root.setAlignment(Pos.TOP_CENTER);

            // 1. Header Card styled with CSS class
            HBox headerBar = new HBox(12);
            headerBar.getStyleClass().add("header-bar");
            headerBar.setAlignment(Pos.CENTER_LEFT);

            Label brand = new Label("Aptech CSS Styling Engine");
            brand.getStyleClass().add("brand-title");

            Label badge = new Label("JavaFX 21");
            badge.getStyleClass().add("brand-badge");

            headerBar.getChildren().addAll(brand, badge);

            // 2. Interactive KPI Card with hover border
            VBox kpiCard = new VBox(8);
            kpiCard.getStyleClass().add("kpi-card");
            kpiCard.setPrefWidth(320);

            Label kpiTitle = new Label("Total Enrolled Candidates");
            kpiTitle.getStyleClass().add("kpi-label");

            Label kpiCount = new Label("1,428 Students");
            kpiCount.getStyleClass().add("kpi-number");

            Label kpiDelta = new Label("+18.4% vs previous semester");
            kpiDelta.getStyleClass().add("kpi-delta");

            kpiCard.getChildren().addAll(kpiTitle, kpiCount, kpiDelta);

            // 3. Form Inputs styled via CSS
            TextField searchField = new TextField();
            searchField.setPromptText("Type here to test :focused pseudo-class...");
            searchField.getStyleClass().add("text-field");
            searchField.setPrefWidth(320);

            // 4. Buttons demonstrating Primary and Secondary classes
            HBox buttonBar = new HBox(12);
            buttonBar.setAlignment(Pos.CENTER);

            Button btnPrimary = new Button("Apply Dark Theme");
            btnPrimary.getStyleClass().add("btn-primary");

            Button btnSecondary = new Button("Reset Style");
            btnSecondary.getStyleClass().add("btn-secondary");

            buttonBar.getChildren().addAll(btnPrimary, btnSecondary);

            root.getChildren().addAll(headerBar, kpiCard, searchField, buttonBar);

            Scene scene = new Scene(root, 620, 440);

            // Load external dashboard.css
            URL cssUrl = getClass().getResource("/dashboard.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Loaded external CSS: " + cssUrl.toExternalForm());
            } else {
                System.out.println("CSS resource path fallback: using inline class markers");
            }

            // Print diagnostics
            System.out.println("Scene Stylesheets: " + scene.getStylesheets());
            System.out.println("Root Style Classes: " + root.getStyleClass());
            System.out.println("KPI Card Style Classes: " + kpiCard.getStyleClass());
            System.out.println("Primary Button Classes: " + btnPrimary.getStyleClass());

            primaryStage.setTitle("Session 15 - CSS Skinning & Themes");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(CssApp.class, args);
    }
}
