package model;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
/**
 * This class creates a message and converts it to a base64url encoded message.
 * The message is then sent to Gmail with necessary fields included.
 *
 * @author Eamon Challinor
 * @version 1.2
 */
public class EmailNotification {
    /**
     * The list of recipient emails.
     */
    private final String [] myTo;
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
     * The file attachment.
     */
    private final File myAttachment;
    /**
     * Public constructor for EmailNotification Superclass.
     *
     * @param theTo ArrayList of all specified recipient emails.
     * @param theFrom String for specified sender email.
     * @param theBodyText String for body text of email.
     * @param theSubject String for subject of email.
     * @param theUserID String for user ID.
     * @param theAttachment File for csv file attachment in email.
     */
    public EmailNotification(String [] theTo, String theFrom, String theBodyText, String theSubject, String theUserID, File theAttachment) {
        myTo = theTo;
        myFrom = theFrom;
        myBodyText = theBodyText;
        mySubject = theSubject;
        myUserID = theUserID;
        myAttachment = theAttachment;
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
            throws MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(myFrom));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(theTo));
        email.setSubject(mySubject);
        email.setText(myBodyText);

        if (myAttachment != null) {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(myBodyText, "utf-8");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(myAttachment);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(MimeUtility.encodeText(myAttachment.getName(), "utf-8", null));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            email.setContent(multipart);
        } else {
            email.setText(myBodyText, "utf-8");
        }
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
