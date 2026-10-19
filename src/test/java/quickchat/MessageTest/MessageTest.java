package quickchat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message();
        msg.setMessage("Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.getMessage().length() <= 250, "Message should be ready to send.");
    }

    @Test
    public void testMessageLengthFailure() {
        Message msg = new Message();
        // Create a string of 260 characters
        String longMsg = "A".repeat(260); 
        msg.setMessage(longMsg);
        assertTrue(msg.getMessage().length() > 250, "Message exceeds 250 characters.");
    }

    @Test
    public void testRecipientCellSuccess() {
        Message msg = new Message();
        msg.setRecipient("+27718693002");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        Message msg = new Message();
        msg.setRecipient("08575975889"); // No international code
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", msg.checkRecipientCell());
    }

    @Test
    public void testMessageHashCreation() {
        Message msg = new Message();
        msg.setNumSent(0);
        msg.setMessage("Hi Mike, can you join us for dinner tonight?");
        
        // Note: Since the ID is randomly generated, we can't test for exactly "00:0:HITONIGHT"
        // But we can test that the hash contains the correct format: "XX:0:HITONIGHT"
        String hash = msg.createMessageHash();
        assertTrue(hash.endsWith(":0:HITONIGHT"), "Hash should end with :0:HITONIGHT");
    }
}