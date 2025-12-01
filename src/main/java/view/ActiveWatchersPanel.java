package view;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

/**
 * ActiveWatchersPanel
 * -------------------
  * @author Merra
 * @version Iteration 6
 *
 * GUI panel that shows all currently active watcher paths.
 * It also lets the user start and stop watching paths,
 * by calling methods on a small service interface.
 */
public class ActiveWatchersPanel extends JPanel {

    /**
     * Service interface that hides the real watcher implementation.
     * The view only knows how to ask for paths and start/stop them.
     */
    public interface WatcherService {
        List<Path> getWatchPaths();
        void startWatcher(Path thePath) throws Exception;
        void stopWatcher(Path thePath);
    }

    /** The service we use to talk to the model. */
    private final WatcherService myService;

    /** List model and list UI for displaying paths. */
    private final DefaultListModel<Path> myListModel;
    private final JList<Path> myList;

    /** Buttons for user actions. */
    private final JButton myRefreshButton;
    private final JButton myStartButton;
    private final JButton myStopButton;

    /**
     * Creates the panel with the given service.
     *
     * @param theService service used to manage watchers
     * @throws IllegalArgumentException if theService is null
     */
    public ActiveWatchersPanel(final WatcherService theService) {
        if (theService == null) {
            throw new IllegalArgumentException("theService cannot be null");
        }
        myService = theService;

        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createTitledBorder("Active Watchers"));

        // List of watcher paths
        myListModel = new DefaultListModel<>();
        myList = new JList<>(myListModel);
        myList.setVisibleRowCount(8);
        add(new JScrollPane(myList), BorderLayout.CENTER);

        // Buttons at the bottom
        final JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));

        myRefreshButton = new JButton("Refresh");
        myStartButton = new JButton("Start");
        myStopButton = new JButton("Stop");

        theButtonPanel.add(myRefreshButton);
        theButtonPanel.add(myStartButton);
        theButtonPanel.add(myStopButton);

        add(theButtonPanel, BorderLayout.SOUTH);

        // Wire up button actions
        myRefreshButton.addActionListener(e -> refreshList());

        myStartButton.addActionListener(e -> {
            final String theInput = JOptionPane.showInputDialog(
                    ActiveWatchersPanel.this,
                    "Enter a directory path to start watching:",
                    "Add Watcher",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (theInput != null && !theInput.trim().isEmpty()) {
                try {
                    myService.startWatcher(Path.of(theInput.trim()));
                    refreshList();
                } catch (final Exception theEx) {
                    JOptionPane.showMessageDialog(
                            ActiveWatchersPanel.this,
                            "Could not start watcher: " + theEx.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        myStopButton.addActionListener(e -> {
            final Path theSelected = myList.getSelectedValue();
            if (theSelected == null) {
                JOptionPane.showMessageDialog(
                        ActiveWatchersPanel.this,
                        "Please select a path to stop watching.",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            myService.stopWatcher(theSelected);
            refreshList();
        });

        // Load initial data
        refreshList();
    }

    /**
     * Refreshes the list of active watchers by asking the service.
     */
    public final void refreshList() {
        myListModel.clear();
        final List<Path> thePaths = myService.getWatchPaths();
        for (final Path thePath : thePaths) {
            myListModel.addElement(thePath);
        }
    }
}
