package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Session 14 - Example 1: JavaFX Text and Typography
 *
 * Demonstrates:
 * 1. The Text node (subclass of Shape) vs the Control Label.
 * 2. Typography styling: Font families, weights, postures, and sizes.
 * 3. Shape fill, stroke, and strokeWidth applied to text glyphs.
 * 4. Text alignment, wrapping width, and baseline origin mechanics.
 * 5. TextBoundsType: LOGICAL (for layout flow) vs VISUAL (tight glyph bounding box).
 * 6. TextFlow: Combining multiple styled Text nodes with inline formatting.
 */
public class E01_TextAndTypography {

    public static class TextApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX Text & Typography Inspection ===");

        // 1. Primary Heading: Rich Typography with fill & stroke
        Text heading = new Text("Aptech Graphic Studio");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, FontPosture.REGULAR, 26));
        heading.setFill(Color.CORNFLOWERBLUE);
        heading.setStroke(Color.DARKSLATEBLUE);
        heading.setStrokeWidth(1.2);
        heading.setUnderline(true);

        // 2. Subheading: Demonstrating baseline coordinate trap
        // Text coordinates (X, Y) specify the font BASELINE, not the top-left corner!
        Text baselineNote = new Text(0, 20, "Coordinates (X, Y) place the text BASELINE, not top-left.");
        baselineNote.setFont(Font.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, 13));
        baselineNote.setFill(Color.DODGERBLUE);

        // 3. Paragraph Text with automatic wrapping and justification
        Text paragraph = new Text(
            "JavaFX treats every Text node as a true geometric Shape. Because Text inherits from " +
            "javafx.scene.shape.Shape, you can apply geometric fills, linear gradients, outline strokes, " +
            "clipping paths, and hardware-accelerated 2D transforms directly to typography."
        );
        paragraph.setFont(Font.font("Segoe UI", 14));
        paragraph.setFill(Color.DARKGRAY);
        paragraph.setWrappingWidth(440);
        paragraph.setTextAlignment(TextAlignment.JUSTIFY);

        // 4. Bounds Type: Logical vs Visual
        Text boundsDemo = new Text("Glyph Bounding Box");
        boundsDemo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        boundsDemo.setBoundsType(TextBoundsType.VISUAL);
        boundsDemo.setFontSmoothingType(FontSmoothingType.LCD);

        // 5. TextFlow: Rich inline document structure
        Text flowPart1 = new Text("Status: ");
        flowPart1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        flowPart1.setFill(Color.BLACK);

        Text flowPart2 = new Text("Hardware Acceleration Active ");
        flowPart2.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        flowPart2.setFill(Color.GREEN);

        Text flowPart3 = new Text("[Prism D3D/Metal]");
        flowPart3.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        flowPart3.setFill(Color.CRIMSON);

        TextFlow statusFlow = new TextFlow(flowPart1, flowPart2, flowPart3);
        statusFlow.setPadding(new Insets(8));

        // Assemble Layout
        VBox root = new VBox(14, heading, baselineNote, paragraph, boundsDemo, statusFlow);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        // Print node metrics to console for verification
        System.out.println("Heading Font: " + heading.getFont());
        System.out.println("Heading Fill: " + heading.getFill());
        System.out.println("Heading Stroke: " + heading.getStroke() + " (width=" + heading.getStrokeWidth() + "px)");
        System.out.println("Paragraph Wrapping Width: " + paragraph.getWrappingWidth() + "px");
        System.out.println("Bounds Demo Local Bounds: " + boundsDemo.getBoundsInLocal());
        System.out.println("TextFlow child count: " + statusFlow.getChildren().size());

        Scene scene = new Scene(root, 520, 360);
        primaryStage.setTitle("JavaFX Text & Typography Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(TextApp.class, args);
    }
}
