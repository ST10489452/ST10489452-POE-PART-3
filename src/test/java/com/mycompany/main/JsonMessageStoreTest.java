package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for reading and writing the stored-messages JSON file. */
class JsonMessageStoreTest {

    private static final String SENDER = "+27831112222";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    @TempDir
    Path tempDir;

    @Test
    void savedMessagesAreReadBackIntoTheStoredArray() throws IOException {
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("messages.json").toString());
        List<Message> toSave = List.of(
                Message.create("1000000002", 1, SENDER, "+27838884567", TEXT_2),
                Message.create("1000000005", 4, SENDER, "+27838884567", TEXT_5));
        store.save(toSave);

        MessageManager manager = new MessageManager();
        for (Message message : store.load()) {
            manager.addStoredMessage(message);
        }

        assertEquals(List.of(TEXT_2, TEXT_5), manager.getStoredMessages());
        assertEquals(List.of("1000000002", "1000000005"), manager.getMessageIDs());
        assertEquals(List.of("10:1:WHERETIME.", "10:4:OK,YOU."), manager.getMessageHashes());
    }

    @Test
    void everyFieldSurvivesTheRoundTrip() throws IOException {
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("messages.json").toString());
        store.save(List.of(Message.create("1000000002", 1, SENDER, "+27838884567", TEXT_2)));

        Message loaded = store.load().get(0);

        assertEquals("1000000002", loaded.getMessageID());
        assertEquals("10:1:WHERETIME.", loaded.getMessageHash());
        assertEquals(SENDER, loaded.getSender());
        assertEquals("+27838884567", loaded.getRecipient());
        assertEquals(TEXT_2, loaded.getMessageText());
    }

    @Test
    void messagesContainingQuotesSurviveTheRoundTrip() throws IOException {
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("messages.json").toString());
        String quoted = "He said \"call me\" at noon";
        store.save(List.of(Message.create("1000000009", 0, SENDER, "+27834557896", quoted)));

        assertEquals(quoted, store.load().get(0).getMessageText());
    }

    @Test
    void missingFileGivesAnEmptyArray() throws IOException {
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("missing.json").toString());
        assertEquals(new ArrayList<Message>(), new ArrayList<>(store.load()));
    }
}
