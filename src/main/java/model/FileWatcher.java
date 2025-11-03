package java.model;

import java.nio.file.Path;
import java.util.*;


public class FileWatcher {

    /** Optional sink for persisting events (e.g., DatabaseLogger). */
    interface EventSink {
        void save(Event evt);
    }

    /** Optional notifier for sending user alerts (e.g., EmailNotification). */
    interface Notifier {
        void notify(Event evt);
    }

    /** Simple event object for this layer (maps cleanly to your FileEventInfo). */
    public static final class Event {
        public final String fileName;
        public final String filePath;
        public final String type;     // CREATE / MODIFY / DELETE
        public final long occurredAt; // epoch millis
        public final String user;

        public Event(String fileName, String filePath, String type, long occurredAt, String user) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.type = type;
            this.occurredAt = occurredAt;
            this.user = user;
        }

        @Override public String toString() {
            return "Event{type=" + type + ", file=" + filePath + ", at=" + occurredAt + ", user=" + user + "}";
        }
    }

    private final FileSystemWatcher watcher;
    private final List<Path> watchPaths = new ArrayList<>();
    private EventSink sink;       // optional
    private Notifier notifier;    // optional
    private boolean started = false;

    public FileWatcher(FileSystemWatcher watcher) {
        this.watcher = Objects.requireNonNull(watcher, "watcher");
        this.watcher.setListener((dir, path, type, when, user) -> {
            Event evt = new Event(
                    path.getFileName().toString(),
                    path.toAbsolutePath().toString(),
                    type.name(),
                    when,
                    user
            );
            // Log / persist
            if (sink != null) {
                try { sink.save(evt); } catch (Exception ex) { ex.printStackTrace(); }
            }
            // Notify (email, etc.)
            if (notifier != null) {
                try { notifier.notify(evt); } catch (Exception ex) { ex.printStackTrace(); }
            }
            // Always print for quick visibility during dev
            System.out.println(evt);
        });
    }

    /** Add a directory path to be watched (call before start; or call anytime to extend). */
    public FileWatcher addPath(Path dir) {
        this.watchPaths.add(Objects.requireNonNull(dir, "dir"));
        return this;
    }

    /** Set optional sink (e.g., DatabaseLogger). */
    public FileWatcher withSink(EventSink sink) {
        this.sink = sink;
        return this;
    }

    /** Set optional notifier (e.g., EmailNotification). */
    public FileWatcher withNotifier(Notifier notifier) {
        this.notifier = notifier;
        return this;
    }

    /** Start watching all registered directories. */
    public synchronized void start() throws Exception {
        if (started) return;
        if (watchPaths.isEmpty()) {
            throw new IllegalStateException("No directories added. Call addPath(Path) before start().");
        }
        for (Path p : watchPaths) {
            watcher.addDirectory(p); // or watcher.addDirectoryRecursive(p) if needed
        }
        watcher.start();
        started = true;
    }

    /** Stop the watcher and release resources. */
    public synchronized void stop() {
        if (!started) return;
        watcher.stop();
        started = false;
    }
}
