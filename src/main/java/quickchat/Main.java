package quickchat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Console entry point for QuickChat.
 * Part 1: registration and login. Part 2: sending messages.
 * Part 3: arrays plus the Stored Messages menu (see MessageManager).
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== QuickChat Registration ===");

      System.out.print("Enter your first name: ");
String firstName = scanner.nextLine().trim();
login.setFirstName(firstName);
System.out.print("Enter your last name: ");
String lastName = scanner.nextLine().trim();
login.setLastName(lastName);

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

        System.out.println("\n=== QuickChat Login ===");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Username: ");
            String enteredUsername = scanner.nextLine().trim();
            System.out.print("Password: ");
            String enteredPassword = scanner.nextLine();

            loggedIn = login.loginUser(enteredUsername, enteredPassword);
            System.out.println(login.returnLoginStatus(loggedIn));
        } // Login loop ends here

        // === PART 2 & 3: THE MENU ===
        System.out.println("\nWelcome to QuickChat.");
        int numMessages = readInt(scanner, "How many messages do you wish to enter? ");

        java.util.ArrayList<Message> allMessages = new java.util.ArrayList<>();

        // Part 3: parallel arrays live in the manager; stored messages are kept in a JSON file
        MessageManager manager = new MessageManager();
        Path storedFile = Path.of("storedMessages.json");
        try {
            manager.loadStoredFromJson(storedFile);
        } catch (IOException | com.google.gson.JsonParseException e) {
            System.out.println("Could not read storedMessages.json: " + e.getMessage());
        }
        String senderName = firstName + " " + lastName;

        boolean running = true;
        while (running) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int choice = readInt(scanner, "Choose an option: ");

            switch (choice) {
                case 1:
                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                        System.out.print("Enter recipient cell number (e.g. +27838968976): ");
                        String recipient = scanner.nextLine().trim();

                        System.out.print("Enter message (max 250 chars): ");
                        String text = scanner.nextLine().trim();

                        if (text.length() > 250) {
                            System.out.println("Please enter a message of less than 250 characters.");
                            continue;
                        }

                        Message msg = new Message();
                        msg.setNumSent(i + 1);
                        msg.setRecipient(recipient);
                        msg.setMessage(text);

                        String cellCheck = msg.checkRecipientCell();
                        if (!cellCheck.equals("Cell phone number successfully captured.")) {
                            System.out.println(cellCheck);
                            continue;
                        }

                        msg.createMessageHash();
                        allMessages.add(msg);

                        System.out.println("\nOptions:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        int msgAction = readInt(scanner, "Choose: ");

                        if (msgAction == 1) {
                            System.out.println("Message successfully sent");
                            manager.addMessage(msg.getMessageID(), msg.getMessageHash(),
                                    recipient, text, MessageManager.SENT);
                        } else if (msgAction == 2) {
                            System.out.println("Press 0 to delete the message");
                            manager.addMessage(msg.getMessageID(), msg.getMessageHash(),
                                    recipient, text, MessageManager.DISREGARD);
                        } else if (msgAction == 3) {
                            msg.storeMessage(); // Part 2 JSON storing
                            manager.addMessage(msg.getMessageID(), msg.getMessageHash(),
                                    recipient, text, MessageManager.STORED);
                            saveStoredMessages(manager, storedFile); // keep file in step with the array
                        } else {
                            System.out.println("Invalid choice, message not processed.");
                        }

                        System.out.println("\n" + msg.printMessages());
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                case 4:
                    StoredMessagesMenu.run(scanner, manager, senderName);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    /** Keeps asking until the user types a whole number, so letters never crash the app. */
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    /** Writes every stored message in the array to the JSON file. */
    private static void saveStoredMessages(MessageManager manager, Path file) {
        try {
            manager.saveStoredToJson(file);
        } catch (IOException e) {
            System.out.println("Could not save storedMessages.json: " + e.getMessage());
        }
    }
}