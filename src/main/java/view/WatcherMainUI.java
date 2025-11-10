package view;

import com.google.api.services.gmail.Gmail;
import teame.fs.EventStats;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

/**
 * This class creates the main UI for the File System Watcher.
 *
 * @author Eamon
 * @version Iteration 3, 0.1
 */
public class WatcherMainUI extends JPanel {
    /**
     * Constructor for WatcherMainUI.
     */
    public WatcherMainUI() {
        super();
        buildUI();
    }
    /**
     * Build UI for main frame.
     */
    public void buildUI(){
        final JFrame frame = new JFrame("Team E File System Watcher");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.setResizable(false);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        createAndShowGUI(frame);
        frame.setVisible(true);
        frame.pack();
    }
    /**
     * Add panels to main frame and show GUI.
     *
     * @return Jpanel main Jpanel to add to JFrame.
     */
    public JFrame createAndShowGUI(JFrame frame) {
       JPanel folderPathPanel = new PathPanel();
       JPanel middlePanel = new JPanel(new GridLayout(1, 2));
       JPanel eventListPanel = new EventPanel();
       JPanel bottomPanel = new StatusBar(new EventStats(), new StatusBar.WatcherInfoProvider() {
           @Override
           public List<Path> getWatchPaths() {
               return List.of();
           }
       });

       middlePanel.add(folderPathPanel);
       middlePanel.add(eventListPanel);

       frame.add(middlePanel, BorderLayout.CENTER);
       frame.add(bottomPanel, BorderLayout.SOUTH);

        return frame;
    }
}
