package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchFilter
 * ------------
 * Provides simple search and filter operations on top of FileEventLog.
 * 
 * This class does not change the log. It only looks at it and returns
 * filtered lists of FileEventInfo objects that match some criteria.
 */
public class SearchFilter {

    /** Log that we search over. */
    private final FileEventLog myLog;

    /**
     * Basic constructor.
     *
     * @param theLog the log we want to filter (must not be null)
     */
    public SearchFilter(final FileEventLog theLog) {
        if (theLog == null) {
            throw new IllegalArgumentException("theLog cannot be null");
        }
        myLog = theLog;
    }

    /**
     * Returns all events whose file name contains the given text (case-insensitive).
     *
     * @param theText text to search for (null or empty means return all)
     * @return list of matching events
     */
    public List<FileEventInfo> filterByName(final String theText) {
        final String theNeedle = theText == null ? "" : theText.trim().toLowerCase();
        final List<FileEventInfo> theResult = new ArrayList<>();

        for (final FileEventInfo theEvent : myLog.getAllEvents()) {
            if (theNeedle.isEmpty()) {
                theResult.add(theEvent);
            } else {
                final String theName = theEvent.getFileName() == null
                        ? ""
                        : theEvent.getFileName().toLowerCase();
                if (theName.contains(theNeedle)) {
                    theResult.add(theEvent);
                }
            }
        }
        return theResult;
    }

    /**
     * Returns all events of a given type (for example, only CREATE events).
     *
     * @param theEventType event type to match (null or empty returns all)
     * @return list of matching events
     */
    public List<FileEventInfo> filterByType(final String theEventType) {
        final String theType = theEventType == null ? "" : theEventType.trim().toUpperCase();
        final List<FileEventInfo> theResult = new ArrayList<>();

        for (final FileEventInfo theEvent : myLog.getAllEvents()) {
            if (theType.isEmpty()) {
                theResult.add(theEvent);
            } else {
                final String theEventTypeUpper = theEvent.getEventType() == null
                        ? ""
                        : theEvent.getEventType().toUpperCase();
                if (theEventTypeUpper.equals(theType)) {
                    theResult.add(theEvent);
                }
            }
        }
        return theResult;
    }

    /**
     * Returns all events whose time is between the given start and end (inclusive).
     *
     * @param theStart start time (null means "no lower bound")
     * @param theEnd   end time (null means "no upper bound")
     * @return list of matching events
     */
    public List<FileEventInfo> filterByTimeRange(final Instant theStart,
                                                 final Instant theEnd) {
        final List<FileEventInfo> theResult = new ArrayList<>();

        for (final FileEventInfo theEvent : myLog.getAllEvents()) {
            final Instant theTime = theEvent.getTimeStamp();
            if (theTime == null) {
                continue;
            }
            boolean isAfterStart = (theStart == null) || !theTime.isBefore(theStart);
            boolean isBeforeEnd = (theEnd == null) || !theTime.isAfter(theEnd);

            if (isAfterStart && isBeforeEnd) {
                theResult.add(theEvent);
            }
        }
        return theResult;
    }
}
