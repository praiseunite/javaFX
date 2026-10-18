package com.aptech.s15;

/**
 * Standard JavaFX Non-Modular Application Launcher for Session 15.
 *
 * Prevents the LauncherHelper from throwing "Error: JavaFX runtime components are missing"
 * when running without modular command-line flags.
 */
public class AppLauncher {
    public static void main(String[] args) {
        System.out.println("[AppLauncher] Bootstrapping Session 15: Layouts, CSS, Controls & Charts...");
        com.aptech.s15.lab.RegistrarDashboardApp.main(args);
    }
}
