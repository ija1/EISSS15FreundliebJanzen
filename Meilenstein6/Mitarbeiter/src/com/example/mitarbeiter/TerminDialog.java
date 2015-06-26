package com.example.mitarbeiter;

import java.io.IOException;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Activity to confirm the termin
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class TerminDialog extends Dialog{
	
	private Termin termin;

	public TerminDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onStop() {
		// if a button is pressed in TerminDialog, send decision to jobcoach
//		termin = MainActivity.getTermin();
//		termin.setUserName(termin.getUserName());
//		String message = new Gson().toJson(termin);
//		new send().execute(message);
		super.onStop();
	}
}
