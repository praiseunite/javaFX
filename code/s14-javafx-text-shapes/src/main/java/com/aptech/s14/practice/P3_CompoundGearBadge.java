package com.aptech.s14.practice;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Practice 3 (Challenge): Animated Engineering Cogwheel / Gear Badge
 *
 * Requirements:
 * 1. Construct a solid cogwheel using Constructive Solid Geometry (CSG):
 *    - Start with a solid circular hub.
 *    - Union 8 radial teeth blocks around the circumference.
 *    - Subtract a central axle bore hole and keyway notch.
 * 2. Apply metallic bronze/steel radial gradient.
 * 3. Attach a continuous RotateTransition that spins the gear smoothly.
 */
public class P3_CompoundGearBadge {

    public static class GearApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== Practice 3: Animated Engineering Gear Badge (CSG) ===");

        double cx = 100, cy = 100;
        double hubRadius = 60;

        // 1. Base Hub Circle
        Shape gearShape = new Circle(cx, cy, hubRadius);

        // 2. Add 8 gear teeth via Shape.union()
        for (int i = 0; i < 4; i++) {
            double angle = i * 45.0;
            // Cross-bar representing two opposite teeth
            Rectangle toothPair = new Rectangle(cx - 72, cy - 12, 144, 24);
            toothPair.setArcWidth(6);
            toothPair.setArcHeight(6);
            toothPair.setRotate(angle);

            gearShape = Shape.union(gearShape, toothPair);
        }

        // 3. Subtract central axle bore hole
        Circle axleHole = new Circle(cx, cy, 24);
        gearShape = Shape.subtract(gearShape, axleHole);

        // 4. Subtract keyway slot notch
        Rectangle keyway = new Rectangle(cx - 5, cy - 32, 10, 14);
        gearShape = Shape.subtract(gearShape, keyway);

        // Metallic Finish: Steel Radial Gradient
        RadialGradient steelGrad = new RadialGradient(
            -45, 0.4, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(241, 245, 249)),
            new Stop(0.5, Color.rgb(148, 163, 184)),
            new Stop(1.0, Color.rgb(51, 65, 85))
        );
        gearShape.setFill(steelGrad);
        gearShape.setStroke(Color.rgb(30, 41, 59));
        gearShape.setStrokeWidth(2.0);

        // 5. Spin Animation: 4 seconds per full 360 rotation
        RotateTransition spin = new RotateTransition(Duration.seconds(4.0), gearShape);
        spin.setByAngle(360);
        spin.setCycleCount(Animation.INDEFINITE);
        spin.setInterpolator(Interpolator.LINEAR);
        spin.play();

        Text title = new Text("Animated Precision Gear (CSG Subtraction)");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        VBox root = new VBox(25, title, gearShape);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        System.out.println("Gear badge built using 4 unions and 2 subtractions. Spin animation active.");

        Scene scene = new Scene(root, 360, 360);
        primaryStage.setTitle("Practice 3 - Compound Gear Badge");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(GearApp.class, args);
    }
}
