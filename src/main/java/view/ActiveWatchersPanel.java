package teame.fs.gui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * ActiveWatchersPanel
 * ---------------------
 * Displays a list of active watcher paths with Start/Stop controls.
 * Uses a tiny service interface so it doesn't depend on your exact backend class names.
 *
 * Author: Merra Migora
 * Iteration: 3
 */
public class ActiveWatchersPanel extends JPanel {

    /** Adapter to your backend watcher service. Implement this using FileSystemWatcher. */
    public interface WatcherService {
        List<Path> getWatchPaths();
        void startWatcher(Path path) throws Exception;
        void stopWatcher(Path path);
    }

    private final WatcherService service;
    private final DefaultListModel<Path> model = new DefaultListModel<>();
    private final JList<Path> list = new JList<>(model);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn = new JButton("Stop");
    private final JButton refreshBtn = new JButton("Refresh");

    public ActiveWatchersPanel(WatcherService service) {
        this.service = Objects.requireNonNull(service, "service");
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Active Watchers"));

        list.setVisibleRowCount(8);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(startBtn);
        btns.add(stopBtn);
        btns.add(refreshBtn);
        add(btns, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> {
            Path sel = list.getSelectedValue();
            if (sel == null) return;
            try {
                service.startWatcher(sel);
                JOptionPane.showMessageDialog(this, "Started: " + sel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to start: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        stopBtn.addActionListener(e -> {
            Path sel = list.getSelectedValue();
            if (sel == null) return;
            service.stopWatcher(sel);
            JOptionPane.showMessageDialog(this, "Stopped: " + sel);
        });

        refreshBtn.addActionListener(e -> refresh());
    }

    /** Reloads the list from the service. */
    public final void refresh() {
        model.clear();
        for (Path p : service.getWatchPaths()) {
            model.addElement(p);
        }
    }
}
