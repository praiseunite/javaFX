package com.aptech.s13;

/**
 * Session 13 - Example 1: Overview and Evolution of Java Desktop GUI Toolkits
 *
 * Demonstrates the contrast between the three generations of Java GUI toolkits:
 * AWT (1995) -> Swing (1998) -> JavaFX (Modern, Hardware-Accelerated).
 */
public class E01_OverviewAndEvolution {

    public static record GuiToolkit(
            String name,
            int yearIntroduced,
            String renderingEngine,
            String architecture,
            boolean hardwareAccelerated,
            boolean cssStylingSupported,
            String primaryLimitation
    ) {}

    public static void main(String[] args) {
        System.out.println("=== Java Desktop GUI Toolkit Evolution ===");

        GuiToolkit[] toolkits = {
            new GuiToolkit(
                "AWT (Abstract Window Toolkit)",
                1995,
                "Native OS Peers (Heavyweight)",
                "Immediate mode, native widgets",
                false,
                false,
                "Least Common Denominator: UI looks completely different across OSes and lacks rich controls."
            ),
            new GuiToolkit(
                "Swing (Java Foundation Classes)",
                1998,
                "Java 2D CPU Rasterizer (Lightweight)",
                "Pluggable Look-and-Feel, painted pixels",
                false,
                false,
                "CPU-bound rendering: No GPU acceleration, complex thread rules, rigid styling."
            ),
            new GuiToolkit(
                "JavaFX (Modern Desktop UI)",
                2008,
                "Prism GPU Pipeline (DirectX/OpenGL/Metal)",
                "Retained-mode Scene Graph with CSS & FXML",
                true,
                true,
                "Standalone library since Java 11: Requires module-path configuration or build tool."
            )
        };

        for (GuiToolkit tk : toolkits) {
            System.out.println("\nToolkit: " + tk.name());
            System.out.println("  Year: " + tk.yearIntroduced());
            System.out.println("  Engine: " + tk.renderingEngine());
            System.out.println("  Architecture: " + tk.architecture());
            System.out.println("  GPU Accelerated: " + (tk.hardwareAccelerated() ? "YES" : "NO"));
            System.out.println("  CSS Skinning: " + (tk.cssStylingSupported() ? "YES" : "NO"));
            System.out.println("  Key Consideration: " + tk.primaryLimitation());
        }

        System.out.println("\nConclusion:");
        System.out.println("JavaFX is the recommended, official standard for building rich client desktop applications in Java.");
    }
}
