package view;

import controller.CSVController;
import controller.GmailController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * This class creates the main UI for the File System Watcher.
 *
 * @author Eamon
 * @version Iteration 3.3
 */
public class WatcherMainUI extends JPanel {
    /**
     * Main frame for UI.
     */
    private final JFrame myFrame;
    /**
     * Event panel for any file change events.
     */
    private final EventPanel myEventPanel;
    /**
     * Path panel for currently watched paths.
     */
    private final PathPanel myPathPanel;
    /**
     * Status bar with information for file events and watched paths.
     */
    public static StatusBar myStatusBar;
    /**
     * Send button for gmail panel.
     */
    public static JButton mySendButton;
    /**
     * Export button for CSV file to local machine.
     */
    public static JButton myExportButton;
    /**
     * Constructor for WatcherMainUI.
     */
    public WatcherMainUI() throws IOException {
        super();
        myFrame = new JFrame("Team E File System Watcher");
        FileSystemWatcher myFileSystemWatcher = new FileSystemWatcher();
        myEventPanel = new EventPanel(myFileSystemWatcher);
        myPathPanel = new PathPanel(myFileSystemWatcher);
        mySendButton = new JButton("Send");
        myExportButton = new JButton("Export");
        mySendButton.setEnabled(false);
        myExportButton.setEnabled(false);
        myStatusBar = new StatusBar(new EventStats(), List::of);
        buildUI();
    }

    /**
     * Build UI for main frame.
     */
    public void buildUI(){
        myFrame.setLayout(new BorderLayout());
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setFocusable(true);
        myFrame.setResizable(true);
        myFrame.setSize(800, 500);
        myFrame.setLocationRelativeTo(null);
        createAndShowGUI();
        myFrame.setVisible(true);
        myFrame.pack();
    }
    /**
     * Add panels to main frame and show GUI.
     */
    public void createAndShowGUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mySendButton.addActionListener(e -> {
            GmailPanel gmailPanel = new GmailPanel(myFrame);
            GmailController controller = new GmailController(gmailPanel);
        });
        myExportButton.addActionListener(e -> {
            String fileName = EventPanel.events.get(EventPanel.myIndex).getFileName();
            String ext = EventPanel.events.get(EventPanel.myIndex).getFileExtension();
            String filePath = EventPanel.events.get(EventPanel.myIndex).getFilePath();
            String change = EventPanel.events.get(EventPanel.myIndex).getFileActivity();
            String timeStamp = EventPanel.events.get(EventPanel.myIndex).getTimeStamp();
            CSVExportPanel csvPanel = new CSVExportPanel();
            CSVController csvController = new CSVController(csvPanel, fileName, ext, filePath, change, timeStamp);
            JFrame frame = new JFrame("CSV Export");
            csvController.showInDialog(frame);
        });
        topBar.add(mySendButton);
        topBar.add(myExportButton);


        JPanel folderPanel = myPathPanel;
        JPanel middlePanel = new JPanel(new GridLayout(1, 3));
        JPanel eventListPanel = myEventPanel;
//        JPanel reportPanel = new ReportPanel(new ReportGenerator(new FileEventLog()));
        JPanel bottomPanel = myStatusBar;
        middlePanel.add(folderPanel);
        middlePanel.add(eventListPanel);
//        middlePanel.add(reportPanel);
        myFrame.add(topBar, BorderLayout.NORTH);
        myFrame.add(middlePanel, BorderLayout.CENTER);
        myFrame.add(bottomPanel, BorderLayout.SOUTH);
    }
}
