package com.aptech.s14.lab;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Guided Lab: Aptech Vector Shape Studio & Animated Brand Emblem
 *
 * A comprehensive interactive JavaFX desktop studio where students:
 * 1. Style rich typography with linear gradients and baseline placement.
 * 2. Build a compound brand shield badge using Constructive Solid Geometry (CSG).
 * 3. Apply radial gradient lighting to create a 3D metallic sphere finish.
 * 4. Control 2D transformations (Rotation & Scaling) dynamically via interactive Sliders.
 * 5. Trigger hardware-accelerated animations using RotateTransition and ScaleTransition.
 */
public class ShapeStudioApp {

    public static class StudioApp extends Application {
        private RotateTransition spinAnim;
        private ScaleTransition pulseAnim;
        private boolean isAnimating = false;

        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== Launching Aptech Vector Shape Studio ===");

        // --- 1. Top Header: Gradient Typography ---
        LinearGradient titleGrad = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(14, 165, 233)),  // Sky blue
            new Stop(0.5, Color.rgb(139, 92, 246)),  // Purple
            new Stop(1.0, Color.rgb(244, 63, 94))    // Rose
        );

        Text headerText = new Text("APTECH VECTOR STUDIO");
        headerText.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        headerText.setFill(titleGrad);

        Text subHeaderText = new Text("Hardware-Accelerated 2D Geometry, CSG & Transitions");
        subHeaderText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, FontPosture.ITALIC, 12));
        subHeaderText.setFill(Color.GRAY);

        VBox headerBox = new VBox(4, headerText, subHeaderText);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15, 15, 10, 15));

        // --- 2. Center Canvas: The Compound Brand Emblem ---
        // Base 1: Octagonal Outer Star
        Polygon outerStar = new Polygon();
        double rOuter = 75, rInner = 55;
        for (int i = 0; i < 8; i++) {
            double r = (i % 2 == 0) ? rOuter : rInner;
            double angle = i * Math.PI / 4;
            outerStar.getPoints().addAll(80 + r * Math.cos(angle), 80 + r * Math.sin(angle));
        }

        // Base 2: Central circular shield
        Circle centerShield = new Circle(80, 80, 48);

        // CSG Union: Combine star and shield into one unified emblem
        Shape emblemBase = Shape.union(outerStar, centerShield);
        LinearGradient badgeGrad = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(59, 130, 246)),
            new Stop(1.0, Color.rgb(30, 64, 175))
        );
        emblemBase.setFill(badgeGrad);
        emblemBase.setStroke(Color.rgb(147, 197, 253));
        emblemBase.setStrokeWidth(3.0);

        // Core 3: Central Metallic Orb with Radial Gradient
        RadialGradient orbGrad = new RadialGradient(
            -35, 0.4, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.WHITE),
            new Stop(0.3, Color.rgb(251, 191, 36)),  // Amber
            new Stop(0.8, Color.rgb(217, 119, 6)),   // Dark amber
            new Stop(1.0, Color.rgb(146, 64, 14))    // Deep bronze
        );
        Circle coreOrb = new Circle(80, 80, 24);
        coreOrb.setFill(orbGrad);
        coreOrb.setStroke(Color.GOLD);
        coreOrb.setStrokeWidth(1.5);

        // Overlay: Center Monogram Letter
        Text monogram = new Text("A");
        monogram.setFont(Font.font("Segoe UI", FontWeight.BLACK, 26));
        monogram.setFill(Color.WHITE);
        monogram.setX(71);
        monogram.setY(89);

        // Group into an emblem container StackPane
        StackPane emblem = new StackPane(emblemBase, coreOrb, monogram);
        emblem.setPrefSize(180, 180);

        // Transforms attached to emblem
        Rotate manualRotate = new Rotate(0, 90, 90);
        Scale manualScale = new Scale(1.0, 1.0, 90, 90);
        emblem.getTransforms().addAll(manualRotate, manualScale);

        // Setup Animations
        spinAnim = new RotateTransition(Duration.seconds(3.0), emblem);
        spinAnim.setByAngle(360);
        spinAnim.setCycleCount(Animation.INDEFINITE);
        spinAnim.setInterpolator(Interpolator.LINEAR);

        pulseAnim = new ScaleTransition(Duration.millis(800), emblem);
        pulseAnim.setFromX(0.9);
        pulseAnim.setFromY(0.9);
        pulseAnim.setToX(1.15);
        pulseAnim.setToY(1.15);
        pulseAnim.setCycleCount(Animation.INDEFINITE);
        pulseAnim.setAutoReverse(true);

        StackPane canvasArea = new StackPane(emblem);
        canvasArea.setPadding(new Insets(30));
        canvasArea.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12px;");

        // --- 3. Bottom Controls: Sliders and Action Buttons ---
        Label rotateLabel = new Label("Rotate: 0°");
        Slider rotateSlider = new Slider(0, 360, 0);
        rotateSlider.valueProperty().addListener((obs, oldV, newV) -> {
            manualRotate.setAngle(newV.doubleValue());
            rotateLabel.setText(String.format("Rotate: %.0f°", newV.doubleValue()));
        });

        Label scaleLabel = new Label("Scale: 1.0x");
        Slider scaleSlider = new Slider(0.5, 2.0, 1.0);
        scaleSlider.valueProperty().addListener((obs, oldV, newV) -> {
            manualScale.setX(newV.doubleValue());
            manualScale.setY(newV.doubleValue());
            scaleLabel.setText(String.format("Scale: %.2fx", newV.doubleValue()));
        });

        Button animToggleBtn = new Button("Start Animation");
        animToggleBtn.setOnAction(e -> {
            if (!isAnimating) {
                spinAnim.play();
                pulseAnim.play();
                animToggleBtn.setText("Pause Animation");
                isAnimating = true;
                System.out.println("[Studio] Hardware animation started.");
            } else {
                spinAnim.pause();
                pulseAnim.pause();
                animToggleBtn.setText("Resume Animation");
                isAnimating = false;
                System.out.println("[Studio] Hardware animation paused.");
            }
        });

        Button resetBtn = new Button("Reset Emblem");
        resetBtn.setOnAction(e -> {
            spinAnim.stop();
            pulseAnim.stop();
            isAnimating = false;
            animToggleBtn.setText("Start Animation");
            rotateSlider.setValue(0);
            scaleSlider.setValue(1.0);
            manualRotate.setAngle(0);
            manualScale.setX(1.0);
            manualScale.setY(1.0);
            System.out.println("[Studio] Emblem transforms reset.");
        });

        HBox sliderRow = new HBox(20, rotateLabel, rotateSlider, scaleLabel, scaleSlider);
        sliderRow.setAlignment(Pos.CENTER);

        HBox btnRow = new HBox(15, animToggleBtn, resetBtn);
        btnRow.setAlignment(Pos.CENTER);

        VBox controlPanel = new VBox(12, sliderRow, btnRow);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(15));

        // --- 4. Root Assembly ---
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(canvasArea);
        root.setBottom(controlPanel);
        root.setPadding(new Insets(15));

        System.out.println("Emblem hierarchy constructed:");
        System.out.println("  Base: " + emblemBase.getId());
        System.out.println("  Core Orb: " + coreOrb);
        System.out.println("  Monogram: " + monogram);
        System.out.println("  Transforms: [Rotate, Scale]");

        Scene scene = new Scene(root, 650, 480);
        primaryStage.setTitle("Aptech Vector Shape Studio - Lab Solution");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(StudioApp.class, args);
    }
}
