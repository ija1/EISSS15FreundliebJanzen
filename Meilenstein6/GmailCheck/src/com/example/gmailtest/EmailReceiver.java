package com.example.gmailtest;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class EmailReceiver extends BroadcastReceiver {
	int count =0;

	public static String ACTION_ALARM = "com.alarammanager.alaram";
	 
	 @Override
	 public void onReceive(Context context, Intent intent) {
	   
	 System.out.println("test");
	  Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show();
	   
	 
	  
	 
	 Intent inService = new Intent(context,MyEmailAlarmService.class);
	 context.startService(inService);
	 

    }
}
    
    
