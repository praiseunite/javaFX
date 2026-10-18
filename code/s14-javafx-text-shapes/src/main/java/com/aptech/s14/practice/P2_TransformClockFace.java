package com.aptech.s14.practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 * Practice 2 (Medium): Analog Clock Face with Rotation Transforms
 *
 * Requirements:
 * 1. Metallic clock rim with radial gradient.
 * 2. 12 hour tick marks arranged around the perimeter using Rotate transforms.
 * 3. Distinct hour hand, minute hand, and second hand with center pivots.
 */
public class P2_TransformClockFace {

    public static class ClockApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== Practice 2: Analog Clock Face with Rotation Transforms ===");

        double centerX = 150, centerY = 150;
        double dialRadius = 120;

        // Clock Rim & Face
        RadialGradient dialFaceGrad = new RadialGradient(
            0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(248, 250, 252)),
            new Stop(0.85, Color.rgb(226, 232, 240)),
            new Stop(1.0, Color.rgb(148, 163, 184))
        );
        Circle clockDial = new Circle(centerX, centerY, dialRadius, dialFaceGrad);
        clockDial.setStroke(Color.rgb(51, 65, 85));
        clockDial.setStrokeWidth(5.0);

        Group tickGroup = new Group();

        // 12 Hour Ticks
        for (int hour = 1; hour <= 12; hour++) {
            double angle = hour * 30.0; // 360 / 12 = 30 degrees per hour
            boolean isQuarter = (hour % 3 == 0);

            double tickLength = isQuarter ? 16 : 8;
            double tickStroke = isQuarter ? 3.5 : 1.5;

            // Base tick mark placed vertically at the 12 o'clock position
            Line tick = new Line(centerX, centerY - dialRadius + 5, centerX, centerY - dialRadius + 5 + tickLength);
            tick.setStroke(isQuarter ? Color.rgb(15, 23, 42) : Color.rgb(100, 116, 139));
            tick.setStrokeWidth(tickStroke);

            // Rotate around the exact center of the clock
            Rotate rot = new Rotate(angle, centerX, centerY);
            tick.getTransforms().add(rot);

            tickGroup.getChildren().add(tick);
        }

        // Hour Hand: points to 10 o'clock (300 degrees)
        Line hourHand = new Line(centerX, centerY, centerX, centerY - 65);
        hourHand.setStroke(Color.rgb(15, 23, 42));
        hourHand.setStrokeWidth(4.5);
        hourHand.getTransforms().add(new Rotate(300, centerX, centerY));

        // Minute Hand: points to 2 o'clock (60 degrees)
        Line minuteHand = new Line(centerX, centerY, centerX, centerY - 95);
        minuteHand.setStroke(Color.rgb(15, 23, 42));
        minuteHand.setStrokeWidth(2.5);
        minuteHand.getTransforms().add(new Rotate(60, centerX, centerY));

        // Second Hand: points to 7 o'clock (210 degrees)
        Line secondHand = new Line(centerX, centerY + 15, centerX, centerY - 105);
        secondHand.setStroke(Color.CRIMSON);
        secondHand.setStrokeWidth(1.2);
        secondHand.getTransforms().add(new Rotate(210, centerX, centerY));

        // Center Cap Pin
        Circle centerPin = new Circle(centerX, centerY, 5, Color.CRIMSON);

        Group clockAssembly = new Group(clockDial, tickGroup, hourHand, minuteHand, secondHand, centerPin);

        Text title = new Text("Analog Clock Face (Geometric Transforms)");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        VBox root = new VBox(15, title, clockAssembly);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        System.out.println("Analog Clock assembled: 12 rotated ticks, Hour Hand (300 deg), Minute Hand (60 deg)");

        Scene scene = new Scene(root, 360, 400);
        primaryStage.setTitle("Practice 2 - Transform Clock Face");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(ClockApp.class, args);
    }
}
