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
    private final String myFileName;
    private final String myFileExtension;
    private final String myFilePath;
    private final String myTimeStamp;
    private final String myFileChange;
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
    public void createNewCSV(ArrayList<String[]> theData) {
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