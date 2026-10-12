package com.aptech.s13;

/**
 * Standard JavaFX Non-Modular Application Launcher
 *
 * Essential pattern for running JavaFX applications without module-path JVM flags.
 * By not extending Application directly, this launcher prevents java.exe's LauncherHelper
 * from throwing "Error: JavaFX runtime components are missing".
 */
public class AppLauncher {
    public static void main(String[] args) {
        System.out.println("[AppLauncher] Bootstrapping JavaFX Application via clean launcher class...");
        E05_ApplicationLifecycle.main(args);
    }
}
