package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Session 14 - Example 5: Constructive Solid Geometry (CSG) & Compound Shapes
 *
 * Demonstrates:
 * 1. The three boolean operations on 2D Shapes:
 *    - Shape.union(s1, s2): Merges two shapes into one combined area.
 *    - Shape.intersect(s1, s2): Computes the overlapping common region.
 *    - Shape.subtract(s1, s2): Carves shape s2 out of shape s1 (cookie cutter).
 * 2. Combining geometric primitives into complex iconography (cross badge, crescent moon, donut washer).
 * 3. Applying custom fills and outlines to the resulting compound Shape.
 */
public class E05_ConstructiveSolidGeometry {

    public static class CsgApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX Constructive Solid Geometry (CSG) ===");

        // 1. UNION: Plus / Medical Cross Badge
        // Combine a horizontal rectangle and a vertical rectangle
        Rectangle hBar = new Rectangle(0, 30, 80, 20);
        Rectangle vBar = new Rectangle(30, 0, 20, 80);
        Shape crossBadge = Shape.union(hBar, vBar);
        crossBadge.setFill(Color.CRIMSON);
        crossBadge.setStroke(Color.DARKRED);
        crossBadge.setStrokeWidth(2.0);

        // 2. INTERSECTION: Lens / Leaf Shape
        // Intersect two overlapping circles
        Circle c1 = new Circle(35, 45, 35);
        Circle c2 = new Circle(65, 45, 35);
        Shape lensShape = Shape.intersect(c1, c2);
        lensShape.setFill(Color.CORNFLOWERBLUE);
        lensShape.setStroke(Color.DARKSLATEBLUE);
        lensShape.setStrokeWidth(2.0);

        // 3. SUBTRACTION (A): Crescent Moon
        // Base circle minus shifted circle
        Circle moonBase = new Circle(45, 45, 40);
        Circle moonCutter = new Circle(65, 35, 36);
        Shape crescentMoon = Shape.subtract(moonBase, moonCutter);
        crescentMoon.setFill(Color.GOLD);
        crescentMoon.setStroke(Color.DARKORANGE);
        crescentMoon.setStrokeWidth(2.0);

        // 4. SUBTRACTION (B): Washer / Donut Ring
        // Outer circle minus concentric inner hole
        Circle outer = new Circle(45, 45, 40);
        Circle innerHole = new Circle(45, 45, 20);
        Shape donutRing = Shape.subtract(outer, innerHole);
        donutRing.setFill(Color.TEAL);
        donutRing.setStroke(Color.CYAN);
        donutRing.setStrokeWidth(2.0);

        // Output details to console
        System.out.println("Union Shape: " + crossBadge.getId() + " [Fill: " + crossBadge.getFill() + "]");
        System.out.println("Intersect Shape: " + lensShape.getId() + " [Fill: " + lensShape.getFill() + "]");
        System.out.println("Subtract (Moon): " + crescentMoon.getId() + " [Fill: " + crescentMoon.getFill() + "]");
        System.out.println("Subtract (Donut): " + donutRing.getId() + " [Fill: " + donutRing.getFill() + "]");

        // UI Assembly
        Text title = new Text("Constructive Solid Geometry (CSG) in JavaFX");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        HBox shapeRow = new HBox(35,
            new VBox(10, crossBadge, new Text("Shape.union()")),
            new VBox(10, lensShape, new Text("Shape.intersect()")),
            new VBox(10, crescentMoon, new Text("Shape.subtract() [Moon]")),
            new VBox(10, donutRing, new Text("Shape.subtract() [Donut]"))
        );
        shapeRow.setAlignment(Pos.CENTER);
        shapeRow.setPadding(new Insets(15));

        VBox root = new VBox(25, title, shapeRow);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 680, 360);
        primaryStage.setTitle("JavaFX Constructive Solid Geometry Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(CsgApp.class, args);
    }
}
