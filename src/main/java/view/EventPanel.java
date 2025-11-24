package view;

import model.FileEventInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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

        // Creates a panel to hold buttons at the bottom (SOUTH) of the layout.
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Adds the Clear button to the bottom panel.
        south.add(clear);

        // Adds the button panel to the bottom (SOUTH) of the layout.
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Adds a single FileEventInfo to the table model.
     *
     * @param event the event to add
     */
    public void addEvent(final FileEventInfo event) {
        tableModel.add(event);
    }

    /**
     * Replaces the current list of events with a new list.
     *
     * @param events the new events to display
     */
    public void setEvents(final List<FileEventInfo> events) {
        tableModel.set(events);
    }

    /**
     * Removes all events from the table.
     */
    public void clearEvents() {
        tableModel.clear();
    }

    /**
     * Inner class defining the table model (data layer for JTable).
     */
    private static final class EventTableModel extends AbstractTableModel {

        // Column headers for the table.
        private final String[] cols = {"Time", "Activity", "File", "Path"};

        // List holding all file event records.
        private final List<FileEventInfo> rows = new ArrayList<>();

        // Returns the number of rows (records) in the table.
        @Override
        public int getRowCount() {
            return rows.size();
        }

        // Returns the number of columns.
        @Override
        public int getColumnCount() {
            return cols.length;
        }

        // Returns the column name for display in the table header.
        @Override
        public String getColumnName(final int column) {
            return cols[column];
        }

        // Returns the value for each cell in the table based on row and column index.
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            // Retrieves the event record at row index 'rowIndex'.
            final FileEventInfo e = rows.get(rowIndex);

            // Returns the appropriate value depending on which column is requested.
            switch (columnIndex) {
                case 0:
                    // Timestamp when the event happened.
                    return e.getTimeStamp();
                case 1:
                    // Type of event (created, modified, deleted, moved).
                    return e.getFileActivity();
                case 2:
                    // Name of the file.
                    return e.getFileName();
                case 3:
                    // Path to the file.
                    return e.getFilePath();
                default:
                    return "";
            }
        }

        // Adds a single event record to the table and updates the view.
        void add(final FileEventInfo event) {
            final int at = rows.size();    // Index where the new row will appear.
            rows.add(event);               // Adds the new record.
            fireTableRowsInserted(at, at); // Notifies the table view that a new row was inserted.
        }

        // Replaces all current records with a new list and refreshes the view.
        void set(final List<FileEventInfo> list) {
            rows.clear();                  // Clears existing records.
            if (list != null && !list.isEmpty()) {
                rows.addAll(list);         // Adds all new records.
            }
            fireTableDataChanged();        // Notifies the view that the table data has changed.
        }

        // Clears all records from the table and refreshes the display.
        void clear() {
            final int n = rows.size();     // Gets the current row count.
            if (n == 0) {
                return;                    // If table is already empty, exit.
            }
            rows.clear();                  // Clears all event records.
            fireTableRowsDeleted(0, n - 1); // Notifies view that rows were deleted.
        }
    }
}
