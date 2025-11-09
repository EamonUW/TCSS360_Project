package view;

import javax.swing.*;
import java.awt.*;

public class GmailPanel  extends JPanel {
    public GmailPanel() {
        super();
        buildUI();
    }
    public void buildUI() {
        final JFrame gmailFrame = new JFrame("Send Email");
        gmailFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gmailFrame.setFocusable(true);
        gmailFrame.setResizable(false);
        gmailFrame.setSize(600, 400);
        gmailFrame.setLocationRelativeTo(null);
        gmailFrame.add(createAndShowGUI());
        gmailFrame.setVisible(true);
        gmailFrame.pack();
    }

    public JPanel createAndShowGUI() {
        JPanel screenPanel = new JPanel(new BorderLayout());
        JPanel textPanel = textPanel();
        JLabel emailLabel = new JLabel("Create An Email");
        JButton sendButton = new JButton("Send");
        sendButton.setHorizontalAlignment(JButton.CENTER);
        textPanel.add(sendButton);
        emailLabel.setHorizontalAlignment(JLabel.CENTER);
        screenPanel.add(emailLabel, BorderLayout.NORTH);
        screenPanel.add(textPanel, BorderLayout.CENTER);
        screenPanel.add(sendButton, BorderLayout.SOUTH);
        return screenPanel;
    }

    public JPanel textPanel() {
        String[] labels = {"To:          ", "From:     ", "Subject:"};
        String[] placeholders = {"Please Enter All Recipient Emails Separated By Space", "Please Enter Sender Email", "Please Enter Subject"};

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel textLabel = new JLabel(labels[i]);
            JTextField textField = new JTextField(placeholders[i], 40);
            textField.setForeground(Color.GRAY);

            String placeholder = placeholders[i];
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

            row.add(textLabel);
            row.add(textField);
            textPanel.add(row);
        }

        JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel body = new JLabel("Body:     ");
        JTextArea bodyField = new JTextArea(8, 40);
        bodyPanel.add(body);
        bodyPanel.add(new JScrollPane(bodyField));

        textPanel.add(bodyPanel);

        return textPanel;
    }
}
