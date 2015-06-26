package com.example.jobcoach;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;  

/**
 * Activity for save new autistics who supervise in userlist.json
 * this class is manly for the user selection in Terminvergabe.java
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Betreuung extends Activity {
	
	private EditText newVorName;
	private EditText newNachName;
	private EditText newAlter;
	private EditText newEmail;
	private EditText newBetrieb;

	/**
     * Method that show autist list
     * 
     * @param savedInstanceState save last Instance state
	 *
     */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.betreuung);
		
		
		if (Profile.fileExistance("userlist.json", getApplicationContext())) {
			List<User> userlist = Profile.getUserList(getApplicationContext(), "userlist.json");
			if (!userlist.isEmpty()) {
					LinearLayout l = (LinearLayout) findViewById(R.id.userlist);
					
					for (User user : userlist ) {
						RelativeLayout r1 = new RelativeLayout(getApplicationContext());
						TextView t1 = new TextView(getApplicationContext());
						RelativeLayout.LayoutParams params1= new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params1.setMargins(40, 0, 0, 0);
						t1.setLayoutParams(params1);
						t1.setText(user.getVorName());
						
						TextView t2 = new TextView(getApplicationContext());
						RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params2.setMargins(120, 0, 0, 0);
						t1.setLayoutParams(params2);
						t2.setText(user.getNachName());
						
						TextView t3 = new TextView(getApplicationContext());
						RelativeLayout.LayoutParams params3= new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params3.setMargins(160, 0, 0, 0);
						t3.setLayoutParams(params1);
						t3.setText(user.getBetrieb());
						r1.addView(t1);
						r1.addView(t2);
						r1.addView(t3);
						l.addView(r1);
					}
				}
		}
		//TODO:
		// send to db if ok
		// load Receiver for Jobcoach	
		newVorName = (EditText) findViewById(R.id.newvorname);
		newNachName = (EditText) findViewById(R.id.newnachname);
		//newAlter = (EditText) findViewById(R.id.n);
		newEmail = (EditText) findViewById(R.id.newemail);
		newBetrieb = (EditText) findViewById(R.id.newbetrieb);
	}
	
	/**
     * Method that add new autist to list if button is pressed
     * 
     * @param v defines Button widget
	 *
     */
	public void btnSaveUserList(View v) throws Exception {
		User user = new User();
		user.setVorName(newVorName.getText().toString());
		user.setNachName(newNachName.getText().toString());
		user.setEmail(newEmail.getText().toString());
		user.setBetrieb(newBetrieb.getText().toString());
		user.setJobcoachVorname(newVorName.getText().toString());
		user.setJobcoachNachname(newNachName.getText().toString());
		
		List<User> userlist = null;
		if (Profile.fileExistance("userlist.json", getApplicationContext())) {
			userlist = Profile.getUserList(getApplicationContext(), "userlist.json");
		} else {
			userlist = new ArrayList<User>();
		}
			userlist.add(user);
			Gson gson = new Gson();  
	   	    String json = gson.toJson(userlist);  
	   	    
	   	    Profile.writeProfiletoFile(json,getApplicationContext(),"userlist.json");

   	     //display file saved data
  		 Intent i = new Intent(this, Betreuung.class);
      	 startActivity(i);
	}
	
	
	public void btnCancel(View v) throws Exception {
	   Intent i = new Intent(this, JobCoach.class);
	   startActivity(i);
	}
		
	
}
	