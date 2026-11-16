package quickchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Part 3: holds every message in parallel arrays and offers the
 * search / delete / report features required by the "Stored Messages" menu.
 *
 * Parallel arrays: index i in messageIDs, messageHashes, recipients,
 * messages and flags all describe the SAME message.
 *
 * JSON reading/writing uses Gson.
 * Reference (Harvard - check your own format and add the date you accessed it):
 * Google (2023) Gson User Guide. Available at:
 * https://github.com/google/gson/blob/main/UserGuide.md (Accessed: <16 November 2026>).
 */
public class MessageManager {

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARD = "Disregard";

    private static final int MAX = 100; // maximum messages the arrays can hold

    // Master parallel arrays (every message, whatever its flag)
    private final String[] messageIDs = new String[MAX];
    private final String[] messageHashes = new String[MAX];
    private final String[] recipients = new String[MAX];
    private final String[] messages = new String[MAX];
    private final String[] flags = new String[MAX];
    private int total = 0;

    // Required category arrays
    private final String[] sentMessages = new String[MAX];
    private final String[] disregardedMessages = new String[MAX];
    private final String[] storedMessages = new String[MAX];
    private int sentCount = 0;
    private int disregardedCount = 0;
    private int storedCount = 0;

    /** Simple holder object that Gson turns to/from JSON. */
    public static class StoredEntry {
        String messageID;
        String messageHash;
        String recipient;
        String message;
    }

    // ------------------------------------------------------------------
    // Populating the arrays (no hard-coding: callers pass the data in)
    // ------------------------------------------------------------------

    /** Adds one message to all relevant arrays. Returns false if full or flag invalid. */
    public boolean addMessage(String id, String hash, String recipient,
                              String message, String flag) {
        if (total >= MAX) {
            return false;
        }
        messageIDs[total] = id;
        messageHashes[total] = hash;
        recipients[total] = recipient;
        messages[total] = message;
        flags[total] = flag;

        if (SENT.equalsIgnoreCase(flag)) {
            flags[total] = SENT;
            sentMessages[sentCount++] = message;
        } else if (STORED.equalsIgnoreCase(flag)) {
            flags[total] = STORED;
            storedMessages[storedCount++] = message;
        } else if (DISREGARD.equalsIgnoreCase(flag)) {
            flags[total] = DISREGARD;
            disregardedMessages[disregardedCount++] = message;
        } else {
            return false; // unknown flag
        }
        total++;
        return true;
    }

