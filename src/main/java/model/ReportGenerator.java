package model;

import java.util.List;

/**
 * ReportGenerator
 * -------------------------
 * Builds simple text reports based on the file events stored
 * in a FileEventLog and the statistics tracked by EventStats.
 *
 * Used by:
 * - ReportPanel (to show a summary or detailed report)
 * - GmailController (if you want to send reports by email)
 */
public class ReportGenerator {

    /** Source of file events. */
    private final FileEventLog myEventLog;

    /**
     * Creates a report generator that uses the given log.
     *
     * @param theEventLog event log to use as a data source
     * @throws IllegalArgumentException if theEventLog is null
     */
    public ReportGenerator(final FileEventLog theEventLog) {
        if (theEventLog == null) {
            throw new IllegalArgumentException("Event log cannot be null.");
        }
        myEventLog = theEventLog;
    }

    /**
     * Generates a short summary using EventStats.
     *
     * @return summary report text
     */
    public String generateSummaryReport() {
        final EventStats theStats = myEventLog.getStats();

        final StringBuilder theBuilder = new StringBuilder();
        theBuilder.append("File Event Summary\n");
        theBuilder.append("==================\n");

        theBuilder.append("Total events: ")
                  .append(theStats.getTotalEvents())
                  .append("\n");

        theBuilder.append("Created: ")
                  .append(theStats.getCreateCount())
                  .append("\n");

        theBuilder.append("Modified: ")
                  .append(theStats.getModifyCount())
                  .append("\n");

        theBuilder.append("Deleted: ")
                  .append(theStats.getDeleteCount())
                  .append("\n");

        theBuilder.append("Moved: ")
                  .append(theStats.getMoveCount())
                  .append("\n");

        return theBuilder.toString();
    }

    /**
     * Generates a detailed report that lists every event on its
     * own line.
     *
     * @return detailed report text
     */
    public String generateDetailedReport() {
        final List<FileEventInfo> theEvents = myEventLog.toFileEventInfoList();

        final StringBuilder theBuilder = new StringBuilder();
        theBuilder.append("Detailed File Event Report\n");
        theBuilder.append("==========================\n");

        for (final FileEventInfo theEvent : theEvents) {
            theBuilder.append(formatEventLine(theEvent)).append("\n");
        }

        return theBuilder.toString();
    }

    /**
     * Formats a single event as one line of text.
     *
     * @param theEvent event to format
     * @return formatted line
     */
    private String formatEventLine(final FileEventInfo theEvent) {
        final String theType = theEvent.getEventType();
        final String theName = theEvent.getFileName();
        final String thePath = theEvent.getFilePath();

        return String.format("%s | %s | %s", theType, theName, thePath);
    }
}
