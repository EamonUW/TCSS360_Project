package teame.fs;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ConfigLoader
 * -------------
 * Small, dependency-free config utility.
 * - Stores settings to a JSON file (pretty-printed).
 * - Loads from JSON; also accepts simple .properties fallback.
 *
 * Supported keys:
 *   watchPaths      : List<String> (directories to watch)
 *   debounceMillis  : int          (optional; default 250)
 *   autoStart       : boolean      (optional; default false)
 *   smtpUser        : String       (optional)
 *   smtpHost        : String       (optional)
 */
public class ConfigLoader {

    // ---- Stored fields ----
    private final List<String> watchPaths = new ArrayList<>();
    private int debounceMillis = 250;
    private boolean autoStart = false;
    private String smtpUser;
    private String smtpHost;

    // ---- Accessors ----
    public List<String> getWatchPaths() { return Collections.unmodifiableList(watchPaths); }
    public void setWatchPaths(Collection<String> paths) {
        watchPaths.clear();
        if (paths != null) watchPaths.addAll(paths);
    }
    public void addWatchPath(String path) { if (path != null && !path.isBlank()) watchPaths.add(path); }

    public int getDebounceMillis() { return debounceMillis; }
    public void setDebounceMillis(int debounceMillis) { this.debounceMillis = Math.max(0, debounceMillis); }

    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }

    public String getSmtpUser() { return smtpUser; }
    public void setSmtpUser(String smtpUser) { this.smtpUser = emptyToNull(smtpUser); }

    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = emptyToNull(smtpHost); }

    private String emptyToNull(String s) { return (s == null || s.isBlank()) ? null : s; }

    // ---- Load / Save ----

    /** Loads config from a JSON file if present; falls back to .properties if JSON parse fails. */
    public void load(Path file) throws IOException {
        if (file == null || !Files.exists(file)) return;
        try (Reader r = Files.newBufferedReader(file)) {
            String txt = readAll(r);
            if (txt.trim().startsWith("{")) {
                parseJson(txt);
                return;
            }
        } catch (Exception jsonFail) {
            // fall through to .properties
        }
        // Properties fallback
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(file)) {
            p.load(r);
        }
        fromProperties(p);
    }

    /** Saves config to a JSON file (pretty-printed). */
    public void save(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        try (Writer w = Files.newBufferedWriter(file)) {
            w.write(toPrettyJson());
        }
    }

    // ---- Helpers: JSON (very small hand-rolled for known keys) ----

    private void parseJson(String json) {
        // Clear first then fill
        watchPaths.clear();
        debounceMillis = 250;
        autoStart = false;
        smtpUser = null;
        smtpHost = null;

        // watchPaths: ["a","b",...]
        Matcher arr = Pattern.compile("\"watchPaths\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL).matcher(json);
        if (arr.find()) {
            String inner = arr.group(1);
            Matcher m = Pattern.compile("\"(.*?)\"").matcher(inner);
            while (m.find()) watchPaths.add(unescape(m.group(1)));
        }

        Integer d = intField(json, "debounceMillis");
        if (d != null) debounceMillis = Math.max(0, d);

        Boolean a = boolField(json, "autoStart");
        if (a != null) autoStart = a;

        String su = stringField(json, "smtpUser");
        if (su != null) smtpUser = unescape(su);

        String sh = stringField(json, "smtpHost");
        if (sh != null) smtpHost = unescape(sh);
    }

    private String toPrettyJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        // watchPaths
        sb.append("  \"watchPaths\": [");
        for (int i = 0; i < watchPaths.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(escape(watchPaths.get(i))).append("\"");
        }
        sb.append("],\n");

        sb.append("  \"debounceMillis\": ").append(debounceMillis).append(",\n");
        sb.append("  \"autoStart\": ").append(autoStart).append(",\n");
        sb.append("  \"smtpUser\": ").append(smtpUser == null ? "null" : "\"" + escape(smtpUser) + "\"").append(",\n");
        sb.append("  \"smtpHost\": ").append(smtpHost == null ? "null" : "\"" + escape(smtpHost) + "\"").append("\n");

        sb.append("}\n");
        return sb.toString();
    }

    private String readAll(Reader r) throws IOException {
        char[] buf = new char[4096];
        StringBuilder sb = new StringBuilder();
        int n;
        while ((n = r.read(buf)) >= 0) sb.append(buf, 0, n);
        return sb.toString();
    }

    private static String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
    private static String unescape(String s) { return s.replace("\\\"", "\"").replace("\\\\", "\\"); }

    private static Integer intField(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    private static Boolean boolField(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)").matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : null;
    }

    private static String stringField(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    // ---- Properties fallback ----

    private void fromProperties(Properties p) {
        watchPaths.clear();
        String paths = p.getProperty("watchPaths", "");
        if (!paths.isBlank()) {
            for (String s : paths.split(";")) {
                if (!s.isBlank()) watchPaths.add(s.trim());
            }
        }
        try {
            debounceMillis = Integer.parseInt(p.getProperty("debounceMillis", "250"));
        } catch (NumberFormatException ignored) { debounceMillis = 250; }
        autoStart = Boolean.parseBoolean(p.getProperty("autoStart", "false"));
        smtpUser = emptyToNull(p.getProperty("smtpUser", null));
        smtpHost = emptyToNull(p.getProperty("smtpHost", null));
    }
}
