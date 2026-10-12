package com.aptech.s13;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Session 13 - Example 6: Stage, Scene & Scene Graph Tree Traversal
 *
 * Demonstrates the theatre hierarchy:
 * Stage (Window) -> Scene (Canvas) -> Root Node (VBox) -> Branch Nodes (HBox) -> Leaf Nodes (Controls)
 * Includes recursive Scene Graph inspector.
 */
public class E06_StageAndSceneGraph {

    public static class SceneGraphApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            System.out.println("=== Assembling the JavaFX Theatre Hierarchy ===");

            // 1. Leaf Nodes (Controls)
            Label titleLabel = new Label("Student Search Portal");
            titleLabel.setId("header-label");

            Label promptLabel = new Label("Student ID:");
            TextField idInput = new TextField();
            idInput.setId("id-input");
            idInput.setPromptText("e.g. STU-101");

            Button searchButton = new Button("Search");
            searchButton.setId("search-btn");

            Button clearButton = new Button("Clear");
            clearButton.setId("clear-btn");

            Label resultLabel = new Label("Status: Ready for query");
            resultLabel.setId("status-label");

            // 2. Branch Nodes (Layout Containers)
            HBox inputBox = new HBox(10, promptLabel, idInput);
            inputBox.setId("input-row");
            inputBox.setAlignment(Pos.CENTER_LEFT);

            HBox actionBox = new HBox(10, searchButton, clearButton);
            actionBox.setId("action-row");
            actionBox.setAlignment(Pos.CENTER_LEFT);

            // 3. Root Node
            VBox root = new VBox(15, titleLabel, inputBox, actionBox, resultLabel);
            root.setId("root-container");
            root.setAlignment(Pos.TOP_LEFT);

            // 4. Scene (Window Canvas)
            Scene scene = new Scene(root, 480, 260);

            // 5. Stage (OS Window)
            primaryStage.setTitle("Aptech Student Portal - Scene Graph Demo");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Inspect and print the Scene Graph tree hierarchy
            System.out.println("\n--- Scene Graph Tree Hierarchy ---");
            printSceneGraph(scene.getRoot(), 0);

            primaryStage.show();
        }

        private void printSceneGraph(Node node, int depth) {
            String indent = "  ".repeat(depth);
            String idInfo = node.getId() != null ? " [id: #" + node.getId() + "]" : "";
            System.out.println(indent + "+-- " + node.getClass().getSimpleName() + idInfo);

            if (node instanceof Parent parent) {
                for (Node child : parent.getChildrenUnmodifiable()) {
                    printSceneGraph(child, depth + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(SceneGraphApp.class, args);
    }
}
