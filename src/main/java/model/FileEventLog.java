package java.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FileEventLog
 * ---------------------
 * Stores and manages file activity records for the File System Watcher.
 * Each event contains details about the file name, path, type of change, user, and timestamp.
 *
 * Author: Merra Migora
 * Iteration: 2
 */
public class FileEventLog {

    /**
     * Represents a single file event record.
     */
    public static class EventRecord {
        private final String fileName;
        private final String filePath;
        private final String eventType;
        private final String user;
        private final Instant timeStamp;

        public EventRecord(String fileName, String filePath, String eventType, String user, Instant timeStamp) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.eventType = eventType;
            this.user = user;
            this.timeStamp = timeStamp;
        }

        public String getFileName() { return fileName; }
        public String getFilePath() { return filePath; }
        public String getEventType() { return eventType; }
        public String getUser() { return user; }
        public Instant getTimeStamp() { return timeStamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s (%s by %s)",
                    DateTimeFormatter.ISO_INSTANT.format(timeStamp),
                    eventType,
                    filePath,
                    fileName,
                    user);
        }
    }

    // List to store all events
    private final List<EventRecord> events = new ArrayList<>();

    /**
     * Adds a new event record to the log.
     */
    public synchronized void addEvent(String fileName, String filePath, String eventType, String user) {
        EventRecord record = new EventRecord(fileName, filePath, eventType, user, Instant.now());
        events.add(record);
        System.out.println("Logged event: " + record);
    }

    /**
     * Returns an unmodifiable list of all stored events.
     */
    public synchronized List<EventRecord> getAllEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    /**
     * Clears all logged events.
     */
    public synchronized void clear() {
        events.clear();
        System.out.println("Event log cleared.");
    }

    /**
     * Prints all logged events (for quick testing).
     */
    public synchronized void printAll() {
        if (events.isEmpty()) {
            System.out.println("No file events logged yet.");
            return;
        }
        for (EventRecord record : events) {
            System.out.println(record);
        }
    }
}
