package model;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * SearchFilter
 * -------------------------
 * Provides helper methods for filtering lists of FileEventInfo.
 *
 * Current filters:
 * - By file name text
 * - By event type
 * - By directory prefix
 */
public class SearchFilter {

    /**
     * Filters events where the file name contains the given text.
     * Search is case-insensitive.
     *
     * @param theEvents     events to filter
     * @param theSearchText text to look for in the file name
     * @return filtered list of events; empty list if none or input is null
     */
    public List<FileEventInfo> filterByName(final List<FileEventInfo> theEvents,
                                            final String theSearchText) {

        if (theEvents == null || theEvents.isEmpty()) {
            return List.of();
        }

        final String theFilter = (theSearchText == null)
                ? ""
                : theSearchText.trim().toLowerCase(Locale.ROOT);

        if (theFilter.isEmpty()) {
            return List.copyOf(theEvents);
        }

        return theEvents.stream()
                .filter(e -> {
                    final String theName = e.getFileName();
                    return theName != null
                           && theName.toLowerCase(Locale.ROOT).contains(theFilter);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filters events by event type (exact match, case-insensitive).
     *
     * @param theEvents   events to filter
     * @param theTypeText event type text (e.g., "CREATE", "MODIFY")
     * @return filtered list of events
     */
    public List<FileEventInfo> filterByType(final List<FileEventInfo> theEvents,
                                            final String theTypeText) {

        if (theEvents == null || theEvents.isEmpty()) {
            return List.of();
        }

        final String theFilter = (theTypeText == null)
                ? ""
                : theTypeText.trim().toLowerCase(Locale.ROOT);

        if (theFilter.isEmpty()) {
            return List.copyOf(theEvents);
        }

        return theEvents.stream()
                .filter(e -> {
                    final String theType = e.getEventType();
                    return theType != null
                           && theType.toLowerCase(Locale.ROOT).equals(theFilter);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filters events where the file path starts with the given directory.
     *
     * @param theEvents events to filter
     * @param theDir    directory path to match
     * @return filtered list of events
     */
    public List<FileEventInfo> filterByDirectory(final List<FileEventInfo> theEvents,
                                                 final Path theDir) {

        if (theEvents == null || theEvents.isEmpty() || theDir == null) {
            return List.of();
        }

        final String theDirString = theDir.toAbsolutePath().toString();

        return theEvents.stream()
                .filter(e -> {
                    final String thePath = e.getFilePath();
                    return thePath != null && thePath.startsWith(theDirString);
                })
                .collect(Collectors.toList());
    }
}
