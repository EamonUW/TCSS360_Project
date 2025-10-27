package teame.fs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Low-level directory watcher built on Java NIO WatchService.
 * - Watches multiple directories (non-recursive by default).
 * - Emits CREATE / MODIFY / DELETE events to a listener.
 * - MOVE is inferred as a DELETE followed by a CREATE on the same file name (best-effort).
 */
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

    public FileSystemWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    /** Sets the listener to receive file events. */
    public void setListener(FileChangeListener listener) {
        this.listener = listener;
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
                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                @SuppressWarnings("unchecked")
                Path rel = ((WatchEvent<Path>) event).context();
                Path fullPath = dir.resolve(rel);

                EventType type = mapKind(kind);
                FileChangeListener l = listener;
                if (l != null && type != null) {
                    long now = System.currentTimeMillis();
                    String user = System.getProperty("user.name", "unknown");
                    try {
                        l.onEvent(dir, fullPath, type, now, user);
                    } catch (Exception ex) {
                        l.onError(ex);
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                synchronized (keyToDir) { keyToDir.remove(key); }
            }
        }
    }

    private static EventType mapKind(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) return EventType.CREATE;
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) return EventType.MODIFY;
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) return EventType.DELETE;
        return null;
    }

    @Override
    public void close() {
        stop();
    }
}
