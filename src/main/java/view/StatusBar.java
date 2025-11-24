package view;

import model.EventStats;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.nio.file.Path;

/**
 * StatusBar
 * ---------
 * Small bar at the bottom of the main window.
 * Shows how many paths are being watched, how many events have happened,
 * and the time of the last update.
 */
public class StatusBar extends JPanel {

    /**
     * Simple provider so the status bar can ask
     * how many paths are being watched.
     */
    public interface WatcherInfoProvider {
        List<Path> getWatchPaths();
    }

    /** Label used to show the status text. */
    private final JLabel myLabel;

    /** Stats object for event counts. */
    private final EventStats myStats;

    /** Provider that knows about active watcher paths. */
    private final WatcherInfoProvider myWatcherInfoProvider;

    /** Formatter for showing the current time. */
    private final DateTimeFormatter myTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Basic constructor.
     *
     * @param theStats stats for event counts (can be null)
     * @param theWatcherInfoProvider provider for active watcher paths (can be null)
     */
    public StatusBar(final EventStats theStats,
                     final WatcherInfoProvider theWatcherInfoProvider) {
        myStats = theStats;
        myWatcherInfoProvider = theWatcherInfoProvider;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        myLabel = new JLabel("Ready");
        myLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(myLabel, BorderLayout.WEST);

        // Show initial status.
        refresh();
    }

    /**
     * Updates the label using the current stats and watcher info.
     * Can be called whenever something changes in the app.
     */
    public void refresh() {
        int theWatchingCount = 0;
        if (myWatcherInfoProvider != null) {
            final List<Path> thePaths = myWatcherInfoProvider.getWatchPaths();
            if (thePaths != null) {
                theWatchingCount = thePaths.size();
            }
        }

        long theTotalEvents = 0;
        if (myStats != null) {
            theTotalEvents = myStats.getTotalEvents();
        }

        final String theTime = LocalTime.now().format(myTimeFormatter);

        final String theText = "Watching " + theWatchingCount
                + " path(s) • " + theTotalEvents
                + " event(s) • Last update " + theTime;

        myLabel.setText(theText);
    }
}
