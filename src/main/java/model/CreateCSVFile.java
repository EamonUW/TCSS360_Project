package model;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class creates a csv (comma separated value) file representing a file event.
 *
 * @author Eamon
 * @version 2.3
 */
public class CreateCSVFile {
    /**
     * File name.
     */
    private final String myFileName;
    /**
     * File extension.
     */
    private final String myFileExtension;
    /**
     * File path.
     */
    private final String myFilePath;
    /**
     * Time stamp.
     */
    private final String myTimeStamp;
    /**
     * File change.
     */
    private final String myFileChange;
    /**
     * Export location.
     */
    private String myExportLocation;

    public CreateCSVFile(String theFileName, String theFileExtension,
                         String theFilePath, String theFileChange, String theTimeStamp) {
        myFileName = theFileName;
        myFileExtension = theFileExtension;
        myFilePath = theFilePath;
        myTimeStamp = theTimeStamp;
        myFileChange = theFileChange;
    }

    /**
     * Setter for export file location.
     *
     * @param exportLocation File path for CSV file.
     */
    public void setExportLocation(String exportLocation) {
        myExportLocation = exportLocation;
    }

    /**
     * Creates CSV file to user specified file path.
     *
     * @param theData CSV file data for table (if null, will use createData()).
     */
    public void exportNewCSV(ArrayList<String[]> theData) {
        if (myExportLocation == null || myExportLocation.isEmpty()) {
            throw new IllegalStateException("No found export location.");
        }

        ArrayList<String[]> dataToWrite = theData == null ? createData() : theData;

        File file = new File(myExportLocation);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter output = new FileWriter(file);
             CSVWriter writer = new CSVWriter(output)) {
            writer.writeAll(dataToWrite);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV: " + e.getMessage(), e);
        }
    }
    /**
     * Creates CSV to be included as an attachment to an email.
     *
     * @param theData CSV file data for table (if null, will use createData()).
     */
    public File createEmailCSV(ArrayList<String[]> theData) {
        ArrayList<String[]> dataToWrite = theData == null ? createData() : theData;

        File file = new File("log.csv");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter output = new FileWriter(file);
            CSVWriter writer = new CSVWriter(output)) {
            writer.writeAll(dataToWrite);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Formats data for csv file output.
     *
     * @return data the data for csv table.
     */
    public ArrayList<String[]> createData() {
        ArrayList<String[]> data = new ArrayList<>();
        data.add(new String[]{"File Name", "File Extension",
                "File Path", "File Change", "Time Stamp"});
        data.add(new String[]{myFileName, myFileExtension,
                myFilePath, myFileChange, myTimeStamp});
        return data;
    }
}