package model;

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
 *  - a "debounce" delay in milliseconds
 *
 * Settings are stored in a small text file like:
 *
 *  debounce=250
 *  /path/one
 *  /path/two
 */
public class ConfigLoader {

    /** Default debounce value if none is configured. */
    private static final long DEFAULT_DEBOUNCE_MILLIS = 250L;

    /** Where the config file lives on disk. */
    private final Path myConfigFile;

    /** Paths that should be watched. */
    private final List<String> myWatchPaths = new ArrayList<>();

    /** Debounce time in milliseconds. */
    private long myDebounceMillis = DEFAULT_DEBOUNCE_MILLIS;

    /**
     * Creates a new ConfigLoader that will read/write the given file.
     *
     * @param theConfigFile path to the config file
     * @throws IllegalArgumentException if theConfigFile is null
     */
    public ConfigLoader(final Path theConfigFile) {
        if (theConfigFile == null) {
            throw new IllegalArgumentException("Config file path cannot be null.");
        }
        myConfigFile = theConfigFile;
    }

    /**
     * Loads settings from the config file, if it exists.
     * If it does not exist, default values are kept.
     *
     * @throws IOException if something goes wrong reading the file
     */
    public void load() throws IOException {
        myWatchPaths.clear();
        myDebounceMillis = DEFAULT_DEBOUNCE_MILLIS;

        if (!Files.exists(myConfigFile)) {
            return;
        }

        try (Reader theReader = Files.newBufferedReader(myConfigFile)) {
            final List<String> theLines = new ArrayList<>();
            final StringBuilder theBuilder = new StringBuilder();

            int theChar;
            while ((theChar = theReader.read()) != -1) {
                if (theChar == '\n' || theChar == '\r') {
                    if (theBuilder.length() > 0) {
                        theLines.add(theBuilder.toString());
                        theBuilder.setLength(0);
                    }
                } else {
                    theBuilder.append((char) theChar);
                }
            }
            if (theBuilder.length() > 0) {
                theLines.add(theBuilder.toString());
            }

            for (final String theLine : theLines) {
                final String theTrimmed = theLine.trim();
                if (theTrimmed.isEmpty()) {
                    continue;
                }
                if (theTrimmed.startsWith("debounce=")) {
                    final String theValue = theTrimmed.substring("debounce=".length());
                    try {
                        myDebounceMillis = Long.parseLong(theValue);
                    } catch (final NumberFormatException theEx) {
                        myDebounceMillis = DEFAULT_DEBOUNCE_MILLIS;
                    }
                } else {
                    myWatchPaths.add(theTrimmed);
                }
            }
        }
    }

    /**
     * @return unmodifiable list of configured watch paths
     */
    public List<String> getWatchPaths() {
        return Collections.unmodifiableList(myWatchPaths);
    }

    /**
     * @return debounce time in milliseconds
     */
    public long getDebounceMillis() {
        return myDebounceMillis;
    }

    /**
     * Replaces the current list of watch paths.
     *
     * @param thePaths new list of paths (null allowed = treated as empty)
     */
    public void setWatchPaths(final List<String> thePaths) {
        myWatchPaths.clear();
        if (thePaths != null) {
            myWatchPaths.addAll(thePaths);
        }
    }

    /**
     * Sets a new debounce value.
     *
     * @param theMillis debounce value in milliseconds
     */
    public void setDebounceMillis(final long theMillis) {
        myDebounceMillis = theMillis;
    }

    /**
     * Saves current settings back to the config file.
     *
     * @throws IOException if something goes wrong writing the file
     */
    public void save() throws IOException {
        final Path theFile = myConfigFile;

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
