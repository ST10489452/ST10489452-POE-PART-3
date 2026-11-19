package com.mycompany.main;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Main class for the Chat Application - Parts 1, 2 and 3 combined.
 *
 * Console-only application. It only handles prompts and printing; all validation
 * lives in Login / Message and all message storage lives in MessageManager.
 *
 * Flow:
 *   1. Register (re-prompts until each field is valid) and log in.
 *   2. Load the stored messages from messages.json into the arrays.
 *   3. Menu: 1) Send Messages  2) Show recently sent messages  3) Quit
 *            4) Stored Messages (display, longest, search, delete, report)
 *
 * @author Mc Chopper
 * @version 3.0
 */
public class Main {

    private static final String STORAGE_FILE = "messages.json";

    private static final Scanner scanner = new Scanner(System.in);
    private static final MessageManager manager = new MessageManager();
    private static final JsonMessageStore store = new JsonMessageStore(STORAGE_FILE);

    // The logged-in user's cell number - used as the sender of every message
    private static String senderCell;

    /**
     * Entry point of the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("   Welcome to the Chat Application   ");
        System.out.println("======================================\n");

        Login login = registerUser();
        logIn(login);
        senderCell = login.getRegisteredCellPhone();

        System.out.println("\nWelcome to QuickChat.");
        loadStoredMessages();

        int numMessages = promptMessageCount();
        runMainMenu(numMessages);

        scanner.close();
    }

    // ==================================================================
    //  PART 1 - Registration and login
    // ==================================================================

    /** Collects and validates the registration details; returns the registered user. */
    private static Login registerUser() {
        String firstName = prompt("Enter your first name: ");
        String lastName = prompt("Enter your last name: ");
        Login login = new Login(firstName, lastName);

        System.out.println("\n--- Registration ---");
        String username = promptUntilValid(
                "Enter a username (must contain '_' and be max 5 characters): ",
                login::checkUserName, Login.USERNAME_ERROR);
        String password = promptUntilValid(
                "Enter a password (min 8 chars, uppercase, number, special char): ",
                login::checkPasswordComplexity, Login.PASSWORD_ERROR);
        String cellPhone = promptUntilValid(
                "Enter your cell phone number with international code (e.g. +27838968976): ",
                login::checkCellPhoneNumber, Login.CELL_ERROR);

        System.out.println(login.registerUser(username, password, cellPhone));
        return login;
    }

    /** Keeps asking until the user logs in successfully. */
    private static void logIn(Login login) {
        System.out.println("\n--- Login ---");
        boolean loggedIn = false;
        while (!loggedIn) {
            String username = prompt("Enter your username: ");
            String password = prompt("Enter your password: ");

            System.out.println(login.returnLoginStatus(username, password));
            loggedIn = login.loginUser(username, password);
        }
    }

    // ==================================================================
    //  PART 2 - QuickChat menu and sending messages
    // ==================================================================

