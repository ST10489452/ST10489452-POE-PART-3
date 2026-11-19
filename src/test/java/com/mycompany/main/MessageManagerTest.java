package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Part 3 (MessageManager), using the test data from the brief.
 *
 * Messages 1-3 come from the brief. Messages 4 and 5 are not shown in the brief's
 * test-data table, so they are taken from the expected results of the unit-test table:
 *   Message 4: recipient 0838884567, "It is dinner time!",            flag Sent
 *   Message 5: recipient +27838884567, "Ok, I am leaving without you.", flag Stored
 * Fixed IDs are used so the hashes and the report are predictable.
 */
class MessageManagerTest {

    private static final String SENDER = "+27831112222";

    private static final String TEXT_1 = "Did you get the cake?";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private MessageManager manager;
    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    /** Loads test messages 1-4 into a fresh manager before every test. */
    @BeforeEach
    void setUp() {
        manager = new MessageManager();
        message1 = Message.create("1000000001", 0, SENDER, "+27834557896", TEXT_1);
        message2 = Message.create("1000000002", 1, SENDER, "+27838884567", TEXT_2);
        message3 = Message.create("1000000003", 2, SENDER, "+27834484567", TEXT_3);
        message4 = Message.create("1000000004", 3, SENDER, "0838884567", TEXT_4);
        message5 = Message.create("1000000005", 4, SENDER, "+27838884567", TEXT_5);

        manager.addSentMessage(message1);
        manager.addStoredMessage(message2);
        manager.addDisregardedMessage(message3);
        manager.addSentMessage(message4);
    }

    // ---- Brief test 1: Sent Messages array correctly populated ----

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_1, TEXT_4), manager.getSentMessages());
    }

    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_3), manager.getDisregardedMessages());
    }

    @Test
    void storedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(TEXT_2), manager.getStoredMessages());
    }

    @Test
    void messageIDAndHashArraysCorrectlyPopulated() {
        assertEquals(List.of("1000000001", "1000000002", "1000000003", "1000000004"),
                manager.getMessageIDs());
        assertEquals(List.of("10:0:DIDCAKE?", "10:1:WHERETIME.", "10:2:YOHOOOO,GATE.", "10:3:ITTIME!"),
                manager.getMessageHashes());
    }

    // ---- Brief test 2: Display the longest message ----

    @Test
    void longestStoredMessageIsReturned() {
        assertEquals(TEXT_2, manager.getLongestStoredMessage());
    }

    @Test
    void longestStoredMessageWhenNothingStored() {
        assertEquals(MessageManager.NO_STORED_MESSAGES, new MessageManager().getLongestStoredMessage());
    }

    // ---- Brief test 3: Search for message ID ----

    @Test
    void searchForMessageIDReturnsMessage() {
        assertEquals(TEXT_4, manager.searchByMessageID(message4.getMessageID()));
    }

    @Test
    void searchForUnknownMessageID() {
        assertEquals(MessageManager.ID_NOT_FOUND, manager.searchByMessageID("9999999999"));
    }

    @Test
    void displayMessageByIDShowsRecipientAndMessage() {
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.displayMessageByID(message4.getMessageID()));
    }

    // ---- Brief test 4: Search all messages sent or stored for a recipient ----

    @Test
    void searchForRecipientReturnsSentAndStoredMessages() {
        manager.addStoredMessage(message5);
        assertEquals(List.of(TEXT_2, TEXT_5), manager.searchByRecipient("+27838884567"));
    }

    @Test
    void searchForRecipientIgnoresDisregardedMessages() {
        assertEquals(new ArrayList<String>(), manager.searchByRecipient("+27834484567"));
    }

    // ---- Brief test 5: Delete a message using a message hash ----

    @Test
    void deleteMessageUsingHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time\" successfully deleted.",
                manager.deleteByMessageHash(message2.getMessageHash()));
    }

    @Test
    void deleteRemovesMessageFromEveryArray() {
        manager.deleteByMessageHash(message2.getMessageHash());

        assertEquals(new ArrayList<String>(), manager.getStoredMessages());
        assertEquals(List.of("1000000001", "1000000003", "1000000004"), manager.getMessageIDs());
        assertEquals(List.of("10:0:DIDCAKE?", "10:2:YOHOOOO,GATE.", "10:3:ITTIME!"),
                manager.getMessageHashes());
    }

    @Test
    void parallelArraysStayAlignedAfterDelete() {
        manager.deleteByMessageHash(message2.getMessageHash());

        // Message 4 moved up one position in every array but must still match its own ID
        assertEquals(TEXT_4, manager.searchByMessageID(message4.getMessageID()));
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.displayMessageByID(message4.getMessageID()));
    }

    @Test
    void deleteUnknownHash() {
        assertEquals(MessageManager.HASH_NOT_FOUND, manager.deleteByMessageHash("NO:SUCH:HASH"));
    }

    // ---- Brief test 6: Display report ----

    @Test
    void reportShowsHashRecipientAndMessage() {
        MessageManager reportManager = new MessageManager();
        reportManager.addSentMessage(message1);
        reportManager.addStoredMessage(message2);

        String expected = String.join("\n",
                "=== MESSAGE REPORT ===",
                "--- Sent Messages ---",
                "Message Hash: 10:0:DIDCAKE?",
                "Message ID: 1000000001",
                "Sender: +27831112222",
                "Recipient: +27834557896",
                "Message: Did you get the cake?",
                "-------------------------",
                "--- Stored Messages ---",
                "Message Hash: 10:1:WHERETIME.",
                "Message ID: 1000000002",
                "Sender: +27831112222",
                "Recipient: +27838884567",
                "Message: " + TEXT_2,
                "-------------------------",
                "");
        assertEquals(expected, reportManager.generateReport());
    }

    // ---- Extra tests: other Stored Messages features ----

    @Test
    void sendersAndRecipientsOfStoredMessages() {
        assertEquals("=== Stored Messages: Sender and Recipient ===\n"
                        + "Sender: +27831112222 | Recipient: +27838884567\n",
                manager.displaySendersAndRecipients());
    }

    @Test
    void totalMessagesSentCountsOnlySentMessages() {
        assertEquals(2, manager.getTotalMessagesSent());
    }

    @Test
    void createdMessagesGetUniqueTenDigitIDs() {
        MessageManager idManager = new MessageManager();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Message message = idManager.createMessage(SENDER, "+27834557896", "Hello there");
            idManager.addSentMessage(message);
            ids.add(message.getMessageID());
            assertEquals(10, message.getMessageID().length());
        }
        assertEquals(200, new HashSet<>(ids).size());
    }

    @Test
    void createdMessagesAreNumberedFromZero() {
        MessageManager numberManager = new MessageManager();
        Message first = numberManager.createMessage(SENDER, "+27834557896", "Hello there");
        Message second = numberManager.createMessage(SENDER, "+27834557896", "Hello again");

        assertEquals("0", first.getMessageHash().split(":")[1]);
        assertEquals("1", second.getMessageHash().split(":")[1]);
    }
}
