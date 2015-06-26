package com.example.gmailtest;

import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
 
public class MyEmailAlarmService extends IntentService {

	
	public MyEmailAlarmService() {
		super("MyEmailAlarmService");
		// TODO Auto-generated constructor stub
	}

	protected void onHandleIntent(Intent arg0) {
		
		Log.i("TaskService", "Service running");
		mail();
	}
	
	private void mail() {
		
		/*
		 * TODO:
		 * Algorithm to find right mails
		 * search body for String Meeting, startdate, enddate
		 * insert into calendar
		 * 
		 */
		// Mit Hilfe der API JavaMail werden im folgenden Codeabschnitt erst einmal alle Emails ausgelesen,
		// dieser Codeabschnitt soll später dahingehend erweitert werden, das nach neuen Mails gesucht wird und
		// die Mails nach Terminvereinbarungen durchsucht werden. diese Termine sollen dann in den calender eingetragen werden.
    	Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imaps");
            
            /*
             * TODO: Make Config File
             */
            
            store.connect("imap.gmail.com", "mailadresse", "passwort");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] msg = inbox.getMessages();
//            Address[] in = msg.getFrom();
//            for (Address address : in) {
//                System.out.println("FROM:" + address.toString());
//            }
//            Multipart mp = (Multipart) msg.getContent();
//            BodyPart bp = mp.getBodyPart(0);
//            System.out.println("SENT DATE:" + msg.getSentDate());
//            System.out.println("SUBJECT:" + msg.getSubject());
//            System.out.println("CONTENT:" + bp.getContent());
            
            for (int i = 0, n = msg.length; i < n; i++) {
                Message message = msg[i];
                Log.i("Email Number ", Integer.toString(i + 1));
                Log.i("Subject: ",message.getSubject());
                Log.i("From: ",message.getFrom()[0].toString());
                Log.i("Text: ",message.getContent().toString());
                Log.i("-", "-------------------------------------------");
             }
 
        } catch (Exception mex) {
            mex.printStackTrace();
        }
	}
}