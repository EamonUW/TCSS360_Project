package view;

import model.FileEventLog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Table view of file system events with Clear support.
 *
 * @author Mihretu Gebre
 * @version Iteration 3
 */
public class EventPanel extends JPanel {

    private final EventTableModel tableModel = new EventTableModel();
    private final JTable table = new JTable(tableModel);

    public EventPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Events"));

        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> tableModel.clear());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(clear);
        add(south, BorderLayout.SOUTH);
    }

    public void addEvent(FileEventLog.EventRecord e) { tableModel.add(e); }
    public void setEvents(List<FileEventLog.EventRecord> list) { tableModel.set(list); }

    private static final class EventTableModel extends AbstractTableModel {
        private final String[] cols = {"Time", "Type", "File", "Path", "User"};
        private final DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        private final List<FileEventLog.EventRecord> rows = new ArrayList<>();

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int r, int c) {
            FileEventLog.EventRecord e = rows.get(r);
            return switch (c) {
                case 0 -> fmt.format(e.getTimeStamp());
                case 1 -> e.getEventType();
                case 2 -> e.getFileName();
                case 3 -> e.getFilePath();
                case 4 -> e.getUser();
                default -> "";
            };
        }

        void add(FileEventLog.EventRecord e) {
            int at = rows.size();
            rows.add(e);
            fireTableRowsInserted(at, at);
        }
        void set(List<FileEventLog.EventRecord> list) {
            rows.clear();
            rows.addAll(list);
            fireTableDataChanged();
        }
        void clear() {
            int n = rows.size();
            if (n == 0) return;
            rows.clear();
            fireTableRowsDeleted(0, n - 1);
        }
    }
}
