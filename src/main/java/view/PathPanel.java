package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * PathPanel
 * ----------
 * Left-hand panel that manages the list of folders to watch.
 * - "Add Folder…" opens a directory chooser and adds the path
 * - "Remove Selected" removes the highlighted path
 * - "Clear" removes all paths
 *
 * It supports a path listener so the main UI can react whenever the
 * set of watched paths changes.
 */
public class PathPanel extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    /** Listener that gets called whenever the path list changes. */
    private Consumer<List<Path>> pathListener;

    public PathPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Watch Paths"));

        list.setVisibleRowCount(8);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton addBtn   = new JButton("Add Folder…");
        JButton remBtn   = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBtn);
        buttons.add(remBtn);
        buttons.add(clearBtn);

        add(buttons, BorderLayout.SOUTH);

        // Add folder
        addBtn.addActionListener(e -> chooseAndAdd());

        // Remove selected
        remBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) {
                model.remove(idx);
                notifyListener();
            }
        });

        // Clear all
        clearBtn.addActionListener(e -> {
            if (!model.isEmpty()) {
                model.clear();
                notifyListener();
            }
        });
    }

    /**
     * Opens a directory chooser and adds the selected folder to the list.
     */
    private void chooseAndAdd() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            if (folder != null) {
                String path = folder.getAbsolutePath();
                // avoid duplicates
                if (!containsPath(path)) {
                    model.addElement(path);
                    notifyListener();
                }
            }
        }
    }

    private boolean containsPath(String path) {
        for (int i = 0; i < model.size(); i++) {
            if (model.getElementAt(i).equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all watched paths as java.nio.file.Path objects.
     */
    public List<Path> getPaths() {
        List<Path> result = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            result.add(Paths.get(model.getElementAt(i)));
        }
        return result;
    }

    /**
     * Register a listener that will be called whenever the list of
     * watched paths changes.
     */
    public void addPathListener(Consumer<List<Path>> listener) {
        this.pathListener = listener;
    }

    /**
     * Notify the listener (if present) with the current path list.
     */
    private void notifyListener() {
        if (pathListener != null) {
            pathListener.accept(getPaths());
        }
    }
}
