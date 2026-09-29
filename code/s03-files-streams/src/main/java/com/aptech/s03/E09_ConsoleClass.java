package com.aptech.s03;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Example 9 — The Console class: reading from a REAL terminal, including hidden passwords.
 * Run it twice: once with the green ▶ (IntelliJ), once from a terminal window (see the lesson).
 */
public class E09_ConsoleClass {
    public static void main(String[] args) {
        Console console = System.console();            // null when there is no real terminal

        if (console == null) {
            System.out.println("No console available (are you running inside an IDE?)");
            System.out.println("Falling back to Scanner - the password WILL be visible.");
            Scanner in = new Scanner(System.in);
            System.out.print("Username: ");
            String user = in.hasNextLine() ? in.nextLine() : "";
            System.out.print("Password: ");
            String pass = in.hasNextLine() ? in.nextLine() : "";
            System.out.println("Welcome, " + user + " (password length " + pass.length() + ")");
            return;
        }

        String user = console.readLine("Username: ");          // like Scanner, but with a prompt built in
        char[] pass = console.readPassword("Password: ");      // typing is HIDDEN, returns char[]
        console.printf("Welcome, %s (password length %d)%n", user, pass.length);
        Arrays.fill(pass, ' ');                                // wipe the password from memory
    }
}
