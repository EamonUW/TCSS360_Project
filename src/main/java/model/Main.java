package model;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws MessagingException, GeneralSecurityException, IOException {
        ArrayList <String> list = new ArrayList<>();
        list.add("eamonxl@uw.edu");
        GmailAuthenticator gmail = new GmailAuthenticator(list, "eamonxl@uw.edu", "Ur Mom", "Ur Mom", "me");
        gmail.finalizeEmail();
    }
}
