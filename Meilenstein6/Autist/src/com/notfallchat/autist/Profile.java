package com.notfallchat.autist;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;  
import com.google.gson.reflect.TypeToken;

/**
 * Activity Profil contained Methods for writing and reading Files
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Profile extends Activity {
	
	private EditText vorName;
	private EditText nachName;
	private EditText alter;
	private EditText email;
	private EditText betrieb;
	private EditText jobCoachVorname;
	private EditText jobCoachNachname;
	

	/**
     * Method to display input fields
     * 
     * @param savedInstanceState save last Instance state
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		// send to db if ok
		// load Receiver for Jobcoach	
		vorName = (EditText) findViewById(R.id.vorname);
		nachName = (EditText) findViewById(R.id.nachname);
		alter = (EditText) findViewById(R.id.alter);
		email = (EditText) findViewById(R.id.email);
		betrieb = (EditText) findViewById(R.id.betrieb);
		jobCoachVorname = (EditText) findViewById(R.id.jobcoachvorname);
		jobCoachNachname =(EditText) findViewById(R.id.jobcoachnachname);
	}
	
	/**
     * Method to save new Profil
     * 
     * @param v defines Button widget
	 *
     */
	public void btnSaveProfile(View v) throws Exception {
		// From Tutorial http://www.java2blog.com/2013/11/gson-example-read-and-write-json.html
		User user = new User();
		user.setVorName(vorName.getText().toString());
		user.setNachName(nachName.getText().toString());
		user.setAlter(Integer.parseInt(alter.getText().toString()));
		user.setEmail(email.getText().toString());
		user.setBetrieb(betrieb.getText().toString());
		user.setJobcoachVorname(jobCoachVorname.getText().toString());
		user.setJobcoachNachname(jobCoachNachname.getText().toString());
		
		Gson gson = new Gson();  
   	    String json = gson.toJson(user);  
		    
   	    Profile.writeProfiletoFile(json, getApplicationContext(),"profile.json");
   		 
   		 //display file saved message
   		 Intent i = new Intent(this, ProfileDisplay.class);
    	 startActivity(i);
	}
	public void btnCancel(View v) throws Exception {
		Intent i = new Intent(this, MainActivity.class);
    	startActivity(i);
	}
	/**
     * Method to save String json in file
     * 
     * @param json json String
     * @param context current state of the object
	 * @param filename File to write json
     */
	public static void writeProfiletoFile(String json, Context context, String filename) {
		try {
	   		 FileOutputStream fileout=context.openFileOutput(filename, context.getApplicationContext().MODE_PRIVATE);
	   		 OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
	   		 outputWriter.write(json);
	   		 outputWriter.close();
	   		 } catch (Exception e) {
	   		 e.printStackTrace();
	   		 }
	}
	/**
     * Method get Situation List
     * 
     * @param context current state of the object
	 * @param filename read content
     */
	public static List<Situation> getSituationList(Context context, String filename) {
		List<Situation> inpList = null;
		Situation situation = null;
		try {
   			FileInputStream out = context.openFileInput(filename);
   	        BufferedReader inputReader = new BufferedReader(new InputStreamReader(out));
   	        String inputString;
   	        StringBuffer sb = new StringBuffer();                
   	        while ((inputString = inputReader.readLine()) != null) {
   	            sb.append(inputString + "\n");
   	        }
   	        
  
 		
 		//User[] list = new Gson().fromJson(response, User[].class);
 		
 		Type type = new TypeToken<List<Situation>>(){}.getType();
 	    inpList = new Gson().fromJson(sb.toString(), type);
// 	    User[] users = new User[inpList.size()];
// 	    for (int i=0;i<inpList.size();i++) {
// 	    	User usr = inpList.get(i);
// 	    	users[i] = usr;
// 	    }

		     out.close();
			 } catch (Exception e) {
	   		 e.printStackTrace();
	   		 }
		   	 return inpList;
	}
	
	/**
     * Method get the User Profile data
     * 
     * @param context current state of the object
	 * @param filename read content
     */
	public static User getUser(Context context, String filename) {
			
			User user = null;
			try {
	   			FileInputStream out = context.openFileInput(filename);
	   	        BufferedReader inputReader = new BufferedReader(new InputStreamReader(out));
	   	        String inputString;
	   	        StringBuffer sb = new StringBuffer();                
	   	        while ((inputString = inputReader.readLine()) != null) {
	   	            sb.append(inputString + "\n");
	   	        }
	   	        
			   	 Gson gson = new Gson();  
			   	 user = gson.fromJson(sb.toString(),User.class);
			     out.close();
				 } catch (Exception e) {
  	   		 e.printStackTrace();
  	   		 }
			   	 return user;
	}
	
	
	/**
     * Method to check if file is empty
     * 
     * @param context current state of the object
	 * @param filename file to check
     */
	public static boolean fileExistance(String fname, Context context){
	    File file = ((ContextWrapper) context).getBaseContext().getFileStreamPath(fname);
	    return file.exists();
	}
}
	