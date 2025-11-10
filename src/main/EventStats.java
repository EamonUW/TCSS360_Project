package teame.fs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EventStats
 * -----------
 * Rolling counters for file events.
 * Call increment("CREATE"/"MODIFY"/"DELETE"/"MOVE") whenever a new event is logged.
 */
public class EventStats {

    private final AtomicLong total = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> byType = new ConcurrentHashMap<>();

    public EventStats() {
        // Preseed common types to keep a stable order in UIs
        byType.putIfAbsent("CREATE", new AtomicLong(0));
        byType.putIfAbsent("MODIFY", new AtomicLong(0));
        byType.putIfAbsent("DELETE", new AtomicLong(0));
        byType.putIfAbsent("MOVE",   new AtomicLong(0));
    }

    /** Increment counters for an event type (case-insensitive). */
    public void increment(String eventType) {
        total.incrementAndGet();
        String key = eventType == null ? "UNKNOWN" : eventType.toUpperCase();
        byType.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }

    /** Total number of events seen. */
    public long total() {
        return total.get();
    }

    /** Snapshot of counts by type. */
    public Map<String, Long> countsByType() {
        Map<String, Long> snap = new LinkedHashMap<>();
        // Keep CREATE/MODIFY/DELETE/MOVE first if present
        for (String k : new String[]{"CREATE","MODIFY","DELETE","MOVE"}) {
            if (byType.containsKey(k)) snap.put(k, byType.get(k).get());
        }
        // Then any others
        byType.forEach((k, v) -> snap.putIfAbsent(k, v.get()));
        return Collections.unmodifiableMap(snap);
    }

    /** Reset all counters. */
    public void reset() {
        total.set(0);
        byType.values().forEach(a -> a.set(0));
    }
}
