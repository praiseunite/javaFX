package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Session 14 - Example 3: Two-Dimensional Shape Primitives and Stroke Styling
 *
 * Demonstrates:
 * 1. Concrete 2D Shape subclasses: Line, Rectangle, Circle, Ellipse, Polygon, Arc.
 * 2. Rounded corner geometry on Rectangle (arcWidth, arcHeight).
 * 3. Arc closure types: ArcType.OPEN, ArcType.CHORD, ArcType.ROUND.
 * 4. Stroke mechanics: StrokeType (INSIDE, OUTSIDE, CENTERED), StrokeLineCap, StrokeLineJoin.
 * 5. Dashed and dotted outlines via strokeDashArray.
 */
public class E03_TwoDimensionalShapes {

    public static class ShapeApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX 2D Shape Primitives & Stroke Styling ===");

        // 1. Line with custom end-caps and dash pattern
        Line dashedLine = new Line(0, 0, 100, 50);
        dashedLine.setStroke(Color.DODGERBLUE);
        dashedLine.setStrokeWidth(3.0);
        dashedLine.setStrokeLineCap(StrokeLineCap.ROUND);
        dashedLine.getStrokeDashArray().addAll(10.0, 5.0, 2.0, 5.0);

        // 2. Rounded Rectangle with custom stroke join
        Rectangle roundedRect = new Rectangle(110, 65, Color.CORNFLOWERBLUE);
        roundedRect.setArcWidth(25);
        roundedRect.setArcHeight(25);
        roundedRect.setStroke(Color.DARKSLATEBLUE);
        roundedRect.setStrokeWidth(3.0);
        roundedRect.setStrokeType(StrokeType.INSIDE);

        // 3. Circle with centered stroke
        Circle circle = new Circle(40, Color.GOLD);
        circle.setStroke(Color.DARKORANGE);
        circle.setStrokeWidth(3.0);

        // 4. Ellipse (radiusX=55, radiusY=35)
        Ellipse ellipse = new Ellipse(55, 35);
        ellipse.setFill(Color.TEAL);
        ellipse.setStroke(Color.CYAN);
        ellipse.setStrokeWidth(2.5);

        // 5. Polygon: Equilateral Hexagon (6 vertices)
        Polygon hexagon = new Polygon();
        double radius = 42;
        double centerX = 45, centerY = 45;
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double px = centerX + radius * Math.cos(angle);
            double py = centerY + radius * Math.sin(angle);
            hexagon.getPoints().addAll(px, py);
        }
        hexagon.setFill(Color.DEEPPINK);
        hexagon.setStroke(Color.CRIMSON);
        hexagon.setStrokeWidth(2.5);
        hexagon.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // 6. Arc (Pie slice): ArcType.ROUND
        Arc pieSlice = new Arc(45, 45, 45, 45, 30, 240);
        pieSlice.setType(ArcType.ROUND);
        pieSlice.setFill(Color.ORANGE);
        pieSlice.setStroke(Color.DARKRED);
        pieSlice.setStrokeWidth(2.0);

        // Log parameters
        System.out.println("Line: " + dashedLine + " [Dashes: " + dashedLine.getStrokeDashArray() + "]");
        System.out.println("Rounded Rectangle: " + roundedRect);
        System.out.println("Circle: " + circle);
        System.out.println("Ellipse: " + ellipse);
        System.out.println("Hexagon: " + hexagon + " [Points count: " + hexagon.getPoints().size() + "]");
        System.out.println("Arc (Pie Slice): " + pieSlice);

        // Assemble Grid
        Text title = new Text("JavaFX 2D Shape Gallery & Stroke Styling");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setHgap(35);
        grid.setVgap(25);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(15));

        grid.add(new VBox(8, dashedLine, new Text("Dashed Line")), 0, 0);
        grid.add(new VBox(8, roundedRect, new Text("Rounded Rectangle")), 1, 0);
        grid.add(new VBox(8, circle, new Text("Circle")), 2, 0);
        grid.add(new VBox(8, ellipse, new Text("Ellipse")), 0, 1);
        grid.add(new VBox(8, hexagon, new Text("Hexagon Polygon")), 1, 1);
        grid.add(new VBox(8, pieSlice, new Text("Round Arc (Pie)")), 2, 1);

        VBox root = new VBox(20, title, grid);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 640, 420);
        primaryStage.setTitle("JavaFX 2D Shapes & Stroke Demonstration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(ShapeApp.class, args);
    }
}
