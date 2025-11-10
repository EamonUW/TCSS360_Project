package teame.fs.gui;

import teame.fs.ReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * ReportPanel
 * ---------------------
 * Small panel to generate and display a text report (totals, top files, sample events).
 * Uses ReportGenerator; does not write CSV (that belongs to the CSV module).
 *
 * Author: Merra Migora
 * Iteration: 3
 */
public class ReportPanel extends JPanel {

    private final ReportGenerator generator;
    private final JTextArea output = new JTextArea();
    private final JSpinner topNSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
    private final JSpinner fromYear = new JSpinner(new SpinnerNumberModel(2025, 1970, 2100, 1));
    private final JSpinner fromMonth = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
    private final JSpinner fromDay = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));

    private final JSpinner toYear = new JSpinner(new SpinnerNumberModel(2025, 1970, 2100, 1));
    private final JSpinner toMonth = new JSpinner(new SpinnerNumberModel(12, 1, 12, 1));
    private final JSpinner toDay = new JSpinner(new SpinnerNumberModel(31, 1, 31, 1));

    private final JCheckBox useRange = new JCheckBox("Use Date Range", false);
    private final JButton buildBtn = new JButton("Build Report");

    public ReportPanel(ReportGenerator generator) {
        this.generator = Objects.requireNonNull(generator, "generator");
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Report"));

        // Controls (top)
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        int col = 0;
        addLabeled(controls, c, 0, col++, new JLabel("Top N Files:"));
        addLabeled(controls, c, 0, col++, topNSpinner);
        addLabeled(controls, c, 0, col++, useRange);

        // From date
        addLabeled(controls, c, 1, 0, new JLabel("From (Y/M/D):"));
        addLabeled(controls, c, 1, 1, fromYear);
        addLabeled(controls, c, 1, 2, fromMonth);
        addLabeled(controls, c, 1, 3, fromDay);

        // To date
        addLabeled(controls, c, 2, 0, new JLabel("To (Y/M/D):"));
        addLabeled(controls, c, 2, 1, toYear);
        addLabeled(controls, c, 2, 2, toMonth);
        addLabeled(controls, c, 2, 3, toDay);

        // Button
        c.gridx = 0; c.gridy = 3; c.gridwidth = 4; c.fill = GridBagConstraints.NONE;
        controls.add(buildBtn, c);

        add(controls, BorderLayout.NORTH);

        // Output area (center)
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(output), BorderLayout.CENTER);

        // Action
        buildBtn.addActionListener(e -> buildReport());
    }

    private void addLabeled(JPanel p, GridBagConstraints c, int row, int col, JComponent comp) {
        GridBagConstraints cc = (GridBagConstraints) c.clone();
        cc.gridx = col;
        cc.gridy = row;
        p.add(comp, cc);
    }

    private void buildReport() {
        Integer topN = (Integer) topNSpinner.getValue();
        Instant from = null, to = null;

        if (useRange.isSelected()) {
            from = ldt((Integer) fromYear.getValue(), (Integer) fromMonth.getValue(), (Integer) fromDay.getValue())
                    .atZone(ZoneId.systemDefault()).toInstant();
            to   = ldt((Integer) toYear.getValue(), (Integer) toMonth.getValue(), (Integer) toDay.getValue())
                    .atZone(ZoneId.systemDefault()).toInstant();
        }

        String text = generator.buildTextReport(from, to, topN);
        output.setText(text);
        output.setCaretPosition(0);
    }

    private LocalDateTime ldt(int y, int m, int d) {
        // clamp simple ranges
        if (m < 1) m = 1; if (m > 12) m = 12;
        if (d < 1) d = 1; if (d > 31) d = 31;
        return LocalDateTime.of(y, m, d, 0, 0);
    }
}
