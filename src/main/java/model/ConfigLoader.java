package teame.fs;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ConfigLoader
 * ------------
 * Loads and saves simple user settings for the application.
 * 
 * Right now, it stores:
 *  - list of watch paths (directories the user wants to monitor)
 *  - debounce time in milliseconds (optional)
 * 
 * It uses a very simple text format (one path per line and an optional setting line).
 * This could be replaced later with a real JSON library.
 */
public class ConfigLoader {

    /** List of directories to watch. */
    private final List<String> myWatchPaths;

    /** Debounce time in milliseconds. This helps reduce noisy events. */
    private int myDebounceMillis;

    /** Default debounce in case nothing is stored. */
    private static final int DEFAULT_DEBOUNCE_MS = 250;

    /**
     * Basic constructor with empty state.
     */
    public ConfigLoader() {
        myWatchPaths = new ArrayList<>();
        myDebounceMillis = DEFAULT_DEBOUNCE_MS;
    }

    /**
     * Returns an unmodifiable copy of the current watch paths.
     *
     * @return list of paths as strings
     */
    public List<String> getWatchPaths() {
        return Collections.unmodifiableList(new ArrayList<>(myWatchPaths));
    }

    /**
     * Replaces the current list of watch paths.
     *
     * @param thePaths new collection of paths (null is treated as empty)
     */
    public void setWatchPaths(final List<String> thePaths) {
        myWatchPaths.clear();
        if (thePaths != null) {
            myWatchPaths.addAll(thePaths);
        }
    }

    /**
     * Adds a single watch path to the list.
     *
     * @param thePath path as a string
     */
    public void addWatchPath(final String thePath) {
        if (thePath == null || thePath.trim().isEmpty()) {
            throw new IllegalArgumentException("thePath cannot be null or empty");
        }
        myWatchPaths.add(thePath.trim());
    }

    /**
     * Returns the current debounce time.
     *
     * @return debounce in milliseconds
     */
    public int getDebounceMillis() {
        return myDebounceMillis;
    }

    /**
     * Sets the debounce time. Negative values are not allowed.
     *
     * @param theMillis new debounce in milliseconds
     */
    public void setDebounceMillis(final int theMillis) {
        if (theMillis < 0) {
            throw new IllegalArgumentException("theMillis cannot be negative");
        }
        myDebounceMillis = theMillis;
    }

    /**
     * Loads settings from a simple text file.
     *
     * First non-empty line that starts with "debounce=" sets the debounce.
     * Other non-empty lines are treated as watch paths.
     *
     * @param theFile file to read from
     * @throws IOException if file reading fails
     */
    public void load(final Path theFile) throws IOException {
        if (theFile == null) {
            throw new IllegalArgumentException("theFile cannot be null");
        }
        if (!Files.exists(theFile)) {
            // If no file yet, just keep defaults.
            return;
        }

        myWatchPaths.clear();
        myDebounceMillis = DEFAULT_DEBOUNCE_MS;

        try (Reader theReader = Files.newBufferedReader(theFile)) {
            final StringBuilder theBuilder = new StringBuilder();
            final char[] theBuffer = new char[1024];
            int theRead;
            while ((theRead = theReader.read(theBuffer)) != -1) {
                theBuilder.append(theBuffer, 0, theRead);
            }
            final String[] theLines = theBuilder.toString().split("\\R");
            for (final String theLineRaw : theLines) {
                final String theLine = theLineRaw.trim();
                if (theLine.isEmpty()) {
                    continue;
                }
                if (theLine.startsWith("debounce=")) {
                    final String theNum = theLine.substring("debounce=".length()).trim();
                    try {
                        myDebounceMillis = Integer.parseInt(theNum);
                    } catch (final NumberFormatException theEx) {
                        myDebounceMillis = DEFAULT_DEBOUNCE_MS;
                    }
                } else {
                    myWatchPaths.add(theLine);
                }
            }
        }
    }

    /**
     * Saves the current settings to a simple text file.
     *
     * Format:
     *   debounce=NUMBER
     *   /path/one
     *   /path/two
     *
     * @param theFile file to write to
     * @throws IOException if file writing fails
     */
    public void save(final Path theFile) throws IOException {
        if (theFile == null) {
            throw new IllegalArgumentException("theFile cannot be null");
        }

        if (theFile.getParent() != null) {
            Files.createDirectories(theFile.getParent());
        }

        try (Writer theWriter = Files.newBufferedWriter(theFile)) {
            theWriter.write("debounce=" + myDebounceMillis + System.lineSeparator());
            for (final String thePath : myWatchPaths) {
                theWriter.write(thePath);
                theWriter.write(System.lineSeparator());
            }
        }
    }
}
