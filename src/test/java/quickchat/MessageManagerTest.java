package quickchat;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Part 3 unit tests - populated with the five test messages from the brief. */
class MessageManagerTest {

    private MessageManager manager;

    // Test data from the brief
    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!"; // brief's expected output has no space before "!"
    private static final String MSG5 = "Ok, I am leaving without you.";

    private static final String[] IDS = {"1111111111", "2222222222", "3333333333",
                                         "4444444444", "5555555555"};

    /** Builds a hash like 00:0:HITHANKS (ID first 2 digits : number : FIRSTLAST, letters only). */
    private static String hash(String id, int number, String text) {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z]", "");
        return (id.substring(0, 2) + ":" + number + ":" + first + last).toUpperCase();
    }

    @BeforeEach
    void setUp() {
        manager = new MessageManager();
        manager.addMessage(IDS[0], hash(IDS[0], 0, MSG1), "+27834557896", MSG1, MessageManager.SENT);
        manager.addMessage(IDS[1], hash(IDS[1], 1, MSG2), "+27838884567", MSG2, MessageManager.STORED);
        manager.addMessage(IDS[2], hash(IDS[2], 2, MSG3), "+27834484567", MSG3, MessageManager.DISREGARD);
        manager.addMessage(IDS[3], hash(IDS[3], 3, MSG4), "0838884567", MSG4, MessageManager.SENT);
        manager.addMessage(IDS[4], hash(IDS[4], 4, MSG5), "+27838884567", MSG5, MessageManager.STORED);
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertArrayEquals(new String[]{MSG1, MSG4}, manager.getSentMessages());
    }

    @Test
    void disregardedAndStoredArraysCorrectlyPopulated() {
        assertArrayEquals(new String[]{MSG3}, manager.getDisregardedMessages());
        assertArrayEquals(new String[]{MSG2, MSG5}, manager.getStoredMessages());
    }

    @Test
    void hashAndIdArraysPopulatedForAllMessages() {
        assertEquals(5, manager.getMessageHashes().length);
        assertEquals(5, manager.getMessageIDs().length);
        assertEquals("11:0:DIDCAKE", manager.getMessageHashes()[0]);
    }

    @Test
    void longestStoredMessageIsMessage2() {
        assertEquals(MSG2, manager.getLongestStoredMessage());
    }

    @Test
    void searchByMessageIDFindsMessage4() {
        String result = manager.searchByMessageID(IDS[3]);
        assertTrue(result.contains("0838884567"));
        assertTrue(result.contains(MSG4));
    }

    @Test
    void searchByMessageIDNotFound() {
        assertEquals("Message ID not found.", manager.searchByMessageID("0000000000"));
    }

    @Test
    void searchByRecipientReturnsMessages2And5() {
        assertArrayEquals(new String[]{MSG2, MSG5}, manager.searchByRecipient("+27838884567"));
    }

    @Test
    void searchByRecipientWithNoMessagesReturnsEmpty() {
        assertEquals(0, manager.searchByRecipient("+27000000000").length);
    }

    @Test
    void deleteByHashRemovesMessage2() {
        String result = manager.deleteByHash(hash(IDS[1], 1, MSG2));
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.", result);
        assertArrayEquals(new String[]{MSG5}, manager.getStoredMessages());
        assertEquals(4, manager.getMessageHashes().length);
        assertEquals("Message ID not found.", manager.searchByMessageID(IDS[1]));
    }

    @Test
    void deleteByUnknownHashFails() {
        assertEquals("Message hash not found.", manager.deleteByHash("XX:9:NOPE"));
    }

    @Test
    void reportListsSentMessagesWithHashRecipientAndMessage() {
        String report = manager.displayReport(MessageManager.SENT);
        assertTrue(report.contains(hash(IDS[0], 0, MSG1)));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(MSG1));
        assertTrue(report.contains(MSG4));
        assertFalse(report.contains(MSG2)); // stored, so not in the sent report
    }

    @Test
    void jsonRoundTripRestoresStoredMessages(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("storedMessages.json");
        manager.saveStoredToJson(file);

        MessageManager fresh = new MessageManager();
        assertEquals(2, fresh.loadStoredFromJson(file));
        assertArrayEquals(new String[]{MSG2, MSG5}, fresh.getStoredMessages());
        assertEquals(0, fresh.loadStoredFromJson(file)); // no duplicates on second load
    }

    @Test
    void loadingMissingJsonFileLoadsNothing(@TempDir Path tempDir) throws IOException {
        assertEquals(0, new MessageManager().loadStoredFromJson(tempDir.resolve("missing.json")));
    }
}
