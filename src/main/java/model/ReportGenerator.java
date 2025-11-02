package teame.fs;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportGenerator
 * ---------------------
 * Builds simple summaries and human-readable reports from FileEventLog data.
 *
 * What it does:
 *  - Counts events by type (CREATE/MODIFY/DELETE/MOVE).
 *  - Finds the top-N files by number of changes.
 *  - Filters a time window for the report (optional).
 *  - Produces a text report string for display or saving.
 *
 * What it does NOT do:
 *  - It does not write CSV files (that’s owned by your teammate’s CreateCSVFile/CsvExporter).
 *
 * Author: Merra Migora
 * Iteration: 2
 */
public class ReportGenerator {

    private final FileEventLog log;
    private final DateTimeFormatter stampFmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public ReportGenerator(FileEventLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    /**
     * Returns all events, optionally filtered by a date range.
     * If both start and end are null, returns everything.
     */
    public List<FileEventLog.EventRecord> eventsInRange(Instant startInclusive, Instant endInclusive) {
        List<FileEventLog.EventRecord> all = log.getAllEvents();
        if (startInclusive == null && endInclusive == null) return all;

        List<FileEventLog.EventRecord> result = new ArrayList<>();
        for (FileEventLog.EventRecord e : all) {
            boolean okStart = (startInclusive == null) || !e.getTimeStamp().isBefore(startInclusive);
            boolean okEnd   = (endInclusive == null)   || !e.getTimeStamp().isAfter(endInclusive);
            if (okStart && okEnd) result.add(e);
        }
        return result;
    }

    /**
     * Counts number of events per eventType (case-insensitive).
     */
    public Map<String, Integer> countByType(List<FileEventLog.EventRecord> events) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (FileEventLog.EventRecord e : events) {
            String key = e.getEventType().toUpperCase(Locale.ROOT);
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }

    /**
     * Computes the top N files by number of changes (by full file path).
     */
    public List<FileStat> topFilesByChanges(List<FileEventLog.EventRecord> events, int topN) {
        Map<String, Integer> tally = new HashMap<>();
        for (FileEventLog.EventRecord e : events) {
            String path = e.getFilePath();
            tally.put(path, tally.getOrDefault(path, 0) + 1);
        }
        List<FileStat> stats = new ArrayList<>();
        for (Map.Entry<String, Integer> en : tally.entrySet()) {
            stats.add(new FileStat(en.getKey(), en.getValue()));
        }
        stats.sort((a, b) -> Integer.compare(b.changeCount, a.changeCount));
        if (topN > 0 && stats.size() > topN) {
            return new ArrayList<>(stats.subList(0, topN));
        }
        return stats;
    }

    /**
     * Builds a human-readable text report.
     * If start/end are null, the report covers all events.
     */
    public String buildTextReport(Instant startInclusive, Instant endInclusive, int topNFiles) {
        List<FileEventLog.EventRecord> window = eventsInRange(startInclusive, endInclusive);
        Map<String, Integer> counts = countByType(window);
        List<FileStat> topFiles = topFilesByChanges(window, topNFiles);

        StringBuilder sb = new StringBuilder();
        sb.append("==== File System Watcher Report ====\n");
        if (startInclusive != null || endInclusive != null) {
            sb.append("Range: ");
            sb.append(startInclusive == null ? "(-∞)" : stampFmt.format(startInclusive));
            sb.append("  to  ");
            sb.append(endInclusive == null ? "(+∞)" : stampFmt.format(endInclusive));
            sb.append("\n");
        } else {
            sb.append("Range: ALL EVENTS\n");
        }
        sb.append("Generated: ").append(stampFmt.format(Instant.now())).append("\n\n");

        // Totals
        sb.append("Totals by Type:\n");
        if (counts.isEmpty()) {
            sb.append("  (no events)\n");
        } else {
            for (Map.Entry<String, Integer> en : counts.entrySet()) {
                sb.append("  ").append(en.getKey()).append(": ").append(en.getValue()).append("\n");
            }
        }
        sb.append("\n");

        // Top files
        sb.append("Top Files by Changes");
        if (topNFiles > 0) sb.append(" (Top ").append(topNFiles).append(")");
        sb.append(":\n");
        if (topFiles.isEmpty()) {
            sb.append("  (no events)\n");
        } else {
            int rank = 1;
            for (FileStat fs : topFiles) {
                sb.append(String.format("  %d) %s  —  %d change(s)\n", rank++, fs.filePath, fs.changeCount));
            }
        }
        sb.append("\n");

        // Optional: List first few raw events for context
        sb.append("Sample Events:\n");
        int sample = Math.min(5, window.size());
        if (sample == 0) {
            sb.append("  (no events)\n");
        } else {
            for (int i = 0; i < sample; i++) {
                FileEventLog.EventRecord e = window.get(i);
                sb.append("  • ")
                  .append(stampFmt.format(e.getTimeStamp()))
                  .append("  ")
                  .append(e.getEventType())
                  .append("  ")
                  .append(e.getFilePath())
                  .append("  (")
                  .append(e.getFileName())
                  .append(", user=")
                  .append(e.getUser())
                  .append(")\n");
            }
        }
        return sb.toString();
    }

    /**
     * Convenience container for top-files listing.
     */
    public static final class FileStat {
        public final String filePath;
        public final int changeCount;

        public FileStat(String filePath, int changeCount) {
            this.filePath = filePath;
            this.changeCount = changeCount;
        }
    }
}
