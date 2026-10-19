package quickchat;

import java.util.Scanner;

/**
 * Console entry point for QuickChat.
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
        } // <--- Login loop ends here

        // === PART 2 & 3: THE MENU ===
        System.out.println("\nWelcome to QuickChat.");
        System.out.print("How many messages do you wish to enter? ");
        int numMessages = Integer.parseInt(scanner.nextLine());
        
        java.util.ArrayList<Message> allMessages = new java.util.ArrayList<>();
        
        boolean running = true;
        while (running) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

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
                        allMessages.add(msg); // Save to array
                        
                        System.out.println("\nOptions:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Choose: ");
                        int msgAction = Integer.parseInt(scanner.nextLine());
                        
                        if (msgAction == 1) {
                            System.out.println("Message successfully sent");
                        } else if (msgAction == 2) {
                            System.out.println("Press 0 to delete the message");
                        } else if (msgAction == 3) {
                            msg.storeMessage();
                        }
                        
                        System.out.println("\n" + msg.printMessages());
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("\n--- Stored Messages Menu ---");
                    System.out.println("a) Display sender and recipient of all stored messages");
                    System.out.println("b) Display the longest stored message");
                    System.out.println("c) Search for a message ID");
                    System.out.println("d) Search by recipient");
                    System.out.println("e) Delete a message using message hash");
                    System.out.println("f) Display report");
                    System.out.print("Choose an option (a-f): ");
                    String subChoice = scanner.nextLine().trim().toLowerCase();

                    switch (subChoice) {
                        case "a":
                            System.out.println("Display sender and recipient coming next...");
                            break;
                        case "b":
                            System.out.println("Longest message coming next...");
                            break;
                        case "c":
                            System.out.println("Search by ID coming next...");
                            break;
                        case "d":
                            System.out.println("Search by recipient coming next...");
                            break;
                        case "e":
                            System.out.println("Delete by hash coming next...");
                            break;
                        case "f":
                            System.out.println("Report coming next...");
                            break;
                        default:
                            System.out.println("Invalid sub-option.");
                    }
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close(); 
    } 
}