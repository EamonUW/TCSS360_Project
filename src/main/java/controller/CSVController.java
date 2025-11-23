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
 */
public class CSVController {

    private final CSVExportPanel myView;
    private final String myFileName;
    private final String myFileExtension;
    private final String myFilePath;
    private final String myFileChange;
    private final String myTimeStamp;

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
     * Utility: present the panel in a modal dialog (handy for quick wiring).
     */
    public void showInDialog(Component parent) {
        int result = JOptionPane.showConfirmDialog(parent, myView, "Export CSV", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            onExport();
        }
    }
}