    public String[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentCount);
    }

    public String[] getDisregardedMessages() {
        return Arrays.copyOf(disregardedMessages, disregardedCount);
    }

    public String[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedCount);
    }

    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, total);
    }

    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, total);
    }

    // ------------------------------------------------------------------
    // Menu features
    // ------------------------------------------------------------------

    /** 2a. Sender and recipient of every stored message. */
    public String displayStoredSenderAndRecipient(String sender) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            if (STORED.equals(flags[i])) {
                sb.append("Sender: ").append(sender)
                  .append(" | Recipient: ").append(recipients[i]).append("\n");
            }
        }
        return sb.length() == 0 ? "No stored messages." : sb.toString();
    }

    /** 2b. Longest stored message (or empty string when none). */
    public String getLongestStoredMessage() {
        String longest = "";
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
        return longest;
    }

    /** 2c. Search by message ID; returns "recipient, message" or a not-found text. */
    public String searchByMessageID(String id) {
        for (int i = 0; i < total; i++) {
            if (messageIDs[i].equals(id)) {
                return "Recipient: " + recipients[i] + ", Message: " + messages[i];
            }
        }
        return "Message ID not found.";
    }

    /** 2d. All sent or stored messages for one recipient (disregarded ones are ignored). */
    public String[] searchByRecipient(String recipient) {
        String[] found = new String[total];
        int count = 0;
        for (int i = 0; i < total; i++) {
            boolean wanted = SENT.equals(flags[i]) || STORED.equals(flags[i]);
            if (wanted && recipients[i].equals(recipient)) {
                found[count++] = messages[i];
            }
        }
        return Arrays.copyOf(found, count);
    }

    /** 2e. Delete by hash from the master arrays AND the category array. */
    public String deleteByHash(String hash) {
        for (int i = 0; i < total; i++) {
            if (messageHashes[i].equals(hash)) {
                String deletedText = messages[i];

                // remove from the matching category array
                if (SENT.equals(flags[i])) {
                    sentCount = removeValue(sentMessages, sentCount, deletedText);
                } else if (STORED.equals(flags[i])) {
                    storedCount = removeValue(storedMessages, storedCount, deletedText);
                } else {
                    disregardedCount = removeValue(disregardedMessages, disregardedCount, deletedText);
                }

                // remove from every master array so they stay aligned
                total = removeIndex(messageIDs, total, i, messageHashes, recipients, messages, flags);
                return "Message: \"" + deletedText + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /** 2f / report: full details of every message with the given flag. */
    public String displayReport(String flag) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            if (flag.equals(flags[i])) {
                sb.append("Message ID:   ").append(messageIDs[i]).append("\n")
                  .append("Message Hash: ").append(messageHashes[i]).append("\n")
                  .append("Recipient:    ").append(recipients[i]).append("\n")
                  .append("Message:      ").append(messages[i]).append("\n")
                  .append("------------------------------\n");
            }
        }
        return sb.length() == 0 ? "No " + flag.toLowerCase() + " messages." : sb.toString();
    }

    // ------------------------------------------------------------------
    // JSON storage
    // ------------------------------------------------------------------

    /** Writes every stored message to a JSON file. */
    public void saveStoredToJson(Path file) throws IOException {
        StoredEntry[] entries = new StoredEntry[storedCount];
        int n = 0;
        for (int i = 0; i < total; i++) {
            if (STORED.equals(flags[i])) {
                StoredEntry e = new StoredEntry();
                e.messageID = messageIDs[i];
                e.messageHash = messageHashes[i];
                e.recipient = recipients[i];
                e.message = messages[i];
                entries[n++] = e;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(entries, writer);
        }
    }

    /**
     * Reads the JSON file into the arrays as Stored messages.
     * Entries whose hash is already loaded are skipped, so calling this twice is safe.
     * Returns how many messages were loaded. A missing file loads 0.
     */
    public int loadStoredFromJson(Path file) throws IOException {
        if (!Files.exists(file)) {
            return 0;
        }
        Gson gson = new Gson();
        StoredEntry[] entries;
        try (Reader reader = Files.newBufferedReader(file)) {
            entries = gson.fromJson(reader, StoredEntry[].class);
        }
        if (entries == null) {
            return 0;
        }
        int loaded = 0;
        for (StoredEntry e : entries) {
            if (!hashExists(e.messageHash)
                    && addMessage(e.messageID, e.messageHash, e.recipient, e.message, STORED)) {
                loaded++;
            }
        }
        return loaded;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private boolean hashExists(String hash) {
        for (int i = 0; i < total; i++) {
            if (messageHashes[i].equals(hash)) {
                return true;
            }
        }
        return false;
    }

    /** Removes the first occurrence of value by shifting left. Returns the new count. */
    private int removeValue(String[] array, int count, String value) {
        for (int i = 0; i < count; i++) {
            if (array[i].equals(value)) {
                for (int j = i; j < count - 1; j++) {
                    array[j] = array[j + 1];
                }
                array[count - 1] = null;
                return count - 1;
            }
        }
        return count;
    }

    /** Removes index idx from the first array and every "other" array. Returns new count. */
    private int removeIndex(String[] first, int count, int idx, String[]... others) {
        for (int j = idx; j < count - 1; j++) {
            first[j] = first[j + 1];
            for (String[] other : others) {
                other[j] = other[j + 1];
            }
        }
        first[count - 1] = null;
        for (String[] other : others) {
            other[count - 1] = null;
        }
        return count - 1;
    }
}
