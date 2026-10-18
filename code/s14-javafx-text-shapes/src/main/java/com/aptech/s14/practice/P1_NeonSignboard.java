package com.aptech.s14.practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Practice 1 (Easy): Glowing Cyber Neon Signboard
 *
 * Requirements:
 * 1. Dark background panel with rounded corners and double border.
 * 2. Vibrant neon Text with custom linear gradient fill.
 * 3. Multi-layer DropShadow effect simulating real fluorescent tube light glow.
 */
public class P1_NeonSignboard {

    public static class NeonApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== Practice 1: Glowing Cyber Neon Signboard ===");

        // Signboard backplate
        Rectangle backplate = new Rectangle(420, 160);
        backplate.setFill(Color.rgb(15, 23, 42)); // Midnight slate
        backplate.setArcWidth(20);
        backplate.setArcHeight(20);
        backplate.setStroke(Color.rgb(51, 65, 85));
        backplate.setStrokeWidth(2.5);

        // Inner glowing border
        Rectangle innerFrame = new Rectangle(390, 130);
        innerFrame.setFill(Color.TRANSPARENT);
        innerFrame.setArcWidth(12);
        innerFrame.setArcHeight(12);
        innerFrame.setStroke(Color.rgb(6, 182, 212)); // Cyan neon
        innerFrame.setStrokeWidth(1.5);

        // Neon Typography
        LinearGradient neonGrad = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(56, 189, 248)),  // Sky
            new Stop(0.5, Color.rgb(236, 72, 153)),  // Neon pink
            new Stop(1.0, Color.rgb(250, 204, 21))   // Neon yellow
        );

        Text neonText = new Text("OPEN 24/7");
        neonText.setFont(Font.font("Segoe UI", FontWeight.BLACK, 42));
        neonText.setFill(neonGrad);

        // Neon Glow Effect
        DropShadow glow = new DropShadow(25, 0, 0, Color.rgb(236, 72, 153));
        neonText.setEffect(glow);

        Text subText = new Text("APTECH JAVA LAB");
        subText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        subText.setFill(Color.rgb(148, 163, 184));

        VBox textGroup = new VBox(6, neonText, subText);
        textGroup.setAlignment(Pos.CENTER);

        StackPane signboard = new StackPane(backplate, innerFrame, textGroup);
        signboard.setPadding(new Insets(20));

        System.out.println("Neon sign rendered: 'OPEN 24/7' with DropShadow radius=25");

        Scene scene = new Scene(new StackPane(signboard), 480, 240);
        scene.setFill(Color.rgb(2, 6, 23));
        primaryStage.setTitle("Practice 1 - Neon Signboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(NeonApp.class, args);
    }
}
