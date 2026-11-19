package com.mycompany.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MessageManager class for the Chat Application - Part 3: Store Data and Display Task Report.
 *
 * Holds every array required by the brief and implements the "Stored Messages" features.
 *
 * ARRAY DESIGN
 * The brief names five arrays: Sent Messages, Disregarded Messages, Stored Messages,
 * Message Hash and Message ID. They are declared below under those names.
 *
 * Because the brief also asks for searches across PARALLEL arrays, every message is also
 * recorded in a set of "master" arrays (IDs, hashes, senders, recipients, texts, flags).
 * Index i of each master array always refers to the same message, so a message can be
 * found by ID, hash or recipient and its other details read from the same index.
 * Every add and every delete updates ALL the arrays together so they never go out of sync.
 *
 * @author Mc Chopper
 * @version 3.0
 */
public class MessageManager {

    // ---- Flags used to label each message (as named in the test data) ----
    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_STORED = "Stored";
    public static final String FLAG_DISREGARD = "Disregard";

    // ---- Messages shown to the user ----
    public static final String NO_STORED_MESSAGES = "No stored messages.";
    public static final String ID_NOT_FOUND = "Message ID not found.";
    public static final String HASH_NOT_FOUND = "Message hash not found.";

    private static final String DIVIDER = "-------------------------\n";

    // ---- The five arrays required by the brief ----
    private final ArrayList<String> sentMessages = new ArrayList<>();
    private final ArrayList<String> disregardedMessages = new ArrayList<>();
    private final ArrayList<String> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    // ---- Parallel arrays (same index = same message as messageIDs / messageHashes) ----
    private final ArrayList<String> messageSenders = new ArrayList<>();
    private final ArrayList<String> messageRecipients = new ArrayList<>();
    private final ArrayList<String> messageTexts = new ArrayList<>();
    private final ArrayList<String> messageFlags = new ArrayList<>();

    // The first message of a session is number 0 (as in the hash example 00:0:HITHANKS)
    private int nextMessageNumber = 0;

    // ------------------------------------------------------------------
    //  Creating and recording messages
    // ------------------------------------------------------------------

    /**
     * Creates a new message with a unique ID and the next message number.
     * The message is NOT recorded yet - call addSent/addStored/addDisregarded.
     *
     * @param sender    The sender's cell phone number.
     * @param recipient The recipient's cell phone number.
     * @param text      The message text.
     * @return The new message.
     */
    public Message createMessage(String sender, String recipient, String text) {
        return Message.create(generateUniqueMessageID(), nextMessageNumber++, sender, recipient, text);
    }

    /**
     * Generates a random 10-digit ID that is not already in use.
     *
     * @return A unique 10-digit message ID.
     */
    private String generateUniqueMessageID() {
        String id;
        do {
            id = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        } while (messageIDs.contains(id));
        return id;
    }

    /**
     * Records a message that was sent.
     *
     * @param message The message to record.
     */
    public void addSentMessage(Message message) {
        record(message, FLAG_SENT);
    }

    /**
     * Records a message that was stored to send later.
     *
     * @param message The message to record.
     */
    public void addStoredMessage(Message message) {
        record(message, FLAG_STORED);
    }

    /**
     * Records a message that the user disregarded.
     *
     * @param message The message to record.
     */
    public void addDisregardedMessage(Message message) {
        record(message, FLAG_DISREGARD);
    }

    /**
     * Adds a message to every array. This is the ONLY place arrays are added to,
     * which keeps all of them aligned.
     */
    private void record(Message message, String flag) {
        messageIDs.add(message.getMessageID());
        messageHashes.add(message.getMessageHash());
        messageSenders.add(message.getSender());
        messageRecipients.add(message.getRecipient());
        messageTexts.add(message.getMessageText());
        messageFlags.add(flag);
        categoryList(flag).add(message.getMessageText());
    }

    /** Returns the Sent / Stored / Disregarded array that matches a flag. */
    private ArrayList<String> categoryList(String flag) {
        switch (flag) {
            case FLAG_SENT:
                return sentMessages;
            case FLAG_STORED:
                return storedMessages;
            default:
                return disregardedMessages;
        }
    }

