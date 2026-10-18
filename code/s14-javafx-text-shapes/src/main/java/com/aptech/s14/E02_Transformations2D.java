package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Session 14 - Example 2: 2D Geometric Transformations
 *
 * Demonstrates:
 * 1. The four core transformations: Translation, Rotation, Scaling, and Shearing.
 * 2. Pivot point mechanics: Rotating/Scaling around center (pivotX, pivotY) vs top-left origin (0, 0).
 * 3. Cumulative transform pipeline via node.getTransforms().addAll(...).
 * 4. Critical rule: Transformation order matters! (Translate-then-Rotate != Rotate-then-Translate).
 */
public class E02_Transformations2D {

    public static class TransformApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX 2D Geometric Transformations Inspection ===");

        // Reference original shape: 80x50 rectangle
        Rectangle originalRect = new Rectangle(80, 50, Color.LIGHTGRAY);
        originalRect.setStroke(Color.BLACK);
        originalRect.setStrokeWidth(1.5);

        // 1. Translation: Shifts position along X and Y axes
        Rectangle translatedRect = new Rectangle(80, 50, Color.CORNFLOWERBLUE);
        translatedRect.setStroke(Color.DARKSLATEBLUE);
        Translate translate = new Translate(25, 15);
        translatedRect.getTransforms().add(translate);

        // 2. Rotation around custom pivot point (center of the rectangle: 40, 25)
        Rectangle rotatedRect = new Rectangle(80, 50, Color.GOLD);
        rotatedRect.setStroke(Color.ORANGE);
        // Pivot point at center (40, 25), rotated 45 degrees
        Rotate rotate = new Rotate(45, 40, 25);
        rotatedRect.getTransforms().add(rotate);

        // 3. Scaling around center pivot (scale 1.3x horizontally, 0.8x vertically)
        Rectangle scaledRect = new Rectangle(80, 50, Color.CYAN);
        scaledRect.setStroke(Color.TEAL);
        Scale scale = new Scale(1.3, 0.8, 40, 25);
        scaledRect.getTransforms().add(scale);

        // 4. Shearing (Skewing) along X axis
        Rectangle shearedRect = new Rectangle(80, 50, Color.DEEPPINK);
        shearedRect.setStroke(Color.CRIMSON);
        Shear shear = new Shear(0.35, 0.0, 0, 25);
        shearedRect.getTransforms().add(shear);

        // 5. Demonstrating that Transform Order Matters
        // Shape A: Translate first, then Rotate
        Rectangle orderA = new Rectangle(60, 40, Color.GREEN);
        orderA.getTransforms().addAll(new Translate(50, 0), new Rotate(45, 0, 0));

        // Shape B: Rotate first, then Translate
        Rectangle orderB = new Rectangle(60, 40, Color.RED);
        orderB.getTransforms().addAll(new Rotate(45, 0, 0), new Translate(50, 0));

        // Output transformation details to console
        System.out.println("Original bounds: " + originalRect.getBoundsInLocal());
        System.out.println("Translation applied: " + translate);
        System.out.println("Rotation applied: " + rotate + " (Center Pivot)");
        System.out.println("Scale applied: " + scale);
        System.out.println("Shear applied: " + shear);
        System.out.println("\n--- Transform Order Experiment ---");
        System.out.println("Order A (Translate then Rotate) transforms: " + orderA.getTransforms());
        System.out.println("Order B (Rotate then Translate) transforms: " + orderB.getTransforms());
        System.out.println("Notice: Order B translates along the ROTATED local axis!");

        // Layout rows
        Text title = new Text("2D Transformations: Translate, Rotate, Scale, Shear");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        HBox showcaseRow = new HBox(30, originalRect, translatedRect, rotatedRect, scaledRect, shearedRect);
        showcaseRow.setAlignment(Pos.CENTER);
        showcaseRow.setPadding(new Insets(20));

        HBox orderRow = new HBox(40, orderA, orderB);
        orderRow.setAlignment(Pos.CENTER);
        orderRow.setPadding(new Insets(10));

        VBox root = new VBox(20, title, showcaseRow, orderRow);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 620, 380);
        primaryStage.setTitle("JavaFX 2D Transformations Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(TransformApp.class, args);
    }
}
