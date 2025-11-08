package View;

import javax.swing.*;
import java.awt.*;
/**
 * This class creates the main UI for the File System Watcher.
 *
 * @author Eamon
 * @version Iteration 3, 0.1
 */
public class WatcherMainUI extends JPanel {
    /**
     * Constructor for WatcherMainUI.
     *
     */
    public WatcherMainUI() {
        super();
        buildUI();
    }
    /**
     * Build UI for main frame.
     *
     */
    public void buildUI(){
        final JFrame frame = new JFrame("Team E File System Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(800, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.add(createAndShowGUI());

    }
    /**
     * Add panels to main frame and show GUI.
     *
     * @return Jpanel main Jpanel to add to JFrame.
     */
    public JPanel createAndShowGUI() {
        JPanel screenPanel = new JPanel(new BorderLayout());
        JPanel folderPathPanel = new PathPanel();
        JPanel middlePanel = new JPanel(new FlowLayout());
        JPanel eventListPanel = new EventPanel();
        JPanel watcherListPanel = new WatcherPanel();
        JPanel bottomPanel = new JPanel(new FlowLayout());

        middlePanel.add(eventListPanel);
        middlePanel.add(watcherListPanel);

        screenPanel.add(folderPathPanel, BorderLayout.NORTH);
        screenPanel.add(middlePanel, BorderLayout.CENTER);
        screenPanel.add(bottomPanel, BorderLayout.SOUTH);

        return screenPanel;
    }
}
