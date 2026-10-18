package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Session 14 - Example 4: Color Gradients and Complex Paints
 *
 * Demonstrates:
 * 1. LinearGradient: Linear interpolation of color stops along a directional vector.
 * 2. RadialGradient: Concentric circular color radiation from a focal center point.
 * 3. Proportional vs absolute coordinates (0.0 - 1.0 vs physical pixels).
 * 4. CycleMethod: NO_CYCLE, REFLECT, REPEAT.
 * 5. Gradient fills applied to geometric Shapes and Text nodes.
 */
public class E04_ColorGradientsAndPaints {

    public static class PaintApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX Color Gradients & Complex Paints ===");

        // 1. Linear Gradient: Diagonal from top-left (0,0) to bottom-right (1,1)
        LinearGradient diagonalLinear = new LinearGradient(
            0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.CORNFLOWERBLUE),
            new Stop(0.5, Color.DEEPPINK),
            new Stop(1.0, Color.GOLD)
        );

        Rectangle linearRect = new Rectangle(140, 90);
        linearRect.setFill(diagonalLinear);
        linearRect.setArcWidth(16);
        linearRect.setArcHeight(16);
        linearRect.setStroke(Color.DARKSLATEBLUE);
        linearRect.setStrokeWidth(2.0);

        // 2. Radial Gradient: Glowing orb with specular center highlight
        RadialGradient sphereRadial = new RadialGradient(
            -30.0, 0.35, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.WHITE),
            new Stop(0.25, Color.CYAN),
            new Stop(0.70, Color.DODGERBLUE),
            new Stop(1.0, Color.DARKBLUE)
        );

        Circle radialCircle = new Circle(48);
        radialCircle.setFill(sphereRadial);
        radialCircle.setStroke(Color.TEAL);
        radialCircle.setStrokeWidth(1.5);

        // 3. Repeating Linear Gradient (Striped effect)
        LinearGradient stripedLinear = new LinearGradient(
            0.0, 0.0, 0.2, 0.0, true, CycleMethod.REPEAT,
            new Stop(0.0, Color.GOLD),
            new Stop(0.5, Color.DARKORANGE),
            new Stop(0.501, Color.BLACK),
            new Stop(1.0, Color.BLACK)
        );

        Rectangle stripedRect = new Rectangle(140, 90);
        stripedRect.setFill(stripedLinear);
        stripedRect.setArcWidth(16);
        stripedRect.setArcHeight(16);

        // 4. Gradient applied directly to typography
        LinearGradient textGradient = new LinearGradient(
            0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.DEEPPINK),
            new Stop(0.5, Color.GOLD),
            new Stop(1.0, Color.CYAN)
        );

        Text gradientText = new Text("GRADIENT TYPOGRAPHY");
        gradientText.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        gradientText.setFill(textGradient);

        // Log parameters
        System.out.println("LinearGradient: " + diagonalLinear);
        System.out.println("RadialGradient: " + sphereRadial);
        System.out.println("Repeat Gradient CycleMethod: " + stripedLinear.getCycleMethod());
        System.out.println("Gradient Text fill: " + gradientText.getFill());

        // UI Layout
        Text title = new Text("Linear & Radial Gradients in JavaFX");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        HBox cardRow = new HBox(30,
            new VBox(8, linearRect, new Text("Diagonal Multi-Stop")),
            new VBox(8, radialCircle, new Text("Radial 3D Orb")),
            new VBox(8, stripedRect, new Text("Repeat Pattern"))
        );
        cardRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(25, title, cardRow, gradientText);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 600, 380);
        primaryStage.setTitle("JavaFX Gradients & Paints");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(PaintApp.class, args);
    }
}
