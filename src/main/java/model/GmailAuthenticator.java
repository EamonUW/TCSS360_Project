package model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import javax.mail.MessagingException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * This class uses the Gmail API to authenticate the application for first time users.
 * An automatic email is sent for authenticated users based on predefined parameters.
 *
 * @author Eamon Challinor
 * @version 1.2
 */
public class GmailAuthenticator extends EmailNotification {
    /**
     * The application name for File System Tracker.
     */
    private static final String APPLICATION_NAME = "TCSS 360 File System Tracker";
    /**
     * Factory loader for json credentials.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to token file with permissions created upon credential authentication.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Scope of permissions for Gmail features.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    /**
     * Path to json credentials.
     */
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * The user ID.
     */
    private final String myUserID;

    /**
     * Public constructor for GmailAuthenticator subclass.
     *
     * @param theTo ArrayList of all specified recipient emails.
     * @param theFrom String for specified sender email.
     * @param theBodyText String for body text of email.
     * @param theSubject String for subject of email.
     * @param theUserID String for user ID.
     * @param theAttachment File for attachment.
     */
    public GmailAuthenticator(String [] theTo, String theFrom, String theBodyText, String theSubject, String theUserID, File theAttachment) {
        super(theTo, theFrom, theBodyText, theSubject, theUserID, theAttachment);
        myUserID = theUserID;
    }
    /**
     * Checks contents of resource file for credentials.json with content root path as fallback.
     * Authenticates first time users and asks for permissions for application to use
     *
     * @param theHTTPTransport Creates a low-level http request to use for Gmail.
     * @return Credential Returns complete credentials from credentials.json.
     */
    private Credential getCredentials(final NetHttpTransport theHTTPTransport) throws IOException {
        InputStream in = GmailAuthenticator.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        try {
            in = GmailAuthenticator.class.getResourceAsStream("/credentials.json");
            if (in == null) {
                in = new FileInputStream("src/main/resources/credentials.json");
                System.out.println("Root path fallback.");
            } else {
                System.out.println("Class path successful.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                theHTTPTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(myUserID);
    }

    /**
     * Uses gmail service to send a completed version of the email message to Gmail.
     * All recipients will receive an automatic email based on specified parameters.
     */
    public void finalizeEmail() throws IOException, GeneralSecurityException, MessagingException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);

        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        sendMessage(service);
    }
}