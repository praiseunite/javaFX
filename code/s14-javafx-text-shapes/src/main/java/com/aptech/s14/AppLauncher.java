package com.aptech.s14;

/**
 * Standard JavaFX Non-Modular Application Launcher for Session 14.
 *
 * Prevents the LauncherHelper from throwing "Error: JavaFX runtime components are missing"
 * when running without modular command-line flags.
 */
public class AppLauncher {
    public static void main(String[] args) {
        System.out.println("[AppLauncher] Bootstrapping Session 14: Text, Transforms & Shapes...");
        com.aptech.s14.lab.ShapeStudioApp.main(args);
    }
}
