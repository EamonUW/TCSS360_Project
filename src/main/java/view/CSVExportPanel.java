package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * This class creates a popup UI for creating a csv file based on an absolute file path.
 *
 * @author Eamon
 * @version Iteration 3, 0.1
 */
public class CSVExportPanel extends JPanel {
    /**
     * File path from user.
     */
    private String myFilePath;
    /**
     * Constructor for CSVExportPanel.
     */
    public CSVExportPanel() {
        super();
        createAndShowGUI();
    }
    /**
     * Creates the UI for CSVExportPanel.
     */
    public void createAndShowGUI() {
            JPanel screenPanel = new JPanel();
            JFileChooser folderChooser = new JFileChooser();

            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = folderChooser.showOpenDialog(screenPanel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = folderChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                myFilePath = filePath;
                //System.out.println(myFilePath);
            }
    }
}
