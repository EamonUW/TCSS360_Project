package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportGenerator
 * ---------------
 * Builds simple text reports based on the file event log.
 * 
 * For example, it can show the "top N" files with the most changes
 * within a given time window.
 */
public class ReportGenerator {

    /** Event log that we generate reports from. */
    private final FileEventLog myLog;

    /**
     * Basic constructor.
     *
     * @param theLog event log to read from
     */
    public ReportGenerator(final FileEventLog theLog) {
        if (theLog == null) {
            throw new IllegalArgumentException("theLog cannot be null");
        }
        myLog = theLog;
    }

    /**
     * Builds a plain text report.
     *
     * @param theStart start time (null means "from the beginning")
     * @param theEnd   end time (null means "until the end")
     * @param theTopN  how many top files to list
     * @return a multi-line string that can be shown in the UI
     */
    public String buildTextReport(final Instant theStart,
                                  final Instant theEnd,
                                  final int theTopN) {
        if (theTopN <= 0) {
            throw new IllegalArgumentException("theTopN must be > 0");
        }

        // First, collect relevant events in the time range.
        final List<FileEventInfo> theAllEvents = myLog.getAllEvents();
        final List<FileEventInfo> theFiltered = new ArrayList<>();
        for (final FileEventInfo theEvent : theAllEvents) {
            final Instant theTime = theEvent.getTimeStamp();
            if (theTime == null) {
                continue;
            }
            boolean isAfterStart = (theStart == null) || !theTime.isBefore(theStart);
            boolean isBeforeEnd = (theEnd == null) || !theTime.isAfter(theEnd);
            if (isAfterStart && isBeforeEnd) {
                theFiltered.add(theEvent);
            }
        }

        // Count how many times each file path changed.
        final Map<String, Integer> theCounts = new HashMap<>();
        for (final FileEventInfo theEvent : theFiltered) {
            final String thePath = theEvent.getFilePath() == null
                    ? "(unknown)"
                    : theEvent.getFilePath();
            final int thePrev = theCounts.getOrDefault(thePath, 0);
            theCounts.put(thePath, thePrev + 1);
        }

        // Convert the map to a list and sort by count (descending).
        final List<Map.Entry<String, Integer>> theEntries =
                new ArrayList<>(theCounts.entrySet());
        theEntries.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed());

        // Build the text output.
        final StringBuilder theBuilder = new StringBuilder();
        theBuilder.append("File System Activity Report").append(System.lineSeparator());
        theBuilder.append("================================").append(System.lineSeparator());
        theBuilder.append("Total events in range: ").append(theFiltered.size()).append(System.lineSeparator());
        theBuilder.append(System.lineSeparator());

        theBuilder.append("Top ").append(theTopN).append(" most active files:").append(System.lineSeparator());

        int theCount = 0;
        for (final Map.Entry<String, Integer> theEntry : theEntries) {
            theCount = theCount + 1;
            if (theCount > theTopN) {
                break;
            }
            theBuilder.append(theCount)
                      .append(". ")
                      .append(theEntry.getKey())
                      .append(" (")
                      .append(theEntry.getValue())
                      .append(" changes)")
                      .append(System.lineSeparator());
        }

        if (theEntries.isEmpty()) {
            theBuilder.append("No file changes found for the selected time range.")
                      .append(System.lineSeparator());
        }

        return theBuilder.toString();
    }
}

