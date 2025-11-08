package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SearchFilter
 * ---------------------
 * Provides search and filtering capabilities for FileEventLog.
 * Allows users to filter events by file name, event type, or date range.
 *
 * Author: Merra Migora
 * Iteration: 2
 */
public class SearchFilter {

    private final FileEventLog log;

    public SearchFilter(FileEventLog log) {
        this.log = log;
    }

    /**
     * Finds all events with a specific file name (case-insensitive).
     */
    public List<FileEventLog.EventRecord> searchByFileName(String name) {
        List<FileEventLog.EventRecord> results = new ArrayList<>();
        for (FileEventLog.EventRecord e : log.getAllEvents()) {
            if (e.getFileName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Filters events by type (CREATE, MODIFY, DELETE, MOVE, etc.).
     */
    public List<FileEventLog.EventRecord> filterByType(String type) {
        List<FileEventLog.EventRecord> results = new ArrayList<>();
        for (FileEventLog.EventRecord e : log.getAllEvents()) {
            if (e.getEventType().equalsIgnoreCase(type)) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Filters events that occurred within the given time range.
     */
    public List<FileEventLog.EventRecord> filterByDateRange(Instant start, Instant end) {
        List<FileEventLog.EventRecord> results = new ArrayList<>();
        for (FileEventLog.EventRecord e : log.getAllEvents()) {
            if (!e.getTimeStamp().isBefore(start) && !e.getTimeStamp().isAfter(end)) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Quick search example for testing.
     */
    public void printResults(List<FileEventLog.EventRecord> results) {
        if (results.isEmpty()) {
            System.out.println("No matching events found.");
            return;
        }
        for (FileEventLog.EventRecord e : results) {
            System.out.println(e);
        }
    }
}
