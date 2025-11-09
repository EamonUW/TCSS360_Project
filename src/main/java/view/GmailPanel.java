package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * This class creates a popup UI for creating a gmail based on a selected file event.
 *
 * @author Eamon
 * @version Iteration 3, 0.1
 */
public class GmailPanel extends JPanel {
    /**
     * The sender email from the user.
     */
    private String myFromEmail = "";
    /**
     * The recipient emails from the user.
     */
    private String myToEmails = "";
    /**
     * The subject text from the user.
     */
    private String mySubject = "";
    /**
     * The body text from the user.
     */
    private String myBody = "";
    /**
     * Array of text fields.
     */
    private final JTextField [] myTextFields = new JTextField[4];
    /**
     * Constructor for GmailPanel.
     */
    public GmailPanel() {
        super();
        buildUI();
    }
    /**
     * Build UI for Gmail frame.
     */
    public void buildUI() {
        final JFrame gmailFrame = new JFrame("Send Email");
        gmailFrame.setFocusable(true);
        gmailFrame.setResizable(false);
        gmailFrame.setSize(600, 400);
        gmailFrame.setLocationRelativeTo(null);
        gmailFrame.add(createAndShowGUI());
        gmailFrame.setVisible(true);
        gmailFrame.pack();
    }
    /**
     * Shows panels for Gmail frame.
     *
     * @return JPanel main screen panel for frame.
     */
    public JPanel createAndShowGUI() {
        JPanel screenPanel = new JPanel(new BorderLayout());
        JPanel textPanel = textPanel();
        JLabel emailLabel = new JLabel("Create An Email");
        JButton sendButton = new JButton("Send");
        sendButton.setHorizontalAlignment(JButton.CENTER);
        textPanel.add(sendButton);

        sendButton.addActionListener(e -> {
            System.out.println("Button clicked using lambda!");

        });

        emailLabel.setHorizontalAlignment(JLabel.CENTER);
        screenPanel.add(emailLabel, BorderLayout.NORTH);
        screenPanel.add(textPanel, BorderLayout.CENTER);
        screenPanel.add(sendButton, BorderLayout.SOUTH);

        return screenPanel;

    }
    /**
     * Create panels for display panel.
     *
     * @return JPanel main screen panel for main panel.
     */
    public JPanel textPanel() {
        String[] labels = {"To:          ",
                "From:     ",
                "Subject:"};
        String[] placeholders = {"Please Enter All Recipient Emails Separated By Space",
                "Please Enter Sender Email",
                "Please Enter Subject"};

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel textLabel = new JLabel(labels[i]);
            JTextField textField = new JTextField(placeholders[i], 40);
            textField.setForeground(Color.GRAY);
            String placeholder = placeholders[i];
            myTextFields[i] = textField;
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                        textField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (textField.getText().isEmpty()) {
                        textField.setForeground(Color.GRAY);
                        textField.setText(placeholder);
                    }
                }
            });
            myTextFields[i].getDocument().addDocumentListener(new MyDocumentListener(myTextFields[i],null, i));
            row.add(textLabel);
            row.add(textField);
            textPanel.add(row);
        }

        JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel body = new JLabel("Body:     ");
        JTextArea bodyField = new JTextArea(8, 40);
        bodyPanel.add(body);
        bodyField.getDocument().addDocumentListener(new MyDocumentListener(null,bodyField, 3));
        bodyPanel.add(new JScrollPane(bodyField));

        textPanel.add(bodyPanel);

        return textPanel;
    }
    /**
     * Inner class for handling events for text inputs to text fields.
     */
    private class MyDocumentListener implements DocumentListener {
        /**
         * Text field with modified text.
         */
        private final JTextField myTextField;
        /**
         * Text area for body text.
         */
        private final JTextArea myTextArea;
        /**
         * Type for which field change.
         */
        private final int myType;

        /**
         * Constructor for MyDocumentListener.
         */
        public MyDocumentListener(JTextField theTextField, JTextArea theTextArea, int theType) {
            myTextField = theTextField;
            myTextArea = theTextArea;
            myType = theType;
        }
        //Console logs for testing purposes.
        @Override
        public void insertUpdate(DocumentEvent e) {
            check();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check();
        }
        /**
         * Private helper method for checking which text field has been modified.
         */
        private void check() {
            if (myType == 0) {
                myToEmails = myTextField.getText();
                //System.out.println(myToEmails);
                //System.out.println("Text inserted: " + textField.getText());
            }
            if (myType == 1) {
                myFromEmail = myTextField.getText();
                //System.out.println(myFromEmail);
                //System.out.println("Text inserted: " + textField.getText());
            }
            if (myType == 2) {
                mySubject = myTextField.getText();
                //System.out.println(mySubject);
                //System.out.println("Text inserted: " + textField.getText());
            }
            if (myType == 3) {
                myBody = myTextArea.getText();
                //System.out.println(myBody);
                //System.out.println("Text inserted: " + textArea.getText());
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }
}
