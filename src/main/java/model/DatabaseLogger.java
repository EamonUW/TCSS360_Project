package java.model;

import java.util.ArrayList;
import java.util.List;

public class DatabaseLogger {

    private final List<FileEventInfo> eventLog = new ArrayList<>();

    public void logFileEventInfo(FileEventInfo event) {
        eventLog.add(event);
    }

    public List<FileEventInfo> getEventInfoLog() {
        return new ArrayList<>(eventLog); // Return safe copy
    }

    public List<FileEventInfo> queryByActivity(String activity) {
        List<FileEventInfo> result = new ArrayList<>();
        for (FileEventInfo event : eventLog) {
            if (event.getFileActivity().equalsIgnoreCase(activity)) {
                result.add(event);
            }
        }
        return result;
    }
}
