package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for the Message class (Part 2 features). */
class MessageTest {

    @Test
    void messageHashUsesIdMessageNumberAndFirstAndLastWord() {
        assertEquals("00:0:HITHANKS", Message.createMessageHash("0012345678", 0, "Hi Thanks"));
    }

    @Test
    void messageHashWithOneWordRepeatsTheWord() {
        assertEquals("00:3:HELLOHELLO", Message.createMessageHash("0012345678", 3, "Hello"));
    }

    @Test
    void messageIDOfTenCharactersIsAccepted() {
        Message message = Message.create("1234567890", 0, "+27831112222", "+27834557896", "Hi there");
        assertEquals(true, message.checkMessageID());
    }

    @Test
    void messageIDLongerThanTenCharactersIsRejected() {
        Message message = Message.create("12345678901", 0, "+27831112222", "+27834557896", "Hi there");
        assertEquals(false, message.checkMessageID());
    }

    @Test
    void validRecipientIsCaptured() {
        assertEquals(Message.RECIPIENT_CAPTURED, Message.checkRecipientCell("+27718693002"));
    }

    @Test
    void recipientWithoutInternationalCodeIsRejected() {
        assertEquals(Message.RECIPIENT_ERROR, Message.checkRecipientCell("08575975889"));
    }

    @Test
    void messageOf250CharactersIsReadyToSend() {
        assertEquals(Message.MESSAGE_READY, Message.validateMessage("a".repeat(250)));
    }

    @Test
    void messageOver250CharactersReportsTheExcess() {
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.validateMessage("a".repeat(260)));
    }
}
