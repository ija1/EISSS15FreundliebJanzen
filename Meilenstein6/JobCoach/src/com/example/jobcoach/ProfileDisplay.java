package com.example.jobcoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Activity to display Profile 
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class ProfileDisplay extends Activity {

		/**
	     * Method display Profile
	     * 
	     * @param savedInstanceState save last Instance state
		 *
	     */
   		@Override
   		public void onCreate(Bundle savedInstanceState) {
   			super.onCreate(savedInstanceState);
   			setContentView(R.layout.profiledisplay);
   			
   			final TextView vorName = (TextView) findViewById(R.id.vorname);
   			final TextView nachName = (TextView) findViewById(R.id.nachname);
   			final TextView alter = (TextView) findViewById(R.id.alter);
   			final TextView email = (TextView) findViewById(R.id.email);
   			final TextView betrieb = (TextView) findViewById(R.id.betrieb);
   			
   			User user = Profile.getUser(getApplicationContext(),"profile.json");
   		 
		   	vorName.append(user.getVorName());
		   	nachName.append(user.getNachName());
   			alter.append(new Integer(user.getAlter()).toString());
   			email.append(user.getEmail());
   			betrieb.append(user.getBetrieb());
   	   	}

   		public void btnToMainMenu(View v) throws Exception {
   			Intent i = new Intent(this, JobCoach.class);
   	    	startActivity(i);
   		}
}
   			
