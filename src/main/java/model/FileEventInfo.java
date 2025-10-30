package model;

public final class FileEventInfo {

    private final String fileName;
    private final String fileExtension;
    private final String filePath;
    private final String fileActivity;
    private final String timeStamp;

    public FileEventInfo(String fileName, String fileExtension, String filePath,
                         String fileActivity, String timeStamp) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.fileActivity = fileActivity;
        this.timeStamp = timeStamp;
    }

    public String getFileName() { return fileName; }
    public String getFileExtension() { return fileExtension; }
    public String getFilePath() { return filePath; }
    public String getFileActivity() { return fileActivity; }
    public String getTimeStamp() { return timeStamp; }

}

