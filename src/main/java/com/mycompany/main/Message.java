package com.mycompany.main;

/**
 * Message class for the Chat Application - Part 2: Sending Messages.
 *
 * A Message holds the details of ONE message (ID, hash, sender, recipient and text).
 * It keeps no static state: the lists of sent, stored and disregarded messages are
 * held by MessageManager.
 *
 * The field names match the keys used in the stored-messages JSON file,
 * so Gson can read and write this class directly.
 *
 * @author Mc Chopper
 * @version 3.0
 */
public class Message {

    // ---- Messages shown to the user ----
    public static final String RECIPIENT_CAPTURED = "Cell phone number successfully captured.";
    public static final String RECIPIENT_ERROR =
            "Cell phone number is incorrectly formatted or does not contain an international code. "
            + "Please correct the number and try again.";
    public static final String MESSAGE_READY = "Message ready to send.";

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MAX_ID_LENGTH = 10;
    private static final int ID_PREFIX_LENGTH = 2;

    private final String messageID;
    private final String messageHash;
    private final String sender;
    private final String recipient;
    private final String messageText;

    /**
     * Creates a message with an already-known hash (used when loading from JSON
     * and when rebuilding stored messages).
     *
     * @param messageID   The message ID.
     * @param messageHash The message hash.
     * @param sender      The sender's cell phone number.
     * @param recipient   The recipient's cell phone number.
     * @param messageText The message text.
     */
    public Message(String messageID, String messageHash, String sender,
                   String recipient, String messageText) {
        this.messageID = messageID;
        this.messageHash = messageHash;
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    /**
     * Creates a brand-new message and generates its hash.
     *
     * @param messageID     The message ID.
     * @param messageNumber The message's number in the sequence (first message is 0).
     * @param sender        The sender's cell phone number.
     * @param recipient     The recipient's cell phone number.
     * @param messageText   The message text.
     * @return The new message with its hash already created.
     */
    public static Message create(String messageID, int messageNumber, String sender,
                                 String recipient, String messageText) {
        String hash = createMessageHash(messageID, messageNumber, messageText);
        return new Message(messageID, hash, sender, recipient, messageText);
    }

    /**
     * Creates the Message Hash: first two digits of the ID, the message number,
     * then the first and last word of the message - all in uppercase.
     *
     * Example: ID 0012345678, number 0, "Hi Thanks" gives "00:0:HITHANKS".
     *
     * @param messageID     The message ID (at least two characters).
     * @param messageNumber The message's number in the sequence.
     * @param messageText   The message text.
     * @return The message hash in uppercase.
     */
    public static String createMessageHash(String messageID, int messageNumber, String messageText) {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String hash = messageID.substring(0, ID_PREFIX_LENGTH)
                + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    /**
     * Checks that this message's ID is no more than ten characters long.
     *
     * @return true if the ID is 10 characters or fewer.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MAX_ID_LENGTH;
    }

    /**
     * Validates a recipient cell number (same rules as the registration number).
     *
     * @param recipient The recipient's cell phone number.
     * @return RECIPIENT_CAPTURED if valid, otherwise RECIPIENT_ERROR.
     */
    public static String checkRecipientCell(String recipient) {
        return Login.isValidCellNumber(recipient) ? RECIPIENT_CAPTURED : RECIPIENT_ERROR;
    }

    /**
     * Validates the message length (maximum 250 characters).
     *
     * @param messageText The message text.
     * @return MESSAGE_READY if valid, otherwise how many characters it is over by.
     */
    public static String validateMessage(String messageText) {
        int excess = messageText.length() - MAX_MESSAGE_LENGTH;
        if (excess <= 0) {
            return MESSAGE_READY;
        }
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /** @return The 10-digit message ID. */
    public String getMessageID() { return messageID; }

    /** @return The message hash. */
    public String getMessageHash() { return messageHash; }

    /** @return The sender's cell phone number. */
    public String getSender() { return sender; }

    /** @return The recipient's cell phone number. */
    public String getRecipient() { return recipient; }

    /** @return The message text. */
    public String getMessageText() { return messageText; }
}
