package model;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FileSystemWatcher implements AutoCloseable {

    public enum EventType { CREATE, MODIFY, DELETE }

    /** Callback for file change events. */
    public interface FileChangeListener {
        void onEvent(Path dir, Path path, EventType type, long whenEpochMillis, String user);
        default void onError(Exception ex) { ex.printStackTrace(); }
    }

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyToDir = new HashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread loopThread;
    private volatile FileChangeListener listener;
    private FileEventInfo myFileEventInfo;

    // Property change support
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public FileSystemWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    /** Sets the listener to receive file events. */
    public void setListener(FileChangeListener listener) {
        this.listener = listener;
    }

    /** Adds a PropertyChangeListener. Property name used: "fileEventInfo" for new events,
     * "fileEventInfoError" for errors. */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /** Removes a previously added PropertyChangeListener. */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    /** Register a single directory (non-recursive). */
    public void addDirectory(Path dir) throws IOException {
        Objects.requireNonNull(dir, "dir");
        if (!Files.isDirectory(dir)) {
            throw new NotDirectoryException(dir.toString());
        }
        WatchKey key = dir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );
        synchronized (keyToDir) {
            keyToDir.put(key, dir);
        }
        System.out.println("File added to watcher: " + dir);
    }

    /** Optionally register a directory and all subfolders (recursive). */
    public void addDirectoryRecursive(Path root) throws IOException {
        Objects.requireNonNull(root, "root");
        if (!Files.isDirectory(root)) throw new NotDirectoryException(root.toString());
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                addDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /** Remove a previously-registered directory. */
    public void removeDirectory(Path dir) {
        synchronized (keyToDir) {
            keyToDir.entrySet().removeIf(e -> {
                if (e.getValue().equals(dir)) {
                    e.getKey().cancel();
                    return true;
                }
                return false;
            });
        }
    }

    /** Start the background watch loop. Safe to call once. */
    public void start() {
        if (running.compareAndSet(false, true)) {
            loopThread = new Thread(this::runLoop, "FileSystemWatcher-Loop");
            loopThread.setDaemon(true);
            loopThread.start();
        }
    }

    /** Stop the background loop and close the watch service. */
    public void stop() {
        running.set(false);
        try { watchService.close(); } catch (IOException ignored) {}
        if (loopThread != null) {
            try { loopThread.join(1000); } catch (InterruptedException ignored) {}
        }
    }

    private void runLoop() {
        System.out.println("Starting FileSystemWatcher");
        while (running.get()) {
            WatchKey key;
            try {
                key = watchService.take(); // blocks
            } catch (InterruptedException e) {
                break;
            } catch (ClosedWatchServiceException cwse) {
                break;
            }
            Path dir;
            synchronized (keyToDir) {
                dir = keyToDir.get(key);
            }
            if (dir == null) {
                key.reset();
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filename = (Path) event.context();
                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                @SuppressWarnings("unchecked")
                Path rel = ((WatchEvent<Path>) event).context();
                Path fullPath = dir.resolve(rel);

                EventType type = mapKind(kind);
                String fileName = filename.toString();
                String filePath = fullPath.toString();
                String fileExtension = "";
                int idx = fileName.lastIndexOf('.');
                if (idx > 0 && idx < fileName.length() - 1) {
                    fileExtension = fileName.substring(idx + 1);
                }
                assert type != null;
                String fileEvent = type.name();
                String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                FileChangeListener l = listener;
                try {
                    // create the FileEventInfo instance
                    myFileEventInfo = new FileEventInfo(fileName, fileExtension, filePath, fileEvent, time);

                    // existing callback
                    if (l != null) {
                        long now = System.currentTimeMillis();
                        String user = System.getProperty("user.name", "unknown");
                        l.onEvent(dir, fullPath, type, now, user);
                    }

                    // fire property change so listeners (UI etc.) can react
                    pcs.firePropertyChange("fileEventInfo", null, myFileEventInfo);

                } catch (Exception ex) {
                    // notify both the callback and property listeners about the error
                    if (l != null) l.onError(ex);
                    pcs.firePropertyChange("fileEventInfoError", null, ex);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                synchronized (keyToDir) { keyToDir.remove(key); }
            }
        }
    }

    private static EventType mapKind(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            System.out.println("Created");
            return EventType.CREATE;
        }
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            System.out.println("Modified");
            return EventType.MODIFY;
        }
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            System.out.println("Deleted");
            return EventType.DELETE;
        }
        return null;
    }

    @Override
    public void close() {
        stop();
    }
}