    /** Asks how many messages the user wants to send each time they choose "Send Messages". */
    private static int promptMessageCount() {
        while (true) {
            try {
                int count = Integer.parseInt(prompt("\nHow many messages would you like to send? "));
                if (count > 0) {
                    return count;
                }
                System.out.println("Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    /** Shows the main menu until the user quits. */
    private static void runMainMenu(int numMessages) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");

            switch (prompt("Select an option: ")) {
                case "1":
                    sendMessages(numMessages);
                    break;
                case "2":
                    System.out.println(manager.displaySentMessages());
                    System.out.println("Total messages sent: " + manager.getTotalMessagesSent());
                    break;
                case "3":
                    running = false;
                    System.out.println("\nGoodbye!");
                    break;
                case "4":
                    runStoredMessagesMenu();
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2, 3, or 4.");
            }
        }
    }

    /**
     * Sends the given number of messages. A disregarded message does not count,
     * so the user is asked to enter that message again.
     */
    private static void sendMessages(int numMessages) {
        int completed = 0;
        while (completed < numMessages) {
            System.out.println("\n--- Message " + (completed + 1) + " of " + numMessages + " ---");

            String recipient = promptValidRecipient();
            String text = promptValidMessageText();

            Message message = manager.createMessage(senderCell, recipient, text);
            System.out.println("Message ID generated: " + message.getMessageID());
            System.out.println("Message Hash: " + message.getMessageHash());

            if (chooseMessageAction(message)) {
                completed++;
            }
        }

        System.out.println("\n======================================");
        System.out.println(manager.displaySentMessages());
        System.out.println("Total messages sent: " + manager.getTotalMessagesSent());
        System.out.println("======================================");
    }

    /** Re-prompts until the recipient's cell number is valid. */
    private static String promptValidRecipient() {
        while (true) {
            String recipient = prompt("Enter recipient cell number (with international code e.g. +27718693002): ");
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.equals(Message.RECIPIENT_CAPTURED)) {
                return recipient;
            }
        }
    }

    /** Re-prompts until the message is 250 characters or fewer. */
    private static String promptValidMessageText() {
        while (true) {
            String text = prompt("Enter your message (max 250 characters): ");
            String result = Message.validateMessage(text);
            System.out.println(result);
            if (result.equals(Message.MESSAGE_READY)) {
                return text;
            }
        }
    }

    /**
     * Lets the user send, disregard or store a message and records it in the arrays.
     *
     * @return true if the message was sent or stored; false if it was disregarded.
     */
    private static boolean chooseMessageAction(Message message) {
        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");

            switch (prompt("Select an option: ")) {
                case "1":
                    manager.addSentMessage(message);
                    System.out.println("Message successfully sent.");
                    return true;
                case "2":
                    if (confirmDelete()) {
                        manager.addDisregardedMessage(message);
                        System.out.println("Message disregarded. Please re-enter the message.");
                        return false;
                    }
                    break;
                case "3":
                    manager.addStoredMessage(message);
                    saveStoredMessages();
                    System.out.println("Message successfully stored.");
                    return true;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    /** Asks the user to press 0 to confirm that the message must be deleted. */
    private static boolean confirmDelete() {
        boolean confirmed = prompt("Press 0 to delete the message: ").equals("0");
        if (!confirmed) {
            System.out.println("Message not deleted.");
        }
        return confirmed;
    }

    // ==================================================================
    //  PART 3 - Stored Messages
    // ==================================================================

    /** Shows the "Stored Messages" sub-menu until the user goes back. */
    private static void runStoredMessagesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all messages for a recipient");
            System.out.println("e) Delete a message using the message hash");
            System.out.println("f) Display report of all stored messages");
            System.out.println("g) Back to main menu");

            switch (prompt("Select an option: ").toLowerCase()) {
                case "a":
                    System.out.println(manager.displaySendersAndRecipients());
                    break;
                case "b":
                    System.out.println(manager.displayLongestStoredMessage());
                    break;
                case "c":
                    System.out.println(manager.displayMessageByID(prompt("Enter the message ID: ")));
                    break;
                case "d":
                    System.out.println(manager.displayMessagesForRecipient(
                            prompt("Enter the recipient's cell number: ")));
                    break;
                case "e":
                    System.out.println(manager.deleteByMessageHash(prompt("Enter the message hash: ")));
                    saveStoredMessages();
                    break;
                case "f":
                    System.out.println(manager.generateReport());
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please select a to g.");
            }
        }
    }

    /** Reads messages.json into the Stored Messages array at start-up and tells the user. */
    private static void loadStoredMessages() {
        try {
            int loaded = 0;
            for (Message message : store.load()) {
                manager.addStoredMessage(message);
                loaded++;
            }
            System.out.println(loaded + " stored message(s) loaded from " + STORAGE_FILE + ".");
        } catch (IOException | RuntimeException e) {
            // RuntimeException covers a JSON file that is damaged or badly formatted
            System.out.println("Could not read " + STORAGE_FILE + ": " + e.getMessage());
        }
    }

    /** Writes the current stored messages back to messages.json. */
    private static void saveStoredMessages() {
        try {
            store.save(manager.getStoredMessageDetails());
        } catch (IOException e) {
            System.out.println("Could not save " + STORAGE_FILE + ": " + e.getMessage());
        }
    }

    // ==================================================================
    //  Input helpers
    // ==================================================================

    /** Prints a prompt and returns the trimmed line the user types. */
    private static String prompt(String text) {
        System.out.print(text);
        return scanner.nextLine().trim();
    }

    /** Asks until the answer passes the check, printing the error message each time it fails. */
    private static String promptUntilValid(String text, Predicate<String> isValid, String errorMessage) {
        while (true) {
            String answer = prompt(text);
            if (isValid.test(answer)) {
                return answer;
            }
            System.out.println(errorMessage);
        }
    }
}
