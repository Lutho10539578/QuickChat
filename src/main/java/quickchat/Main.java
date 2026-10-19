package quickchat;

import java.util.Scanner;

/**
 * Console entry point for QuickChat (Part 1): register, then log in.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== QuickChat Registration ===");

        System.out.print("Enter your first name: ");
        login.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter your last name: ");
        login.setLastName(scanner.nextLine().trim());

        // Keep asking until each detail passes its check.
        do {
            System.out.print("Enter a username (underscore, max 5 characters): ");
            login.setUsername(scanner.nextLine().trim());
            System.out.println(login.getUsernameMessage());
        } while (!login.checkUserName());

        do {
            System.out.print("Enter a password: ");
            login.setPassword(scanner.nextLine());
            System.out.println(login.getPasswordMessage());
        } while (!login.checkPasswordComplexity());

        do {
            System.out.print("Enter your cell number with international code (e.g. +27831234567): ");
            login.setCellPhone(scanner.nextLine().trim());
            System.out.println(login.getCellPhoneMessage());
        } while (!login.checkCellPhoneNumber());

        System.out.println(login.registerUser());

        System.out.println();
        System.out.println("=== QuickChat Login ===");

        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Username: ");
            String enteredUsername = scanner.nextLine().trim();
            System.out.print("Password: ");
            String enteredPassword = scanner.nextLine();

            loggedIn = login.loginUser(enteredUsername, enteredPassword);
            System.out.println(login.returnLoginStatus(loggedIn));
        }

        scanner.close();
    }
}
