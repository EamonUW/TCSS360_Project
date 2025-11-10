package teame.fs.gui;

import teame.fs.EventStats;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.nio.file.Path;

/**
 * StatusBar
 * ----------
 * Small bar at the bottom of the main UI.
 * Shows how many paths are being watched, total number of events,
 * and when the last update occurred.
 *
 * Author: Merra Migora
 */
public class StatusBar extends JPanel {

    /** Simple interface so the status bar can ask for watcher info */
    public interface WatcherInfoProvider {
        List<Path> getWatchPaths();
    }

    private final JLabel label = new JLabel("Ready");
    private final EventStats stats;
    private final WatcherInfoProvider watcherInfo;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    public StatusBar(EventStats stats, WatcherInfoProvider watcherInfo) {
        this.stats = stats;
        this.watcherInfo = watcherInfo;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(label, BorderLayout.WEST);
        refresh();
    }

    /** Refreshes the status text — call this after any update */
    public void refresh() {
        int watching = watcherInfo == null ? 0 : watcherInfo.getWatchPaths().size();
        long total = stats == null ? 0 : stats.total();
        String time = LocalTime.now().format(timeFmt);

        label.setText(String.format("Watching %d path(s) • %d event(s) • Last update %s",
                watching, total, time));
    }
}
