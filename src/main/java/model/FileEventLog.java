package teame.fs;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FileEventLog
 * -------------
 * Stores file events and (optionally) updates EventStats.
 Merra Migora
 */
public class FileEventLog {

    public static class EventRecord {
        private final String fileName;
        private final String filePath;
        private final String eventType;
        private final String user;
        private final Instant timeStamp;

        public EventRecord(String fileName, String filePath,
                           String eventType, String user,
                           Instant timeStamp) {
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

    private final List<EventRecord> events = new ArrayList<>();
    private final EventStats stats; // may be null

    /** Use this if you do not care about EventStats. */
    public FileEventLog() {
        this(null);
    }

    /** Use this constructor if you want stats to be updated automatically. */
    public FileEventLog(EventStats stats) {
        this.stats = stats;
    }

    public synchronized void addEvent(String fileName,
                                      String filePath,
                                      String eventType,
                                      String user) {
        EventRecord record =
                new EventRecord(fileName, filePath, eventType, user, Instant.now());
        events.add(record);

        // update stats if present
        if (stats != null) {
            stats.increment(eventType);
        }
        System.out.println("Logged event: " + record);
    }

    public synchronized List<EventRecord> getAllEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public synchronized void clear() {
        events.clear();
        if (stats != null) {
            stats.reset();
        }
        System.out.println("Event log cleared.");
    }

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

