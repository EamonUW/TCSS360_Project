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
 * @version Iteration 2
 */
public class CreateCSVFile {
    /**
     * Name of file.
     */
    String myFileName;
    /**
     * File extension.
     */
    String myFileExtension;
    /**
     * File path.
     */
    String myFilePath;
    /**
     * Time stamp of file event.
     */
    String myTimeStamp;
    /**
     * Type of file activity.
     */
    String myFileChange;

    String myExportLocation;

    /**
     * Public constructor for CreateCSVFile class.
     *
     * @param theFileName name of file.
     * @param theFileExtension file extension.
     * @param theFilePath file path.
     * @param theFileChange type of file activity.
     * @param theTimeStamp time stamp of file event.
     */
    public CreateCSVFile(String theFileName, String theFileExtension,
                         String theFilePath, String theFileChange, String theTimeStamp) {
        myFileName = theFileName;
        myFileExtension = theFileExtension;
        myFilePath = theFilePath;
        myTimeStamp = theTimeStamp;
        myFileChange = theFileChange;
//        myExportLocation =
    }
    /**
     * Creates CSV file to user specified file path.
     *
     * @param theData CSV file data for table.
     */
    public void createNewCSV(ArrayList<String []> theData){
        try {
            File file = new File(myExportLocation); //Future modification to accept user file path.
            FileWriter output = new FileWriter(file);
            CSVWriter writer = new CSVWriter(output);

            writer.writeAll(createData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Formats data for csv file output.
     *
     * @return data the data for csv table.
     */
    public ArrayList<String []> createData() {
        ArrayList<String []> data = new ArrayList<>();
        data.add(new String[] {"File Name", "File Extension",
                "File Path", "File Change", "Time Stamp"});
        data.add(new String [] {myFileName, myFileExtension,
                myFilePath, myFileChange, myTimeStamp});
        return data;
    }
}
