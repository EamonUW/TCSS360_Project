package controller;

import model.CreateCSVFile;
import model.GmailAuthenticator;
import view.EventPanel;
import view.GmailPanel;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * This class acts as the controller for the GmailPanel and GmailAuthenticator classes.
 * It handles sending emails based on user inputs from the GUI to the model Gmail classes.
 *
 * @author Eamon
 * @version 5.2
 */
public class GmailController {

    private final GmailPanel myView;
    /**
     * Constructor for the GmailController.
     *
     * @param theView Instance of the GmailPanel.
     */
    public GmailController(GmailPanel theView) {
        myView = theView;
        myView.addSendActionListener(new SendActionListener());
        myView.addCheckActionListener();
    }

    /**
     * Creates an action listener for when the send button is pressed.
     * Checks if all fields entered are valid for an email and authenticates an email
     * to be sent based on the user specifications.
     */
    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String toRaw = myView.getToEmails().trim();
            String from = myView.getFromEmail().trim();
            String subject = myView.getSubject().trim();
            String body = myView.getBody().trim();
            Boolean select = myView.getSelect();

            if (toRaw.isEmpty() || from.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Please use valid sender and recipient emails.",
                        "Invalid fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] toArray = toRaw.split("\\s+");

            myView.setSendEnabled(false);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    File attachment = new File("Event Log");
                    try {
                        if (select){
                            String fileName = EventPanel.events.get(EventPanel.myIndex).getFileName();
                            String ext = EventPanel.events.get(EventPanel.myIndex).getFileExtension();
                            String filePath = EventPanel.events.get(EventPanel.myIndex).getFilePath();
                            String change = EventPanel.events.get(EventPanel.myIndex).getFileActivity();
                            String timeStamp = EventPanel.events.get(EventPanel.myIndex).getTimeStamp();
                            CreateCSVFile model =
                                    new CreateCSVFile(fileName, ext, filePath, change, timeStamp);
                            attachment = model.createEmailCSV(null);
                        }
                        GmailAuthenticator gmailAuth = new GmailAuthenticator(toArray, from, body, subject, "me", attachment);
                        gmailAuth.finalizeEmail();
                    } catch (IOException | MessagingException | GeneralSecurityException ex) {
                        throw new RuntimeException(ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    myView.setSendEnabled(true);
                    try {
                        get();
                        JOptionPane.showMessageDialog(null,
                                "Email sent successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        JOptionPane.showMessageDialog(null,
                                "Failed to send email: " + cause.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        cause.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
}