    // ------------------------------------------------------------------
    //  Reading the arrays
    // ------------------------------------------------------------------

    /** @return The text of every sent message. */
    public List<String> getSentMessages() { return Collections.unmodifiableList(sentMessages); }

    /** @return The text of every disregarded message. */
    public List<String> getDisregardedMessages() { return Collections.unmodifiableList(disregardedMessages); }

    /** @return The text of every stored message. */
    public List<String> getStoredMessages() { return Collections.unmodifiableList(storedMessages); }

    /** @return Every message hash. */
    public List<String> getMessageHashes() { return Collections.unmodifiableList(messageHashes); }

    /** @return Every message ID. */
    public List<String> getMessageIDs() { return Collections.unmodifiableList(messageIDs); }

    /** @return The total number of messages sent. */
    public int getTotalMessagesSent() { return sentMessages.size(); }

    /**
     * Rebuilds the stored messages as Message objects (used to save them to JSON).
     *
     * @return All stored messages with their full details.
     */
    public List<Message> getStoredMessageDetails() {
        List<Message> details = new ArrayList<>();
        for (int i : indexesWithFlag(FLAG_STORED)) {
            details.add(new Message(messageIDs.get(i), messageHashes.get(i), messageSenders.get(i),
                    messageRecipients.get(i), messageTexts.get(i)));
        }
        return details;
    }

    // ------------------------------------------------------------------
    //  Stored Messages menu features
    // ------------------------------------------------------------------

    /**
     * Feature (a): lists the sender and recipient of every stored message.
     *
     * @return The formatted list, or NO_STORED_MESSAGES.
     */
    public String displaySendersAndRecipients() {
        List<Integer> stored = indexesWithFlag(FLAG_STORED);
        if (stored.isEmpty()) {
            return NO_STORED_MESSAGES;
        }
        StringBuilder output = new StringBuilder("=== Stored Messages: Sender and Recipient ===\n");
        for (int i : stored) {
            output.append("Sender: ").append(messageSenders.get(i))
                  .append(" | Recipient: ").append(messageRecipients.get(i)).append("\n");
        }
        return output.toString();
    }

    /**
     * Feature (b): finds the longest stored message.
     *
     * @return The text of the longest stored message, or NO_STORED_MESSAGES.
     */
    public String getLongestStoredMessage() {
        int longest = indexOfLongestStoredMessage();
        return longest < 0 ? NO_STORED_MESSAGES : messageTexts.get(longest);
    }

    /**
     * Feature (b) for display: the longest stored message with its full details.
     *
     * @return The formatted details, or NO_STORED_MESSAGES.
     */
    public String displayLongestStoredMessage() {
        int longest = indexOfLongestStoredMessage();
        if (longest < 0) {
            return NO_STORED_MESSAGES;
        }
        return "=== Longest Stored Message ===\n" + formatEntry(longest);
    }

    /**
     * Feature (c): searches every message by its ID.
     *
     * @param messageID The message ID to look for.
     * @return The message text, or ID_NOT_FOUND.
     */
    public String searchByMessageID(String messageID) {
        int index = messageIDs.indexOf(messageID.trim());
        return index < 0 ? ID_NOT_FOUND : messageTexts.get(index);
    }

    /**
     * Feature (c) for display: the recipient and message for a message ID.
     *
     * @param messageID The message ID to look for.
     * @return "Recipient: ... / Message: ...", or ID_NOT_FOUND.
     */
    public String displayMessageByID(String messageID) {
        int index = messageIDs.indexOf(messageID.trim());
        if (index < 0) {
            return ID_NOT_FOUND;
        }
        return "Recipient: " + messageRecipients.get(index) + "\nMessage: " + messageTexts.get(index);
    }

