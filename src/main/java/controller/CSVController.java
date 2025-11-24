package controller;

import model.CreateCSVFile;
import view.CSVExportPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Controller connecting the CSVExportPanel view with the CreateCSVFile model.
 *
 * @author Eamon
 * @version 5.1
 */
public class CSVController {
    /**
     * Instance of CSVExportPanel.
     */
    private final CSVExportPanel myView;
    /**
     * File name in csv file log.
     */
    private final String myFileName;
    /**
     * File extension in csv file log.
     */
    private final String myFileExtension;
    /**
     * File path in csv file log.
     */
    private final String myFilePath;
    /**
     * File change in csv file log.
     */
    private final String myFileChange;
    /**
     * Timestamp in csv file log.
     */
    private final String myTimeStamp;
    /**
     * Constructor for CSVController.
     *
     * @param theView Instance of CSVExportPanel.
     * @param theFileName file name.
     * @param theFileExtension file extension.
     * @param theFilePath file path.
     * @param theFileChange file change.
     * @param theTimeStamp time stamp.
     */
    public CSVController(CSVExportPanel theView, String theFileName,
                         String theFileExtension, String theFilePath,
                         String theFileChange, String theTimeStamp) {
        myView = theView;
        myFileName = theFileName;
        myFileExtension = theFileExtension;
        myFilePath = theFilePath;
        myFileChange = theFileChange;
        myTimeStamp = theTimeStamp;

        myView.addExportActionListener(e -> onExport());
    }
    /**
     * Handle export event and any exceptions.
     */
    private void onExport() {
        String folder = myView.getSelectedFolder();
        if (folder == null || folder.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Please choose an export folder first.",
                    "No folder selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String filename = myView.getExportFilename();
        if (filename == null || filename.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Please provide an export filename.",
                    "Missing filename",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String outFilename = filename.endsWith(".csv") ? filename : filename + ".csv";
        File outFile = new File(folder, outFilename);
        String outPath = outFile.getAbsolutePath();

        myView.setExportEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                CreateCSVFile model = new CreateCSVFile(myFileName, myFileExtension, myFilePath, myFileChange, myTimeStamp);
                model.setExportLocation(outPath);
                model.createNewCSV(null);
                return null;
            }

            @Override
            protected void done() {
                myView.setExportEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(null,
                            "CSV exported to:\n" + outPath,
                            "Export complete",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(null,
                            "Failed to export CSV:\n" + cause.getMessage(),
                            "Export error",
                            JOptionPane.ERROR_MESSAGE);
                    cause.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    /**
     * Creates Export CSV dialog box.
     */
    public void showInDialog(Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Export CSV", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(myView);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
