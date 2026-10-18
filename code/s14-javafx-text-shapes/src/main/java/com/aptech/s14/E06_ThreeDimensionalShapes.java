package com.aptech.s14;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 * Session 14 - Example 6: Three-Dimensional Shapes, Phong Shading and Lighting
 *
 * Demonstrates:
 * 1. JavaFX 3D Mesh Primitives: Box, Cylinder, and Sphere.
 * 2. PhongMaterial: Diffuse reflection color, specular shine highlight, and specular power.
 * 3. 3D Lighting engines: PointLight (directional omni-source) and AmbientLight (fill light).
 * 4. PerspectiveCamera: True 3D perspective projection with vanishing points.
 * 5. Depth Buffer requirement: new Scene(root, width, height, true) to enable the GPU Z-buffer.
 * 6. 3D Rotation transforms around X, Y, and Z axes.
 */
public class E06_ThreeDimensionalShapes {

    public static class Shape3DApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        System.out.println("=== JavaFX 3D Shapes & Phong Shading Inspection ===");

        // 1. Box with Gold Phong Material
        Box box = new Box(75, 75, 75);
        PhongMaterial goldMaterial = new PhongMaterial();
        goldMaterial.setDiffuseColor(Color.GOLD);
        goldMaterial.setSpecularColor(Color.WHITE);
        goldMaterial.setSpecularPower(48.0);
        box.setMaterial(goldMaterial);
        box.setTranslateX(120);
        box.setTranslateY(180);
        box.getTransforms().addAll(
            new Rotate(25, Rotate.X_AXIS),
            new Rotate(35, Rotate.Y_AXIS)
        );

        // 2. Cylinder with Emerald Phong Material
        Cylinder cylinder = new Cylinder(38, 90);
        PhongMaterial emeraldMaterial = new PhongMaterial();
        emeraldMaterial.setDiffuseColor(Color.rgb(16, 185, 129));
        emeraldMaterial.setSpecularColor(Color.WHITE);
        emeraldMaterial.setSpecularPower(32.0);
        cylinder.setMaterial(emeraldMaterial);
        cylinder.setTranslateX(280);
        cylinder.setTranslateY(180);
        cylinder.getTransforms().add(new Rotate(20, Rotate.X_AXIS));

        // 3. Sphere with Sapphire Phong Material
        Sphere sphere = new Sphere(45, 64);
        PhongMaterial sapphireMaterial = new PhongMaterial();
        sapphireMaterial.setDiffuseColor(Color.rgb(59, 130, 246));
        sapphireMaterial.setSpecularColor(Color.WHITE);
        sapphireMaterial.setSpecularPower(64.0);
        sphere.setMaterial(sapphireMaterial);
        sphere.setTranslateX(440);
        sphere.setTranslateY(180);

        // 4. Lighting Rig
        // Point Light positioned in front and above the shapes
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(250);
        pointLight.setTranslateY(80);
        pointLight.setTranslateZ(-300);

        // Ambient Fill Light to illuminate shadow sides
        AmbientLight ambientLight = new AmbientLight(Color.rgb(80, 80, 80));

        // Assemble 3D Scene Root
        Group root3D = new Group(box, cylinder, sphere, pointLight, ambientLight);

        // Configure Perspective Camera
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);

        // IMPORTANT: depthBuffer must be TRUE for 3D z-ordering!
        Scene scene = new Scene(root3D, 580, 360, true);
        scene.setFill(Color.rgb(15, 23, 42)); // Deep slate background
        scene.setCamera(camera);

        System.out.println("3D Box: " + box + " with material: " + goldMaterial);
        System.out.println("3D Cylinder: " + cylinder + " with material: " + emeraldMaterial);
        System.out.println("3D Sphere: " + sphere + " with material: " + sapphireMaterial);
        System.out.println("PointLight positioned at: (" + pointLight.getTranslateX() + ", " + pointLight.getTranslateY() + ", " + pointLight.getTranslateZ() + ")");
        System.out.println("Scene DepthBuffer Enabled: true");

        primaryStage.setTitle("JavaFX 3D Primitives & Phong Lighting Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    }

    public static void main(String[] args) {
        Application.launch(Shape3DApp.class, args);
    }
}
