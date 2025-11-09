package model;

import java.util.ArrayList;

import java.util.List;

/**
 * DatabaseLogger is responsible for storing, retrieving, and querying
 * FileEventInfo records in memory. This class acts as the "database"
 * component of the Model layer.
 */
public class DatabaseLogger {

    // Declares the internal event log list that stores all file events.
    // 'final' means the reference cannot be reassigned after initialization.
    private final List<FileEventInfo> eventLog = new ArrayList<>();

    /**
     * Logs a new FileEventInfo object by adding it to the internal event list.
     */
    // Method to add a file event to the log.
    public void logFileEventInfo(FileEventInfo event) {
        // Adds the provided event to the eventLog list.
        eventLog.add(event);
    }

    /**
     * Returns a defensive (safe) copy of the event log.
     * This prevents external modification of the internal list.
     */
    // Method that returns a copy of the eventLog to preserve encapsulation.
    public List<FileEventInfo> getEventInfoLog() {
        // Returns a new ArrayList containing all the events from eventLog.
        return new ArrayList<>(eventLog);
    }

    /**
     * Searches for file events that match a specific activity type
     * (e.g., created, moved, modified, deleted).
     */
    // Method that filters events based on the given activity string.
    public List<FileEventInfo> queryByActivity(String activity) {

        // Creates a new empty list to store the matching events.
        List<FileEventInfo> result = new ArrayList<>();

        // Loops through each event stored in eventLog.
        for (FileEventInfo event : eventLog) {

            // Checks if the event's activity matches (case-insensitive comparison).
            if (event.getFileActivity().equalsIgnoreCase(activity)) {

                // If the activity matches, add the event to the result list.
                result.add(event);
            }
        }

        // Returns the list of events that matched the search criteria.
        return result;
    }
}


