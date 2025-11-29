package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FileEventLog
 * -------------------------
 * Stores all file events that the watcher has seen.
 * Other parts of the app (reporting, CSV export, search, GUI)
 * can read events from this log.
 *
 * Responsibilities:
 * - Add new events
 * - Return a copy of all events
 * - Track basic statistics using EventStats
 */
public class FileEventLog {

    /** All recorded file events in the order they happened. */
    private final List<FileEventInfo> myEvents;

    /** Helper that tracks statistics about the events. */
    private final EventStats myStats;

    /**
     * Creates an empty event log with a fresh EventStats.
     */
    public FileEventLog() {
        myEvents = new ArrayList<>();
        myStats = new EventStats();
    }

    /**
     * Adds a new file event to the log and updates statistics.
     *
     * @param theEvent the file event to add
     * @throws IllegalArgumentException if theEvent is null
     */
    public synchronized void addEvent(final FileEventInfo theEvent) {
        if (theEvent == null) {
            throw new IllegalArgumentException("Event cannot be null.");
        }

        myEvents.add(theEvent);
        myStats.recordEvent(theEvent);
    }

    /**
     * Returns a copy of all events stored in the log.
     * Callers can modify the returned list without
     * affecting the internal data.
     *
     * @return copy of the events list
     */
    public synchronized List<FileEventInfo> toFileEventInfoList() {
        return new ArrayList<>(myEvents);
    }

    /**
     * Returns an unmodifiable view of all events.
     * Use this only when you want read-only access.
     *
     * @return unmodifiable list of events
     */
    public synchronized List<FileEventInfo> getReadOnlyEvents() {
        return Collections.unmodifiableList(myEvents);
    }

    /**
     * Returns the statistics object for this log.
     *
     * @return EventStats instance used by this log
     */
    public synchronized EventStats getStats() {
        return myStats;
    }

    /**
     * Clears all events and resets statistics.
     */
    public synchronized void clear() {
        myEvents.clear();
        myStats.reset();
    }

    /**
     * @return number of events in the log
     */
    public synchronized int size() {
        return myEvents.size();
    }

    /**
     * @return true if there are no events
     */
    public synchronized boolean isEmpty() {
        return myEvents.isEmpty();
    }
}
