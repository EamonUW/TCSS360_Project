package model;
/**
 * @author Mihretu Gebre
 * @version Iteration 5
 */

public final class FileEventInfo {

    /** Stores the name of the file involved in the event. */
    private final String fileName;

    /** Stores the file extension (e.g., .txt, .jpg, .pdf). */
    private final String fileExtension;

    /** Stores the full directory path where the file is located. */
    private final String filePath;

    /** Stores the type of activity (Created, Modified, Deleted, Moved). */
    private final String fileActivity;

    /** Stores the timestamp indicating when the event occurred. */
    private final String timeStamp;

    /**
     * Constructs a new FileEventInfo object with all event details.
     *
     * @param fileName      the name of the file
     * @param fileExtension the file extension (for filtering/sorting)
     * @param filePath      the full directory path
     * @param fileActivity  the type of activity performed on the file
     * @param timeStamp     when the event occurred
     */
    public FileEventInfo(String fileName, String fileExtension, String filePath,
                         String fileActivity, String timeStamp) {

        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.fileActivity = fileActivity;
        this.timeStamp = timeStamp;
    }

    /** @return the file name */
    public String getFileName() { return fileName; }

    /** @return the file extension */
    public String getFileExtension() { return fileExtension; }

    /** @return the full file path */
    public String getFilePath() { return filePath; }

    /** @return the file activity (Created, Modified, Deleted, etc.) */
    public String getFileActivity() { return fileActivity; }

    /** @return the timestamp of the event */
    public String getTimeStamp() { return timeStamp; }

    /**
     * Added for compatibility with SearchFilter.
     * Many parts of the project refer to this value as "event type",
     * so this getter simply returns the same value as fileActivity.
     *
     * @return the event type
     */
    public String getEventType() {
        return fileActivity;
    }
}
