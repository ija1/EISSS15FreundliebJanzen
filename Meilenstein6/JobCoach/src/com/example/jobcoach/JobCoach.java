package com.example.jobcoach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.example.jobcoach.MessageConsumer.OnReceiveMessageHandler;
import com.google.gson.Gson;

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
 * Activity for displaying the main menu
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class JobCoach extends Activity {
	
	private static Termin setTermin;
	private MessageConsumer ConsumNotfall;
	private MessageConsumer ConsumRating;
	
	// Buttons for main menu
	private Button terminvergabe;
	public static Button receivetermin;
	private Button userprofile;
	private Button betreuung;
	public static Button statistics;
	public static Button notfallChat;
	
	private String jsonSituation;
	
	// TODO: better use savedInstanceState instead of this flag, to call consumer method only one time
	private boolean consumFlag = false;



	/**
     * Method that call the consumer methods consumNotfall() and consumRating();
     * 
     * @param savedInstanceState save last Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        terminvergabe = (Button) findViewById(R.id.terminvergabe);
        terminvergabe.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        receivetermin = (Button) findViewById(R.id.receivetermin);
        receivetermin.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        userprofile = (Button) findViewById(R.id.userprofile);
        userprofile.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        notfallChat = (Button) findViewById(R.id.notfallChat);
        notfallChat.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        betreuung = (Button) findViewById(R.id.betreuung);
        betreuung.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        statistics = (Button) findViewById(R.id.statistics);
        statistics.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        Intent intent = this.getIntent();
        String act = intent.getAction();
        if (act != null) {
        	boolean action = act.equalsIgnoreCase("android.intent.action.MAIN");
        
        	if (action) {
            	  if (intent.hasExtra("toClass") ) {
	            	  String strdata = intent.getExtras().getString("toClass");
		              if(strdata.equals("To_Activity_TerminVergabe"))  {
		            	  String msg = intent.getExtras().getString("message");
		            	  consumeCheckedTermin(msg); 
		              }
	              }
            	  
             }
        }


//        File dir = getFilesDir();
//        File file = new File(dir, "statistik.json");
//        boolean deleted = file.delete();
        consumNotfall();
        consumRating();
        consumFlag = true;
    }
    
    @Override
    protected void onResume() {
    	if (!consumFlag) {
    		consumNotfall();
    		consumRating();
    	} else {
    		consumFlag = false;
    	}
    	super.onResume();
    }
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    }

	private void consumNotfall() {
		if (Profile.fileExistance("userlist.json", getApplicationContext()) && Profile.fileExistance("profile.json", getApplicationContext())) {
		List<User> userlist = Profile.getUserList(getApplicationContext(), "userlist.json");
		User coach = Profile.getUser(getApplicationContext(), "profile.json");
		String[] rk = new String[userlist.size()];
		int i=0;
		for (User user : userlist) {
			rk[i] = "autistundjobcoach"+coach.getVorName()+coach.getJobcoachNachname()+user.getVorName()+user.getJobcoachNachname();
			i++;
		}
		// 192.168.178.20
        ConsumNotfall = new MessageConsumer(ConfigRabbitMQ.IP,ConfigRabbitMQ.EXCHANGE_ACTION,ConfigRabbitMQ.EXCHANGE_TYPE,rk);
        
		new consumerconnect().execute();
		
		// register for messages
		ConsumNotfall.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
			
			@Override
			public void onReceiveMessage(byte[] message) {
				jsonSituation = null;
				try {
					jsonSituation = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					Situation situation = new Gson().fromJson(jsonSituation, Situation.class);
				if (situation.getNotfallhandlung() == null) {
					changeNotfallButton();
					setNotfallButton();					
				}
				else {
					Intent i = new Intent(getApplicationContext(), ChatWithAutist.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("toClass","To_Activity_NotfallChat"); 
					i.putExtra("message", jsonSituation);
			    	getApplicationContext().startActivity(i);
					
				}
			}
		});
		} else {
			Toast.makeText(getApplicationContext(), "Bitte Liste für Betreute Autisten und Profil erstellen!", Toast.LENGTH_LONG).show();
		}
	}
	
	private void consumRating() {
		if (Profile.fileExistance("profile.json", getApplicationContext())) {
		User coach = Profile.getUser(getApplicationContext(), "profile.json");
		String[] rk = {"ratingjobcoach"+coach.getVorName()+coach.getNachName()};

        ConsumRating = new MessageConsumer(ConfigRabbitMQ.IP,ConfigRabbitMQ.EXCHANGE_RATING,ConfigRabbitMQ.EXCHANGE_TYPE,rk);
        
		new consumerRatingconnect().execute();
		
		// register for messages
		ConsumRating.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
			
			@Override
			public void onReceiveMessage(byte[] message) {
				jsonSituation = null;
				try {
					jsonSituation = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					Situation situation = new Gson().fromJson(jsonSituation, Situation.class);
				    //get List, search for id, set action for situation
					if (Profile.fileExistance("statistik.json", getApplicationContext())) {
						JobCoach.changeStatisticButton();
						List<Situation> situationList = Profile.getSituationList(getApplicationContext(), "statistik.json");
						List<Situation> newSituationList = new ArrayList<Situation>();
						if (!situationList.isEmpty()) {
								for (Situation st : situationList ) {
									if ( situation.getNotfallhandlung().getActionId().equals(st.getNotfallhandlung().getActionId())) {
										// save action with employee rating
										st.setNotfallhandlung(situation.getNotfallhandlung());
										newSituationList.add(st);
									} else {
										newSituationList.add(st);
									}
								}
								String jsonSituationList = new Gson().toJson(newSituationList);
								Profile.writeProfiletoFile(jsonSituationList, getApplicationContext(), "statistik.json");
						}
					}				
			}
		});
		} else {
			Toast.makeText(getApplicationContext(), "Bitte Liste für Betreute Autisten und Profil erstellen!", Toast.LENGTH_LONG).show();
		}
	}
	/**
     * Method that set OnClickListener to Button notfallchat, which sends jsonString to NotfallChat
     * 
     * @param savedInstanceState save last Instance state
     */
	private void setNotfallButton() {
			notfallChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), ChatWithAutist.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("toClass","To_Activity_NotfallChat"); 
					i.putExtra("message", jsonSituation);
			    	getApplicationContext().startActivity(i);
			}
		});	
	}
	
	private class consumerconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			
			try {

				// Connect to broker
				ConsumNotfall.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	private class consumerRatingconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			
			try {

				// Connect to broker
				ConsumRating.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}
	}

	
	private void consumeCheckedTermin(String msg) {
		
					final String text = msg;
					final Button receiveTermin = (Button)findViewById(R.id.receivetermin);
					changeReceiveTerminButton();
					receiveTermin.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
							setTermin = new Gson().fromJson(text, Termin.class);
							Log.i("json 1:", text);
							String a = text;
							final TerminDialog dialog = new TerminDialog(JobCoach.this);
							dialog.setContentView(R.layout.termindialog);
							dialog.setTitle("Terminannahme");

							// set the custom dialog components - text and button
							TextView title = (TextView) dialog.findViewById(R.id.title1);
							title.setText(setTermin.getBetreff());
							
							TextView start = (TextView) dialog.findViewById(R.id.startDate1);
							start.setText(getTime(setTermin.getStartDatum()).toString());
							
							TextView end = (TextView) dialog.findViewById(R.id.endDate1);
							end.setText(getTime(setTermin.getEndDatum()).toString());
							
							TextView info = (TextView) dialog.findViewById(R.id.info1);
							info.setText(setTermin.getBemerkung());
							
							TextView check = (TextView) dialog.findViewById(R.id.check);
							if (setTermin.isCheck()) {
								check.setText("Termin wurde aktzeptiert");
							} else {
								check.setText("Termin wurde nicht aktzeptiert");
							}
					

							Button dialogCheckButton = (Button) dialog.findViewById(R.id.checkButton);
							
							// if button is clicked, close the custom dialog
							dialogCheckButton.setOnClickListener(new OnClickListener(){
								public void onClick(View v) {
									if (setTermin.isCheck()) {
										ContentResolver cr = getContentResolver();
										ContentValues values = new ContentValues();
										values.put(Events.DTSTART, setTermin.getStartDatum());
										values.put(Events.DTEND, setTermin.getEndDatum());
										values.put(Events.TITLE, setTermin.getBetreff());
										values.put(Events.DESCRIPTION,  setTermin.getBemerkung());
										values.put(Events.CALENDAR_ID, 5);
										// German timezone
										defaultReceiveTerminButton();
										values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
										Uri uri = cr.insert(Uri.parse("content://com.android.calendar/events/"), values);
										dialog.cancel();
										
				
									} else {
										// TODO:
										// welcher Termin
										// von wem
										// warum
										// dauerhafte Anzeige
										
										defaultReceiveTerminButton();
										dialog.cancel();
										
									}
								}
							});
							dialog.show();
					}	
		});	
		}
	
		/**
		 * Method that return time by a given timestamp
		 * @param timestamp 
		 * @return Datetime
		 */
		public Timestamp getTime(long timestamp) {
			java.util.Date time = new java.util.Date(timestamp);
		    Timestamp h= new Timestamp(time.getTime());
		    return h;
		}

		public static Termin getTermin() {
			return setTermin;
		}

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.hauptmenue, menu);
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
	    
	    // TODO: only one Method with Buttons as Parameter to change Background Color of Buttons
	    public static void changeNotfallButton(){
	    	notfallChat.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
	    	notfallChat.invalidate();
	    }
	    public static void defaultNotfallButton(){
	    	notfallChat.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);	
	    	notfallChat.invalidate();
	    }
	    
	    public static void changeStatisticButton(){
	    	statistics.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
	    	statistics.invalidate();
	    }
	    public static void defaultStatistikButton(){
	    	statistics.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);	
	    	statistics.invalidate();
	    }
	    
	    public static void changeReceiveTerminButton(){
	    	receivetermin.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
	    	receivetermin.invalidate();
	    }
	    public static void defaultReceiveTerminButton(){
	    	receivetermin.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);	
	    	receivetermin.invalidate();
	    }
	    
    
	    public void onClickTerminVergabe(final View sfNormal){
	        //Startet Location Activity
	    	Intent i = new Intent(this, TerminVergabe.class);
	    	startActivity(i);
	    }
	    public void onClickUserProfile(final View sfNormal){
	        //Startet Location Activity
	    	Intent i = new Intent(this, Profile.class);
	    	startActivity(i);
	    }
	    public void onClickBetreuung(final View sfNormal){
	        //Startet Location Activity
	    	Intent i = new Intent(this, Betreuung.class);
	    	startActivity(i);
	    }
	    public void onClickReceiveTermin(final View sfNormal){
	        //Startet Location Activity
	    	Intent i = new Intent(this, ReceiveTermin.class);
	    	startActivity(i);
	        }
	    public void onClickNotFallChat(final View sfNormal) {
	    	defaultNotfallButton();
	        //Startet Location Activity
	    	Intent i = new Intent(this, ChatWithAutist.class);
	    	startActivity(i);
	    }
	    public void onClickStatistics(final View sfNormal) {
	    	defaultStatistikButton();
	        //Startet Location Activity
	    	Intent i = new Intent(this, Statistics.class);
	    	startActivity(i);
	    }

	    @Override
	    protected void onPause() {
	    	// TODO Auto-generated method stub
	    	super.onPause();
	    }

}
