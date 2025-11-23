package view;

import controller.CSVController;
import controller.GmailController;
import org.checkerframework.checker.units.qual.C;
import teame.fs.EventStats;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class creates the main UI for the File System Watcher.
 *
 * @author Eamon
 * @version Iteration 3.3
 */
public class WatcherMainUI extends JPanel {

    JFrame myFrame;
    /**
     * Constructor for WatcherMainUI.
     */
    public WatcherMainUI() {
        super();
        myFrame = new JFrame("Team E File System Watcher");
        buildUI();
    }
    /**
     * Build UI for main frame.
     */
    public void buildUI(){
        myFrame.setLayout(new BorderLayout());
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setFocusable(true);
        myFrame.setResizable(false);
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
        JButton sendButton = new JButton("Send");
        JButton exportButton = new JButton("Export");
        sendButton.addActionListener(e -> {
            GmailPanel gmailPanel = new GmailPanel(myFrame);
            GmailController controller = new GmailController(gmailPanel);
        });
        exportButton.addActionListener(e -> {
            String fileName = "example";
            String ext = ".txt";
            String filePath = "/Users/me/Documents/example.txt";
            String change = "MODIFIED";
            String timeStamp = "2025-11-23T12:34:56";
            CSVExportPanel csvPanel = new CSVExportPanel();
            CSVController csvController = new CSVController(csvPanel, fileName, ext, filePath, change, timeStamp);

            JFrame frame = new JFrame();
            csvController.showInDialog(frame);
            JFrame demo = new JFrame("CSV Export Demo");
            demo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            demo.getContentPane().add(csvPanel);
            demo.pack();
            demo.setLocationRelativeTo(null);
            demo.setVisible(true);
        });
        topBar.add(sendButton);
        topBar.add(exportButton);
        JPanel folderPathPanel = new PathPanel();
        JPanel middlePanel = new JPanel(new GridLayout(1, 2));
        JPanel eventListPanel = new EventPanel();
        JPanel bottomPanel = new StatusBar(new EventStats(), List::of);
        middlePanel.add(folderPathPanel);
        middlePanel.add(eventListPanel);

        myFrame.add(topBar, BorderLayout.NORTH);
        myFrame.add(middlePanel, BorderLayout.CENTER);
        myFrame.add(bottomPanel, BorderLayout.SOUTH);
    }
}
