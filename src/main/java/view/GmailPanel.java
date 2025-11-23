package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class creates a popup UI for creating a gmail based on a selected file event.
 *
 * @author Eamon
 * @version 3.2
 */
public class GmailPanel extends JPanel {

    private final JFrame myMainFrame;

    private String myFromEmail = "";
    private String myToEmails = "";
    private String mySubject = "";
    private String myBody = "";

    private final JTextField[] myTextFields = new JTextField[4];
    private JButton mySendButton; // moved to field so controller can enable/disable

    /**
     * Constructor for GmailPanel.
     */
    public GmailPanel(JFrame theMainFrame) {
        super();
        myMainFrame = theMainFrame;
        myMainFrame.setEnabled(false);
        buildUI();
    }

    /**
     * Build UI for Gmail frame.
     */
    public void buildUI() {
        final JFrame gmailFrame = new JFrame("Send Email");
        gmailFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                myMainFrame.setEnabled(true);
            }
        });
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
        mySendButton = new JButton("Send");
        mySendButton.setHorizontalAlignment(JButton.CENTER);
        textPanel.add(mySendButton);

        // Remove internal action listener — controller will register it instead.

        emailLabel.setHorizontalAlignment(JLabel.CENTER);
        screenPanel.add(emailLabel, BorderLayout.NORTH);
        screenPanel.add(textPanel, BorderLayout.CENTER);
        screenPanel.add(mySendButton, BorderLayout.SOUTH);

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
            myTextFields[i].getDocument().addDocumentListener(new MyDocumentListener(myTextFields[i], null, i));
            row.add(textLabel);
            row.add(textField);
            textPanel.add(row);
        }

        JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel body = new JLabel("Body:     ");
        JTextArea bodyField = new JTextArea(8, 40);
        bodyPanel.add(body);
        bodyField.getDocument().addDocumentListener(new MyDocumentListener(null, bodyField, 3));
        bodyPanel.add(new JScrollPane(bodyField));

        textPanel.add(bodyPanel);

        return textPanel;
    }

    /**
     * Inner class for handling events for text inputs to text fields.
     */
    private class MyDocumentListener implements DocumentListener {
        private final JTextField myTextField;
        private final JTextArea myTextArea;
        private final int myType;

        public MyDocumentListener(JTextField theTextField, JTextArea theTextArea, int theType) {
            myTextField = theTextField;
            myTextArea = theTextArea;
            myType = theType;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            check();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check();
        }

        private void check() {
            if (myType == 0) {
                myToEmails = myTextField.getText();
            }
            if (myType == 1) {
                myFromEmail = myTextField.getText();
            }
            if (myType == 2) {
                mySubject = myTextField.getText();
            }
            if (myType == 3) {
                myBody = myTextArea.getText();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    /**
     * Register a listener for the Send button.
     */
    public void addSendActionListener(ActionListener listener) {
        if (mySendButton != null) {
            mySendButton.addActionListener(listener);
        }
    }
    /**
     * Getter for recipient emails.
     *
     * @return myToEmails all recipient emails.
     */
    public String getToEmails() {
        return myToEmails;
    }
    /**
     * Getter for sender email.
     *
     * @return myFromEmail sender email.
     */
    public String getFromEmail() {
        return myFromEmail;
    }
    /**
     * Getter for subject.
     *
     * @return mySubject subject header.
     */
    public String getSubject() {
        return mySubject;
    }
    /**
     * Getter for body.
     *
     * @return myBody body text.
     */
    public String getBody() {
        return myBody;
    }

    /**
     * Option to enable and disable send to prevent unintended user interactions.
     *
     * @param enabled Check for if send button is enabled or disabled.
     */
    public void setSendEnabled(boolean enabled) {
        if (mySendButton != null) {
            mySendButton.setEnabled(enabled);
        }
    }
}