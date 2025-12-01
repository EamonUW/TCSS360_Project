package view;

import model.ReportGenerator;

import javax.swing.*;
import java.awt.*;

/**
 * ReportPanel
 * -----------
  * @author Merra
 * @version Iteration 6
 *
 * Panel for generating and viewing file activity reports.
 * The user can choose how many top files to show (future use),
 * and the report is displayed in a scrollable text area.
 */
public class ReportPanel extends JPanel {

    /** Report generator used to build the text. */
    private final ReportGenerator myReportGenerator;

    /** Spinner for "Top N" choice (currently not enforced). */
    private final JSpinner myTopNSpinner;

    /** Button to trigger report creation. */
    private final JButton myBuildButton;

    /** Text area showing the report. */
    private final JTextArea myReportArea;

    /**
     * Creates the report panel.
     *
     * @param theReportGenerator report generator to use
     * @throws IllegalArgumentException if theReportGenerator is null
     */
    public ReportPanel(final ReportGenerator theReportGenerator) {
        if (theReportGenerator == null) {
            throw new IllegalArgumentException("theReportGenerator cannot be null");
        }
        myReportGenerator = theReportGenerator;

        // Layout and border
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Reports"));

        // ----- Top controls (spinner + button) -----
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        controls.add(new JLabel("Top files (not enforced yet):"));

        // Spinner: user can choose a "Top N" value (we can use this in future)
        myTopNSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        controls.add(myTopNSpinner);

        myBuildButton = new JButton("Build Report");
        controls.add(myBuildButton);

        add(controls, BorderLayout.NORTH);

        // ----- Center: text area for report -----
        myReportArea = new JTextArea(20, 80);
        myReportArea.setEditable(false);
        myReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        myReportArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(myReportArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button action
        myBuildButton.addActionListener(e -> buildReport());

        // Build initial report when panel is created
        buildReport();
    }

    /**
     * Asks ReportGenerator for a text report and shows it in the text area.
     * Currently uses the detailed report from ReportGenerator.
     * (Spinner value is read but not yet used for filtering.)
     */
    private void buildReport() {
        // Read spinner value (for future enhancement).
        final int theTopN = (Integer) myTopNSpinner.getValue();

        // For now we just ignore theTopN and show the full detailed report.
        final String theText = myReportGenerator.generateDetailedReport();

        myReportArea.setText(theText);
        myReportArea.setCaretPosition(0); // Scroll to the top.
    }
}
