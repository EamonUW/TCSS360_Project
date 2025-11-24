package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EventStats
 * ----------
 * Keeps track of how many file events have happened in total,
 * and how many events there are of each type (CREATE, MODIFY, DELETE, MOVE, etc.).
 * 
 * The idea is that whenever a new file event is logged,
 * this class is updated so the UI and reports can show live statistics.
 */
public class EventStats {

    /** Total number of all events. */
    private long myTotalEvents;

    /** Counts for each event type (for example, "CREATE" -> 10). */
    private final ConcurrentHashMap<String, Long> myCountsByType;

    /**
     * Basic constructor.
     * Starts with zero counts for all event types.
     */
    public EventStats() {
        myCountsByType = new ConcurrentHashMap<>();
        myTotalEvents = 0L;
    }

    /**
     * Increments the count for a given event type and the total count.
     *
     * @param theEventType the type of the event (for example, "CREATE")
     */
    public synchronized void increment(final String theEventType) {
        final String theKey;
        if (theEventType == null || theEventType.trim().isEmpty()) {
            theKey = "UNKNOWN";
        } else {
            theKey = theEventType.trim().toUpperCase();
        }

        myTotalEvents = myTotalEvents + 1L;
        final long theCurrent = myCountsByType.getOrDefault(theKey, 0L);
        myCountsByType.put(theKey, theCurrent + 1L);
    }

    /**
     * Returns the total number of events logged so far.
     *
     * @return total event count
     */
    public synchronized long getTotalEvents() {
        return myTotalEvents;
    }

    /**
     * Returns a snapshot of counts for each event type.
     * 
     * The returned map is unmodifiable so callers cannot change our internal state.
     *
     * @return unmodifiable map of event type to count
     */
    public synchronized Map<String, Long> getCountsByType() {
        // Use LinkedHashMap to keep a stable order (if needed by the UI).
        final Map<String, Long> theCopy = new LinkedHashMap<>(myCountsByType);
        return Collections.unmodifiableMap(theCopy);
    }

    /**
     * Resets all counters back to zero.
     * Called when the log is cleared.
     */
    public synchronized void reset() {
        myTotalEvents = 0L;
        myCountsByType.clear();
    }
}
