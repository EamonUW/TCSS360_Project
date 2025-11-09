package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of directories to watch (add/remove/clear).
 *
 * @author Mihretu Gebre
 * @version Iteration 3
 */
public class PathPanel extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public PathPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Watch Paths"));

        list.setVisibleRowCount(8);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Folder…");
        JButton remBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBtn);
        buttons.add(remBtn);
        buttons.add(clearBtn);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> chooseAndAdd());
        remBtn.addActionListener(e -> list.getSelectedValuesList().forEach(model::removeElement));
        clearBtn.addActionListener(e -> model.clear());
    }

    private void chooseAndAdd() {
        JFileChooser ch = new JFileChooser();
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            if (f != null) {
                String p = f.getAbsolutePath();
                if (!contains(p)) model.addElement(p);
            }
        }
    }

    private boolean contains(String p) {
        for (int i = 0; i < model.size(); i++) if (model.get(i).equals(p)) return true;
        return false;
    }

    public List<Path> getPaths() {
        List<Path> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) out.add(Path.of(model.get(i)));
        return out;
    }
}

