// Package declaration: This class belongs to the 'view' package of the MVC structure.
package view;

// Import the FileEventLog class from the model package to access event data.
import model.FileEventLog;

// Import Swing components for GUI construction.
import javax.swing.*;
// Import AbstractTableModel for defining a custom table model.
import javax.swing.table.AbstractTableModel;
// Import AWT components for layout management.
import java.awt.*;
// Import utilities for handling timestamps and formatting.
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
// Import collections to store multiple events.
import java.util.ArrayList;
import java.util.List;

/**
 * Table view of file system events with Clear support.
 * Displays file events in a scrollable table and allows clearing all entries.
 *
 * @author Mihretu Gebre
 * @version Iteration 5
 */
public class EventPanel extends JPanel {

    // Custom table model to hold and manage file event data.
    private final EventTableModel tableModel = new EventTableModel();

    // JTable UI component that uses the table model to display data.
    private final JTable table = new JTable(tableModel);

    // Constructor: Initializes the panel layout and UI components.
    public EventPanel() {
        // Calls JPanel constructor with a BorderLayout and spacing (8px horizontal and vertical gaps).
        super(new BorderLayout(8, 8));

        // Adds a titled border labeled "Events" to the panel.
        setBorder(BorderFactory.createTitledBorder("Events"));

        // Ensures table fills the available viewport height.
        table.setFillsViewportHeight(true);

        // Enables automatic sorting of table columns.
        table.setAutoCreateRowSorter(true);

        // Adds the table (wrapped in a JScrollPane for scrolling) to the center of the panel.
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Creates a "Clear" button for removing all table entries.
        JButton clear = new JButton("Clear");

        // Adds an action listener that clears the table when the button is clicked.
        clear.addActionListener(e -> tableModel.clear());

        // Creates a panel for the button aligned to the right.
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Adds the "Clear" button to the bottom panel.
        south.add(clear);

        // Adds the button panel to the bottom (SOUTH) of the layout.
        add(south, BorderLayout.SOUTH);
    }

    // Adds a single FileEventLog.EventRecord to the table model.
    public void addEvent(FileEventLog.EventRecord e) { tableModel.add(e); }

    // Sets a new list of events, replacing existing ones.
    public void setEvents(List<FileEventLog.EventRecord> list) { tableModel.set(list); }

    // Inner class defining the table model (data layer for JTable).
    private static final class EventTableModel extends AbstractTableModel {

        // Column headers for the table.
        private final String[] cols = {"Time", "Type", "File", "Path", "User"};

        // Date formatter for displaying timestamps in human-readable form.
        private final DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        // List holding all file event records.
        private final List<FileEventLog.EventRecord> rows = new ArrayList<>();

        // Returns the number of rows (records) in the table.
        @Override public int getRowCount() { return rows.size(); }

        // Returns the number of columns.
        @Override public int getColumnCount() { return cols.length; }

        // Returns the column name for display in the table header.
        @Override public String getColumnName(int c) { return cols[c]; }

        // Returns the value for each cell in the table based on row and column index.
        @Override
        public Object getValueAt(int r, int c) {
            // Retrieves the event record at row index 'r'.
            FileEventLog.EventRecord e = rows.get(r);

            // Returns the appropriate value depending on which column is requested.
            return switch (c) {
                case 0 -> fmt.format(e.getTimeStamp()); // Formats timestamp
                case 1 -> e.getEventType();              // Displays event type (e.g., CREATED, DELETED)
                case 2 -> e.getFileName();               // Displays file name
                case 3 -> e.getFilePath();               // Displays file path
                case 4 -> e.getUser();                   // Displays user who triggered the event
                default -> "";                           // Returns empty string for invalid index
            };
        }

        // Adds a single event record to the table and updates the view.
        void add(FileEventLog.EventRecord e) {
            int at = rows.size();     // Gets current size to determine insertion index.
            rows.add(e);              // Adds the new record.
            fireTableRowsInserted(at, at); // Notifies the table view that a new row was inserted.
        }

        // Replaces all current records with a new list and refreshes the view.
        void set(List<FileEventLog.EventRecord> list) {
            rows.clear();             // Clears existing records.
            rows.addAll(list);        // Adds all new records.
            fireTableDataChanged();   // Notifies the view that the table data has changed.
        }

        // Clears all records from the table and refreshes the display.
        void clear() {
            int n = rows.size();      // Gets the current row count.
            if (n == 0) return;       // If table is already empty, exit.
            rows.clear();             // Clears all event records.
            fireTableRowsDeleted(0, n - 1); // Notifies view that rows were deleted.
        }
    }
}
