package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class creates a popup UI for creating a csv file based on an absolute file path.
 *
 * @author Eamon
 * @version 3.2
 */
public class CSVExportPanel extends JPanel {
    /**
     * The text field for selected folder path.
     */
    private JTextField myFolderField;
    /**
     * The export button for folder path.
     */
    private JButton myExportButton;
    /**
     * The text field for file name.
     */
    private JTextField myFileNameField;
    /**
     * String path of for selected folder path.
     */
    private String mySelectedFolder;
    /**
     * Constructor for CSVExportPanel.
     */
    public CSVExportPanel() {
        super(new BorderLayout());
        buildUI();
    }
    /**
     * Creates and shows the UI for the CSVExportPanel as a pop-up.
     */
    private void buildUI() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel folderRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel folderLabel = new JLabel("Export folder:");
        myFolderField = new JTextField(30);
        myFolderField.setEditable(false);

        JButton myChooseButton = new JButton("Choose...");
        folderRow.add(folderLabel);
        folderRow.add(myFolderField);
        folderRow.add(myChooseButton);

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Export filename:");
        myFileNameField = new JTextField("export.csv", 30);
        nameRow.add(nameLabel);
        nameRow.add(myFileNameField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        myExportButton = new JButton("Export");

        JButton myCancelButton = new JButton("Cancel");
        buttons.add(myCancelButton);
        buttons.add(myExportButton);

        center.add(folderRow);
        center.add(nameRow);

        this.add(center, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.SOUTH);

        myChooseButton.addActionListener(e -> openFolderChooser());
        myCancelButton.addActionListener(e -> {
            mySelectedFolder = null;
            myFolderField.setText("");
        });
    }
    /**
     * Creates a JFileChooser menu to select a specified file path.
     */
    private void openFolderChooser() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setAcceptAllFileFilterUsed(false);
        int returnValue = folderChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = folderChooser.getSelectedFile();
            mySelectedFolder = selectedFile.getAbsolutePath();
            myFolderField.setText(mySelectedFolder);
        }
    }
    /**
     * Add action listener for add button.
     *
     * @param listener creates action listener for export button.
     */
    public void addExportActionListener(ActionListener listener) {
        myExportButton.addActionListener(listener);
    }

    /**
     * Get the currently selected folder path.
     */
    public String getSelectedFolder() {
        return mySelectedFolder;
    }

    /**
     * Get the filename to export.
     */
    public String getExportFilename() {
        return myFileNameField.getText();
    }

    /**
     * Enable/disable the Export button.
     */
    public void setExportEnabled(boolean enabled) {
        myExportButton.setEnabled(enabled);
    }
}