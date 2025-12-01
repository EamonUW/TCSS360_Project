package model;

/**
 * EventStats
  * @author Merra
 * @version Iteration 6
 *
 * -------------------------
 * Tracks basic statistics about the file events recorded
 * in FileEventLog. This class is used by ReportGenerator
 * and other parts of the project to show summary counts.
 *
 * Supported event types:
 * - CREATE
 * - MODIFY
 * - DELETE
 * - MOVE
 */
public class EventStats {

    /** Total number of events recorded. */
    private int myTotalEvents;

    /** Count of CREATE events. */
    private int myCreateCount;

    /** Count of MODIFY events. */
    private int myModifyCount;

    /** Count of DELETE events. */
    private int myDeleteCount;

    /** Count of MOVE events. */
    private int myMoveCount;

    /**
     * Records a new event and updates all counters.
     *
     * @param theEvent event to record
     * @throws IllegalArgumentException if theEvent is null
     */
    public void recordEvent(final FileEventInfo theEvent) {
        if (theEvent == null) {
            throw new IllegalArgumentException("Event cannot be null.");
        }

        myTotalEvents++;

        final String type = theEvent.getEventType();
        if (type == null) {
            return;
        }

        switch (type.toUpperCase()) {
            case "CREATE" -> myCreateCount++;
            case "MODIFY" -> myModifyCount++;
            case "DELETE" -> myDeleteCount++;
            case "MOVE"   -> myMoveCount++;
            default -> {
                // ignore unknown event type
            }
        }
    }

    /** @return total number of events recorded */
    public int getTotalEvents() {
        return myTotalEvents;
    }

    /** @return number of CREATE events */
    public int getCreateCount() {
        return myCreateCount;
    }

    /** @return number of MODIFY events */
    public int getModifyCount() {
        return myModifyCount;
    }

    /** @return number of DELETE events */
    public int getDeleteCount() {
        return myDeleteCount;
    }

    /** @return number of MOVE events */
    public int getMoveCount() {
        return myMoveCount;
    }

    /**
     * Resets all counters back to zero.
     * Used when the log is cleared.
     */
    public void reset() {
        myTotalEvents = 0;
        myCreateCount = 0;
        myModifyCount = 0;
        myDeleteCount = 0;
        myMoveCount = 0;
    }
}

