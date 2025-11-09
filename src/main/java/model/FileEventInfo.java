package model;

// Marks the class as final, meaning it cannot be subclassed.
public final class FileEventInfo {

    // Stores the name of the file involved in the event.
    private final String fileName;

    // Stores the file extension (e.g., .txt, .jpg, .pdf).
    private final String fileExtension;

    // Stores the full directory path where the file is located.
    private final String filePath;

    // Stores the type of file activity (created, modified, deleted, moved).
    private final String fileActivity;

    // Stores a timestamp indicating when the event occurred.
    private final String timeStamp;

    // Constructor that initializes all fields when a new file event is created.
    public FileEventInfo(String fileName, String fileExtension, String filePath,
                         String fileActivity, String timeStamp) {

        // Initializes the file name.
        this.fileName = fileName;

        // Initializes the file extension.
        this.fileExtension = fileExtension;

        // Initializes the file path.
        this.filePath = filePath;

        // Initializes the type of file activity.
        this.fileActivity = fileActivity;

        // Initializes the timestamp of the event.
        this.timeStamp = timeStamp;
    }

    // Getter method for retrieving the file name.
    public String getFileName() { return fileName; }

    // Getter method for retrieving the file extension.
    public String getFileExtension() { return fileExtension; }

    // Getter method for retrieving the full file path.
    public String getFilePath() { return filePath; }

    // Getter method for retrieving the file activity type.
    public String getFileActivity() { return fileActivity; }

    // Getter method for retrieving the timestamp.
    public String getTimeStamp() { return timeStamp; }

}
