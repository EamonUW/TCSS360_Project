package model;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
/**
 * This class creates a message and converts it to a base64url encoded message.
 * The message is then sent to Gmail with necessary fields included.
 *
 * @author Eamon Challinor
 * @version 0.1
 */
public class EmailNotification {
    /**
     * The list of recipient emails.
     */
    private final ArrayList<String> myTo;
    /**
     * The sender email.
     */
    private final String myFrom;
    /**
     * The body text.
     */
    private final String myBodyText;
    /**
     * The subject text.
     */
    private final String mySubject;
    /**
     * The user ID.
     */
    private final String myUserID;
    /**
     * Public constructor for EmailNotification Superclass.
     *
     * @param theTo ArrayList of all specified recipient emails.
     * @param theFrom String for specified sender email.
     * @param theBodyText String for body text of email.
     * @param theSubject String for subject of email.
     * @param theUserID String for user ID.
     */
    public EmailNotification(ArrayList<String> theTo, String theFrom, String theBodyText, String theSubject, String theUserID) {
        myTo = theTo;
        myFrom = theFrom;
        myBodyText = theBodyText;
        mySubject = theSubject;
        myUserID = theUserID;
    }
    /**
     * Sends an automatic email to Gmail based on specified fields.
     *
     * @param service Enables message to be sent to Gmail.
     */
    public void sendMessage(Gmail service)
            throws MessagingException, IOException {
        for (String s : myTo) {
            MimeMessage email = createEmail(s);
            Message message = createMessageWithEmail(email);
            message = service.users().messages().send(myUserID, message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        }
    }
    /**
     * Creates a mime message containing the information for composing an email.
     *
     * @param theTo The individual email currently selected from the list
     */
    private MimeMessage createEmail(String theTo)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(myFrom));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(theTo));
        email.setSubject(mySubject);
        email.setText(myBodyText);
        return email;
    }
    /**
     * Converts the mime message to a base64url encoded message.
     * Message is then sendable in the form of an email through Gmail.
     *
     * @param email Mime message to be encoded.
     */
    private Message createMessageWithEmail(MimeMessage email)
            throws IOException, MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
