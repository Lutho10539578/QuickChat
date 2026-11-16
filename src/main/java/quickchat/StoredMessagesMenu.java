package quickchat;

import java.util.Scanner;

/** Console submenu for main-menu option 4 "Stored Messages" (no GUI, no JOptionPane). */
public class StoredMessagesMenu {

    /** Runs until the user chooses 7 (back). */
    public static void run(Scanner scanner, MessageManager manager, String senderName) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display report of all stored messages");
            System.out.println("7) Back to main menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println(manager.displayStoredSenderAndRecipient(senderName));
                    break;
                case "2":
                    String longest = manager.getLongestStoredMessage();
                    System.out.println(longest.isEmpty() ? "No stored messages." : "Longest message: " + longest);
                    break;
                case "3":
                    System.out.print("Enter message ID: ");
                    System.out.println(manager.searchByMessageID(scanner.nextLine().trim()));
                    break;
                case "4":
                    System.out.print("Enter recipient number: ");
                    String[] found = manager.searchByRecipient(scanner.nextLine().trim());
                    if (found.length == 0) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String message : found) {
                        System.out.println("- " + message);
                    }
                    break;
                case "5":
                    System.out.print("Enter message hash: ");
                    System.out.println(manager.deleteByHash(scanner.nextLine().trim().toUpperCase()));
                    saveQuietly(manager); // keep the JSON file in step with the array
                    break;
                case "6":
                    System.out.println(manager.displayReport(MessageManager.STORED));
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please choose 1-7.");
            }
        }
    }

    private static void saveQuietly(MessageManager manager) {
        try {
            manager.saveStoredToJson(java.nio.file.Path.of("storedMessages.json"));
        } catch (java.io.IOException e) {
            System.out.println("Could not update storedMessages.json: " + e.getMessage());
        }
    }
}
