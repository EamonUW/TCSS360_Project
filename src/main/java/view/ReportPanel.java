package teame.fs.gui;

import teame.fs.ReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

/**
 * ReportPanel
 * -----------
 * Panel for generating and viewing file activity reports.
 * The user can choose how many top files to show, and the report
 * is displayed in a scrollable text area.
 */
public class ReportPanel extends JPanel {

    /** Generator that builds the report text from the log. */
    private final ReportGenerator myReportGenerator;

    /** UI controls. */
    private final JSpinner myTopNSpinner;
    private final JTextArea myReportArea;
    private final JButton myBuildButton;

    /**
     * Basic constructor.
     *
     * @param theReportGenerator used to build report text
     */
    public ReportPanel(final ReportGenerator theReportGenerator) {
        if (theReportGenerator == null) {
            throw new IllegalArgumentException("theReportGenerator cannot be null");
        }
        myReportGenerator = theReportGenerator;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Reports"));

        // Top controls: "Top N" and button
        final JPanel theControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        theControls.add(new JLabel("Top files by changes:"));

        myTopNSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        theControls.add(myTopNSpinner);

        myBuildButton = new JButton("Build Report");
        theControls.add(myBuildButton);

        add(theControls, BorderLayout.NORTH);

        // Center area: text report
        myReportArea = new JTextArea(20, 80);
        myReportArea.setEditable(false);
        myReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        myReportArea.setLineWrap(false);

        final JScrollPane theScroll = new JScrollPane(myReportArea);
        add(theScroll, BorderLayout.CENTER);

        // Wire up button action
        myBuildButton.addActionListener(e -> buildReport());

        // Build an initial report on load
        buildReport();
    }

    /**
     * Asks ReportGenerator for a text report and shows it in the text area.
     * Uses the current spinner value for "top N" and includes all time.
     */
    private void buildReport() {
        final int theTopN = (Integer) myTopNSpinner.getValue();

        // Null start/end means "no time filter" here.
        final Instant theStart = null;
        final Instant theEnd = null;

        final String theText = myReportGenerator.buildTextReport(theStart, theEnd, theTopN);
        myReportArea.setText(theText);
        myReportArea.setCaretPosition(0); // Scroll to the top.
    }
}