    /**
     * Feature (d): finds every SENT or STORED message for a recipient.
     *
     * @param recipient The recipient's cell phone number.
     * @return The matching message texts, in the order they were recorded.
     */
    public List<String> searchByRecipient(String recipient) {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < messageRecipients.size(); i++) {
            boolean sameRecipient = messageRecipients.get(i).equals(recipient.trim());
            boolean sentOrStored = !FLAG_DISREGARD.equals(messageFlags.get(i));
            if (sameRecipient && sentOrStored) {
                matches.add(messageTexts.get(i));
            }
        }
        return matches;
    }

    /**
     * Feature (d) for display: the messages for a recipient as a numbered list.
     *
     * @param recipient The recipient's cell phone number.
     * @return The formatted list, or a "no messages found" line.
     */
    public String displayMessagesForRecipient(String recipient) {
        List<String> matches = searchByRecipient(recipient);
        if (matches.isEmpty()) {
            return "No messages found for " + recipient.trim() + ".";
        }
        StringBuilder output = new StringBuilder("=== Messages for " + recipient.trim() + " ===\n");
        for (int i = 0; i < matches.size(); i++) {
            output.append(i + 1).append(". ").append(matches.get(i)).append("\n");
        }
        return output.toString();
    }

    /**
     * Feature (e): deletes a message using its hash. The message is removed from
     * EVERY array so the parallel arrays stay aligned.
     *
     * @param messageHash The hash of the message to delete (not case sensitive).
     * @return A confirmation message, or HASH_NOT_FOUND.
     */
    public String deleteByMessageHash(String messageHash) {
        int index = messageHashes.indexOf(messageHash.trim().toUpperCase());
        if (index < 0) {
            return HASH_NOT_FOUND;
        }

        String text = messageTexts.get(index);
        categoryList(messageFlags.get(index)).remove(text);

        messageIDs.remove(index);
        messageHashes.remove(index);
        messageSenders.remove(index);
        messageRecipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        return "Message: \"" + withoutTrailingPeriod(text) + "\" successfully deleted.";
    }

    /**
     * Feature (f): the full details of all sent and stored messages.
     *
     * @return The report text (hash, ID, sender, recipient and message for each).
     */
    public String generateReport() {
        return "=== MESSAGE REPORT ===\n"
                + buildSection("Sent Messages", FLAG_SENT)
                + buildSection("Stored Messages", FLAG_STORED);
    }

    /**
     * Shows every sent message (used by "Show recently sent messages").
     *
     * @return The formatted list of sent messages.
     */
    public String displaySentMessages() {
        return buildSection("All Sent Messages", FLAG_SENT);
    }

    // ------------------------------------------------------------------
    //  Private helpers
    // ------------------------------------------------------------------

    /** Returns the master-array positions of every message with the given flag. */
    private List<Integer> indexesWithFlag(String flag) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (flag.equals(messageFlags.get(i))) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /** Returns the position of the longest stored message, or -1 if none are stored. */
    private int indexOfLongestStoredMessage() {
        int longest = -1;
        for (int i : indexesWithFlag(FLAG_STORED)) {
            if (longest < 0 || messageTexts.get(i).length() > messageTexts.get(longest).length()) {
                longest = i;
            }
        }
        return longest;
    }

    /** Builds a titled block listing every message with the given flag. */
    private String buildSection(String title, String flag) {
        StringBuilder section = new StringBuilder("--- " + title + " ---\n");
        List<Integer> indexes = indexesWithFlag(flag);
        if (indexes.isEmpty()) {
            return section.append("None.\n").toString();
        }
        for (int i : indexes) {
            section.append(formatEntry(i)).append(DIVIDER);
        }
        return section.toString();
    }

    /** Formats the full details of the message at the given position. */
    private String formatEntry(int index) {
        return "Message Hash: " + messageHashes.get(index) + "\n"
                + "Message ID: " + messageIDs.get(index) + "\n"
                + "Sender: " + messageSenders.get(index) + "\n"
                + "Recipient: " + messageRecipients.get(index) + "\n"
                + "Message: " + messageTexts.get(index) + "\n";
    }

    /** Removes one trailing full stop, so the delete confirmation matches the required wording. */
    private String withoutTrailingPeriod(String text) {
        return text.endsWith(".") ? text.substring(0, text.length() - 1) : text;
    }
}
