package com.example.mitarbeiter;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.example.mitarbeiter.MessageConsumer.OnReceiveMessageHandler;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity to display main menu and to activate Consumer for Rating Queue
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class MainActivity extends Activity {
	
	private MessageConsumer ConsumerRating;	
	private MessageConsumer ConsumerOverlapTermin;
	private List<User> Exampleuserlist;
	private static Button receiveRatingButton;
	private static Button userprofile;
	private Situation situation = null;
	private String json;
	private Button receivetermin;
	public static Button showDialogButton;
	
	/**
     * Method to display main menu
     * 
     * @param savedInstanceState save last Instance state
	 *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);  
        
        receivetermin = (Button) findViewById(R.id.receivetermin);
        receivetermin.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        
        receiveRatingButton = (Button)findViewById(R.id.receiveRating);
        receiveRatingButton.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        userprofile = (Button) findViewById(R.id.userprofile);
        userprofile.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        // TODO:
        // if the corporate network is detected, a list of all autistic employees should be send
        Exampleuserlist = new ArrayList<User>();
        User autist1 = new User();
        User autist2 = new User();
        Exampleuserlist.add(autist1);
        Exampleuserlist.add(autist2);

        consumRatingRequests(ConfigRabbitMQ.EXCHANGE_RATING);
        consumOverlapTerminfromAutist(ConfigRabbitMQ.EXCHANGE_OVERLAP);
    }
    
    public void consumOverlapTerminfromAutist(String exchange) {
    	if (Profile.fileExistance("profile.json", getApplicationContext())) {
		User employee = Profile.getUser(getApplicationContext(),"profile.json");
		// Create the consumer
		String[] rotk = {"overlap"+employee.getVorName()+employee.getNachName()};
		ConsumerOverlapTermin = new MessageConsumer(ConfigRabbitMQ.IP, exchange, ConfigRabbitMQ.EXCHANGE_TYPE,rotk);
		new consumerOverlapTerminconnect().execute();
		// register for messages
		ConsumerOverlapTermin.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {

			public void onReceiveMessage(byte[] message) {
				
				String json=null;
				try {
					json = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				final Termin termin = new Gson().fromJson(json, Termin.class);

				showDialogButton = (Button)findViewById(R.id.receivetermin);
				changeShowDialogButton();
				
				showDialogButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
							// TODO: Ask for saving
							final TerminDialog dialog = new TerminDialog(MainActivity.this);
							dialog.setContentView(R.layout.termindialog);
							
							// set the custom dialog components - text, image and button
							TextView start = (TextView) dialog.findViewById(R.id.startDate);
							start.setText(getTime(termin.getStartDatumOverlap()).toString());
							
							TextView end = (TextView) dialog.findViewById(R.id.endDate);
							end.setText(getTime(termin.getEndDatumOverlap()).toString());
							
							TextView email = (TextView) dialog.findViewById(R.id.email);
							email.setText(termin.getAutist().getEmail());
							
							Button dialogCheckButton = (Button) dialog.findViewById(R.id.checkButton);
							// if button is clicked, close the custom dialog
							dialogCheckButton.setOnClickListener(new OnClickListener(){
								public void onClick(View v) {
									defaultShowDialogButton();
									dialog.cancel();
								}
							});
							
							dialog.show();
				}
				});
	}
		});
    	} else {
    		Toast.makeText(getApplicationContext(), "Bitte erstellen Sie zuerst Ihr Profil!", Toast.LENGTH_LONG).show();
    	}
    }
    
    /**
     * For chnaging Color if Messages receive
     */
    public static void changeShowDialogButton(){
    	showDialogButton.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
    	showDialogButton.invalidate();
    }
    
    /**
     * Setting back to default Color
     */
    public static void defaultShowDialogButton(){
    	showDialogButton.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);	
    	showDialogButton.invalidate();
    }

    

    /*
     * For chnaging Color if Messages receive
     */
    public static void changeRatingButton(){
    	receiveRatingButton.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
    	receiveRatingButton.invalidate();
    }
    
    /*
     * Setting back to default Color
     */
    public static void defaultRatingButton(){
    	receiveRatingButton.getBackground().setColorFilter(0xcccddddd,Mode.MULTIPLY);
    	receiveRatingButton.invalidate();
    }

    public void consumRatingRequests(String exchange) {
    	
    	if (Profile.fileExistance("profile.json", getApplicationContext())) {
    		
		User employee = Profile.getUser(getApplicationContext(),"profile.json");
		if (employee != null) {
		// Create the consumer
		String[] rotk = {"ratingemployee"+employee.getVorName()+employee.getNachName()};
		Log.i("schlüssel", rotk.toString());
		Log.i("exchangee", ConfigRabbitMQ.EXCHANGE_RATING);
		ConsumerRating = new MessageConsumer(ConfigRabbitMQ.IP,exchange,ConfigRabbitMQ.EXCHANGE_TYPE,rotk);
		new consumerconnect().execute();
		// register for messages
		ConsumerRating.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {

			public void onReceiveMessage(byte[] message) {
				
				json=null;
				try {
					json = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				situation = new Gson().fromJson(json, Situation.class);
				changeRatingButton();
				receiveRatingButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
							Intent i = new Intent(getApplicationContext(), Rating.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("toClass","To_Activity_Rating"); 
							i.putExtra("message", json);
					    	getApplicationContext().startActivity(i);
					}
				});	
			}
		});
		} else {
			Toast.makeText(getApplicationContext(), "Bitte erstellen Sie zuerst Ihr Profil!", Toast.LENGTH_LONG).show();
		}
    	} else {
    		Toast.makeText(getApplicationContext(), "Bitte erstellen Sie zuerst Ihr Profil!", Toast.LENGTH_LONG).show();
    	}
    	
    }
	
	private class consumerconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {

				// Connect to broker
				ConsumerRating.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class consumerOverlapTerminconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {

				// Connect to broker
				ConsumerOverlapTermin.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
        case R.id.opt_beenden:
        	finish(); return true;
        default: 
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public Timestamp getTime(long timestamp) {
		java.util.Date time = new java.util.Date(timestamp);
	    Timestamp h= new Timestamp(time.getTime());
	    return h;
	}

    public void onClickUserProfile(final View sfNormal) {
        //Startet Location Activity
    	Intent i = new Intent(this, Profile.class);
    	startActivity(i);
    }
    public void onClickReceiveRating(final View sfNormal) {
    	defaultRatingButton();
        //Startet Location Activity
    	Intent i = new Intent(this, Rating.class);
    	startActivity(i);
    }
        
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}

