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
  * @author Merra
 * @version Iteration 6
 *
 * Small bar at the bottom of the main window.
 * Shows how many paths are being watched, how many events have happened,
 * and when the status was last updated.
 */
public class StatusBar extends JPanel {

    /**
     * Provider for active watcher paths.
     * This lets the StatusBar ask "how many directories are being watched?"
     * without knowing the full watcher implementation.
     */
    public interface WatcherInfoProvider {
        List<Path> getWatchPaths();
    }

    /** Label used to show the status text. */
    private final JLabel myLabel;

    /** Stats object for event counts. */
    private final EventStats myStats;

    /** Provider for watcher paths (may be null). */
    private final WatcherInfoProvider myWatcherInfoProvider;

    /** Formatter for the time shown in the status bar. */
    private final DateTimeFormatter myTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Creates a new StatusBar.
     *
     * @param theStats                stats object holding event counts (can be null)
     * @param theWatcherInfoProvider  provider for active watcher paths (can be null)
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
                theWatchingCount = PathPanel.model.size();
            }
        }

        long theTotalEvents = 0;
        if (myStats != null) {
            theTotalEvents = EventPanel.events.size();
        }

        final String theTime = LocalTime.now().format(myTimeFormatter);

        final String theText = "Watching " + theWatchingCount
                + " path(s) • " + theTotalEvents
                + " event(s) • Last update " + theTime;

        myLabel.setText(theText);
    }
}
