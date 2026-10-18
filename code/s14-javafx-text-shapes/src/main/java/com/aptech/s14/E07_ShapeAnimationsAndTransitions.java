package com.aptech.s14;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Session 14 - Example 7: Shape Animations and Transitions
 *
 * Demonstrates:
 * 1. Hardware-accelerated Transition classes: RotateTransition, ScaleTransition, TranslateTransition.
 * 2. Continuous looping and ping-pong animations: setCycleCount(INDEFINITE) and setAutoReverse(true).
 * 3. ParallelTransition: Executing multiple transforms simultaneously on a single shape.
 * 4. SequentialTransition: Choreographing a multi-step animation sequence.
 * 5. Interpolator: Smoothing motion with LINEAR vs EASE_BOTH.
 */
public class E07_ShapeAnimationsAndTransitions {

    public static class AnimApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX Shape Animations & Transitions ===");

        // 1. Continuous Rotation Transition on a Star/Hexagon
        Polygon star = new Polygon();
        double r = 40;
        for (int i = 0; i < 8; i++) {
            double currentR = (i % 2 == 0) ? r : r * 0.5;
            double angle = i * Math.PI / 4;
            star.getPoints().addAll(45 + currentR * Math.cos(angle), 45 + currentR * Math.sin(angle));
        }
        star.setFill(Color.GOLD);
        star.setStroke(Color.DARKORANGE);
        star.setStrokeWidth(2.0);

        RotateTransition continuousRotate = new RotateTransition(Duration.seconds(3.0), star);
        continuousRotate.setByAngle(360);
        continuousRotate.setCycleCount(Animation.INDEFINITE);
        continuousRotate.setInterpolator(Interpolator.LINEAR);
        continuousRotate.play();

        // 2. Pulsing Scale Transition (Heartbeat effect)
        Circle pulseCircle = new Circle(35, Color.CRIMSON);
        pulseCircle.setStroke(Color.DARKRED);
        pulseCircle.setStrokeWidth(2.0);

        ScaleTransition pulseScale = new ScaleTransition(Duration.millis(600), pulseCircle);
        pulseScale.setFromX(1.0);
        pulseScale.setFromY(1.0);
        pulseScale.setToX(1.3);
        pulseScale.setToY(1.3);
        pulseScale.setCycleCount(Animation.INDEFINITE);
        pulseScale.setAutoReverse(true);
        pulseScale.play();

        // 3. Composite Parallel Transition (Rotate + Scale simultaneously on a Badge)
        Rectangle compositeBadge = new Rectangle(65, 65, Color.CORNFLOWERBLUE);
        compositeBadge.setArcWidth(16);
        compositeBadge.setArcHeight(16);
        compositeBadge.setStroke(Color.DARKSLATEBLUE);
        compositeBadge.setStrokeWidth(2.5);

        RotateTransition badgeRotate = new RotateTransition(Duration.seconds(2.0), compositeBadge);
        badgeRotate.setByAngle(180);

        ScaleTransition badgeScale = new ScaleTransition(Duration.seconds(2.0), compositeBadge);
        badgeScale.setFromX(0.8);
        badgeScale.setFromY(0.8);
        badgeScale.setToX(1.2);
        badgeScale.setToY(1.2);

        ParallelTransition parallelAnim = new ParallelTransition(badgeRotate, badgeScale);
        parallelAnim.setCycleCount(Animation.INDEFINITE);
        parallelAnim.setAutoReverse(true);
        parallelAnim.play();

        // 4. Sequential Bouncing Transition
        Circle bouncingBall = new Circle(20, Color.TEAL);
        bouncingBall.setStroke(Color.CYAN);

        TranslateTransition drop = new TranslateTransition(Duration.millis(500), bouncingBall);
        drop.setByY(50);
        TranslateTransition rebound = new TranslateTransition(Duration.millis(500), bouncingBall);
        rebound.setByY(-50);

        SequentialTransition sequenceAnim = new SequentialTransition(drop, rebound);
        sequenceAnim.setCycleCount(Animation.INDEFINITE);
        sequenceAnim.play();

        System.out.println("Started 4 concurrent shape animations on JavaFX Application Thread:");
        System.out.println("  1. RotateTransition on 8-point Star [Duration: 3.0s, Linear, Indefinite]");
        System.out.println("  2. ScaleTransition on Pulse Circle [Duration: 0.6s, AutoReverse]");
        System.out.println("  3. ParallelTransition (Rotate + Scale) on Rounded Badge");
        System.out.println("  4. SequentialTransition (Drop + Rebound) on Ball");

        // UI Assembly
        Text title = new Text("JavaFX Hardware-Accelerated Shape Animations");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        HBox animRow = new HBox(40,
            new VBox(12, star, new Text("Continuous Rotate")),
            new VBox(12, pulseCircle, new Text("AutoReverse Pulse")),
            new VBox(12, compositeBadge, new Text("Parallel Rotate+Scale")),
            new VBox(12, bouncingBall, new Text("Sequential Rebound"))
        );
        animRow.setAlignment(Pos.CENTER);
        animRow.setPadding(new Insets(20));

        VBox root = new VBox(25, title, animRow);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 680, 360);
        primaryStage.setTitle("JavaFX Shape Animations Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(AnimApp.class, args);
    }
}
