package view;

// Imports Swing components for creating GUI elements
import javax.swing.*;
// Imports AWT classes for layout management and UI spacing
import java.awt.*;
// Imports File class for handling directories
import java.io.File;
// Imports Path for returning Java NIO paths
import java.nio.file.Path;
// Imports List and ArrayList for storing multiple paths
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of directories to watch (add/remove/clear).
 * This panel allows users to add folders to monitor, remove them, or clear the entire list.
 *
 * @author Mihretu Gebre
 * @version Iteration 5
 */
public class PathPanel extends JPanel {

    // Stores the list of directory paths (as strings) in a dynamic model for JList.
    private final DefaultListModel<String> model = new DefaultListModel<>();

    // Displays the list of paths visually in the GUI.
    private final JList<String> list = new JList<>(model);

    // Constructor that sets up the panel layout and user interface.
    public PathPanel() {
        // Calls the superclass constructor, setting a BorderLayout with 8px horizontal and vertical gaps.
        super(new BorderLayout(8, 8));

        // Adds a titled border labeled “Watch Paths” around the panel.
        setBorder(BorderFactory.createTitledBorder("Watch Paths"));

        // Sets the number of visible rows in the JList (scrolling will appear beyond 8 items).
        list.setVisibleRowCount(8);

        // Adds the list inside a scroll pane to the center area of the layout.
        add(new JScrollPane(list), BorderLayout.CENTER);

        // Creates a button for adding new folders to the list.
        JButton addBtn = new JButton("Add Folder…");

        // Creates a button for removing the selected folders from the list.
        JButton remBtn = new JButton("Remove Selected");

        // Creates a button for clearing all entries from the list.
        JButton clearBtn = new JButton("Clear");

        // Creates a sub-panel for holding the buttons horizontally aligned to the left.
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Adds the “Add Folder…” button to the button panel.
        buttons.add(addBtn);

        // Adds the “Remove Selected” button to the button panel.
        buttons.add(remBtn);

        // Adds the “Clear” button to the button panel.
        buttons.add(clearBtn);

        // Adds the button panel to the bottom (SOUTH) region of the main layout.
        add(buttons, BorderLayout.SOUTH);

        // Assigns an event listener to the Add button that triggers folder selection when clicked.
        addBtn.addActionListener(e -> chooseAndAdd());

        // Assigns an event listener to the Remove button that deletes all selected list items.
        remBtn.addActionListener(e -> list.getSelectedValuesList().forEach(model::removeElement));

        // Assigns an event listener to the Clear button that removes all items from the list.
        clearBtn.addActionListener(e -> model.clear());
    }

    // Opens a folder chooser dialog and adds the selected directory path to the list.
    private void chooseAndAdd() {
        // Creates a file chooser dialog.
        JFileChooser ch = new JFileChooser();

        // Restricts the chooser to select only directories (no files).
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Opens the dialog and checks if the user approved the selection.
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            // Gets the selected folder.
            File f = ch.getSelectedFile();

            // Ensures the selected file object is not null.
            if (f != null) {

                // Retrieves the absolute path of the selected folder.
                String p = f.getAbsolutePath();

                // Adds the path to the model only if it doesn’t already exist.
                if (!contains(p)) model.addElement(p);
            }
        }
    }

    // Checks if the given path already exists in the list model.
    private boolean contains(String p) {
        // Loops through all elements in the model.
        for (int i = 0; i < model.size(); i++)
            // Returns true if a matching path is found.
            if (model.get(i).equals(p)) return true;

        // Returns false if no matching path exists.
        return false;
    }

    // Returns a list of Path objects representing all directories currently in the model.
    public List<Path> getPaths() {
        // Creates a new list to hold Path objects.
        List<Path> out = new ArrayList<>();

        // Loops through all entries in the model and converts each to a Path object.
        for (int i = 0; i < model.size(); i++)
            out.add(Path.of(model.get(i)));

        // Returns the list of paths.
        return out;
    }
}

