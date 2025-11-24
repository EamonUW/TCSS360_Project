package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FileEventLog
 * ------------
 * Stores all file events that the watcher detects.
 * Each event is stored as a FileEventInfo object.
 * 
 * This class also updates EventStats, so statistics
 * always match the events that were logged.
 */
public class FileEventLog {

    /** List of all events we have seen so far. */
    private final List<FileEventInfo> myEvents;

    /** Optional stats object that we keep in sync. */
    private final EventStats myStats;

    /**
     * Constructor when you do not care about statistics.
     */
    public FileEventLog() {
        this(null);
    }

    /**
     * Constructor that also keeps EventStats updated.
     *
     * @param theStats event statistics (can be null if not needed)
     */
    public FileEventLog(final EventStats theStats) {
        myEvents = new ArrayList<>();
        myStats = theStats;
    }

    /**
     * Adds a new event to the log and updates stats if available.
     *
     * @param theFileName file name
     * @param theFilePath full path
     * @param theEventType event type (for example, CREATE, MODIFY)
     * @param theUser user who triggered the change (optional)
     */
    public synchronized void addEvent(final String theFileName,
                                      final String theFilePath,
                                      final String theEventType,
                                      final String theUser) {
        final FileEventInfo theInfo = new FileEventInfo(
                theFileName,
                theFilePath,
                theEventType,
                theUser,
                Instant.now()
        );
        myEvents.add(theInfo);

        // Keep stats updated.
        if (myStats != null) {
            myStats.increment(theEventType);
        }
    }

    /**
     * Returns an unmodifiable copy of all events.
     *
     * @return list of FileEventInfo
     */
    public synchronized List<FileEventInfo> getAllEvents() {
        return Collections.unmodifiableList(new ArrayList<>(myEvents));
    }

    /**
     * Convenience method used by other parts of the app that
     * want a plain list of FileEventInfo.
     *
     * @return modifiable copy of the events list
     */
    public synchronized List<FileEventInfo> toFileEventInfoList() {
        return new ArrayList<>(myEvents);
    }

    /**
     * Clears the event log and resets stats (if present).
     */
    public synchronized void clear() {
        myEvents.clear();
        if (myStats != null) {
            myStats.reset();
        }
    }
}
