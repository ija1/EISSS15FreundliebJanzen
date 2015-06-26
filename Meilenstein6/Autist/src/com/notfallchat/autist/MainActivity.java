package com.notfallchat.autist;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import com.google.gson.Gson;
import com.notfallchat.autist.MessageConsumer.OnReceiveMessageHandler;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Events;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class Mainactivity for the main menu anto consum receiving termins
 * 
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class MainActivity extends Activity {
	
	private MessageConsumer ConsumerTermin;
	SettingsObserver observer = new SettingsObserver(new Handler());
    private PendingIntent pendingIntent;
	private static Termin termin;
	private Button userprofile;
	public static Button notfallchat;
	private Button receivetermin;
	private Button handlungsprofil;
	public static Button showDialogButton;
	
	// TODO:
	// solve with savedInstanceState parameter
	private boolean consumTerminFlag = false;
	
	/**
     * Method that call the consumer methods consumTerminfromJobCoach()
     * 
     * @param savedInstanceState save last Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        /******************* Main Menu Buttons *******************************/
        
        receivetermin = (Button) findViewById(R.id.receivetermin);
        receivetermin.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        handlungsprofil = (Button) findViewById(R.id.handlungsprofil);
        handlungsprofil.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);

        userprofile = (Button) findViewById(R.id.userprofile);
        userprofile.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
            
        notfallchat = (Button) findViewById(R.id.notfallChat);
        notfallchat.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);
        
        /**********************************************************************/
        
        // get Overlapping Info from Receiveer
        Intent intent = this.getIntent();
        String act = intent.getAction();
        if (act != null) {
        	boolean action = act.equalsIgnoreCase("android.intent.action.MAIN");
        
        	if (action) {
            	  if (intent.hasExtra("toClass") ) {
	            	  String strdata = intent.getExtras().getString("toClass");
		              if(strdata.equals("To_Activity_MainActivity"))  {
		            	  String msg = intent.getExtras().getString("message");
		            	  publishOverlapingInfo(msg); 
		              }
	              }
            	  
             }
        }

//        File dir = getFilesDir();
//        File file = new File(dir, "handlungsprofil.json");
//        boolean deleted = file.delete();
        consumTerminfromJobCoach(ConfigRabbitMQ.EXCHANGE_TERMIN);
        consumTerminFlag = true;
    }
    
    
    private void publishOverlapingInfo(final String msg) {
		termin = new Gson().fromJson(msg, Termin.class);
		final TerminDialogOverlaping dialog = new TerminDialogOverlaping(MainActivity.this);
		dialog.setContentView(R.layout.termindialogoverlaping);
		dialog.setTitle("Terminannahme");
		

		// set the custom dialog components - text and button
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(termin.getBetreff().toString());
		
		TextView startDate = (TextView) dialog.findViewById(R.id.startDate);
		startDate.setText(getTime(termin.getStartDatum()).toString());
		
		TextView endDate = (TextView) dialog.findViewById(R.id.endDate);
		endDate.setText(getTime(termin.getEndDatum()).toString());
		
		TextView titleOverlap = (TextView) dialog.findViewById(R.id.titleOverlap);
		titleOverlap.setText(termin.getBetreffOverlap().toString());
		
		TextView startDateOverlap = (TextView) dialog.findViewById(R.id.startDateOverlap);
		startDateOverlap.setText(getTime(termin.getStartDatumOverlap()).toString());
		
		TextView endDateOverlap = (TextView) dialog.findViewById(R.id.endDateOverlap);
		endDateOverlap.setText(getTime(termin.getEndDatumOverlap()).toString());

		Button dialogCheckButton = (Button) dialog.findViewById(R.id.checkButton);
		
		// if button is clicked, close the custom dialog
		dialogCheckButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
					dialog.cancel();
			}
			
		});
		
		Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		
		// if button is clicked, close the custom dialog
		dialogCancelButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
					dialog.hide();
			}
			
		});
		dialog.show();	
	}

    @Override
    protected void onResume() {
    	if (!consumTerminFlag) {
    		consumTerminfromJobCoach(ConfigRabbitMQ.EXCHANGE_TERMIN);
    	} else {
    		consumTerminFlag = false;
    	}
    	
    	// start the Observer for Calendar
    	Uri calendars = Uri.parse("content://com.android.calendar/events");
    	ContentResolver resolver = getContentResolver();
    	// set our watcher for the Uri we are concerned with
    	resolver.registerContentObserver(calendars,
    	false, // only notify that Uri of change *prob won't need true here often*
    	observer); // this innerclass handles onChange
    	
    	super.onResume();
    }

    @Override
    protected void onDestroy() {
    	// unregister the Calendar receiver
    	getContentResolver().unregisterContentObserver(observer);
    	super.onDestroy();
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

    public void consumTerminfromJobCoach(String exchange) {
    	if (Profile.fileExistance("profile.json", getApplicationContext())) {
		final User autist = Profile.getUser(getApplicationContext(),"profile.json");
		// Create the consumer
		String rotk = "jobcoach"+autist.getJobcoachVorname()+autist.getJobcoachNachname()+autist.getVorName()+autist.getNachName();
		ConsumerTermin = new MessageConsumer(ConfigRabbitMQ.IP, exchange, ConfigRabbitMQ.EXCHANGE_TYPE,rotk );
		new consumerconnect().execute();
		// register for messages
		ConsumerTermin.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {

			public void onReceiveMessage(byte[] message) {
				
				String json=null;
				try {
					json = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				termin = new Gson().fromJson(json, Termin.class);

				showDialogButton = (Button)findViewById(R.id.receivetermin);
				changeShowDialogButton();
				
				showDialogButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
							// TODO: Ask for saving
							final TerminDialog dialog = new TerminDialog(MainActivity.this);
							dialog.setContentView(R.layout.termindialog);
							dialog.setTitle("Termin aktzeptieren");
				 
							// set the custom dialog components - text, image and button
							TextView title = (TextView) dialog.findViewById(R.id.title1);
							title.setText("Betreff:    "+termin.getBetreff());
							
							TextView start = (TextView) dialog.findViewById(R.id.startDate1);
							start.setText("Start Zeit:   "+getTime(termin.getStartDatum()).toString());
							
							TextView end = (TextView) dialog.findViewById(R.id.endDate1);
							end.setText("End Zeit:    "+getTime(termin.getEndDatum()).toString());
							
							TextView info = (TextView) dialog.findViewById(R.id.info1);
							info.setText("Bemerkung: "+termin.getBemerkung());
							
							
							final User autist =Profile.getUser(getApplicationContext(),"profile.json");
							Button dialogCheckButton = (Button) dialog.findViewById(R.id.check1);
							
							// if button is clicked, close the custom dialog
							dialogCheckButton.setOnClickListener(new OnClickListener(){
								public void onClick(View v) {
									
									ContentResolver cr = getContentResolver();
									ContentValues values = new ContentValues();
									values.put(Events.DTSTART, termin.getStartDatum());
									values.put(Events.DTEND, termin.getEndDatum());
									values.put(Events.TITLE, termin.getBetreff());
									values.put(Events.DESCRIPTION,  termin.getBemerkung());
									values.put(Events.CALENDAR_ID, 5);
									values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
									Uri uri = cr.insert(Uri.parse("content://com.android.calendar/events/"), values);
									defaultShowDialogButton();
									termin.setCheck(true);
									termin.setAutist(autist);
									dialog.cancel();
								}
							});
							
							
							Button dialogUnCheckButton = (Button) dialog.findViewById(R.id.uncheck1);
							// if button is clicked, close the custom dialog
							dialogUnCheckButton.setOnClickListener(new OnClickListener(){
								public void onClick(View v) {	
									defaultShowDialogButton();
								termin.setCheck(false);
								termin.setAutist(autist);
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
    
    public static void changeNotfallButton(){
    	notfallchat.getBackground().setColorFilter(0xEEEE0000, Mode.MULTIPLY);	
    	notfallchat.invalidate();
    }
    public static void defaultNotfallButton(){
    	notfallchat.getBackground().setColorFilter(0xcccddddd, Mode.MULTIPLY);	
    	notfallchat.invalidate();
    }
	
	
	private class consumerconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {

				// Connect to broker
				ConsumerTermin.connectToRabbitMQ();

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
    
    class SettingsObserver extends ContentObserver {
        
    	SettingsObserver(Handler handler) {
            super(handler);
        }
        
    	// Run observer not twice
    	// TODO: if there is NOTIFICATION for starting calendar termin there are multiple calls, stop this .. 
        int count = 0;
        
      
        @Override
        public void onChange(boolean selfChange) {
            // if changes occurred in either of the watched Uris updateSettings()
            // updateSettings() is a normal void method that will be called to handle all changes
            // or you could just do your work here but presumable the same work will need to be done
            // on load of your class as well... but you get the picture
        	 
        	
        	//calendar
        	// only call for update not second time for sync see stakeoverflow
        	if (count == 0) {
        		updateSettings();
        	} else {
        		count = 0;
        	}
        }
        
        
		@Override
        public boolean deliverSelfNotifications() {
            return true;
        }
        
        private void updateSettings() {
        	
        	count++;
        	
        	
        	Cursor cur = getContentResolver().query(Uri.parse("content://com.android.calendar/events"), null, null, null, null);
            // this will make it point to the first record, which is the last SMS sent
        	cur.moveToLast();
        	
            
        	//String[] body = cur.getColumnNames();
        	//String title = cur.getString(cur.getColumnIndex("title"));
        	long dtstart = cur.getLong(cur.getColumnIndex("dtstart"));
        	long dtend = cur.getLong(cur.getColumnIndex("dtend"));
        	String _id = cur.getString(cur.getColumnIndex("_id"));
        	
        	Cursor cur2 = getContentResolver().query(Uri.parse("content://com.android.calendar/events"), null,  "_id != " + _id + " AND deleted = 0 AND dtend >= " + dtstart + " AND dtstart <=" + dtend, null,null);
        	        	
        	
        	if (cur2.getCount() == 0) {
        		System.out.println("No Job have to be include");
        	} else {
        		
        	// TODO: if Termin is in the past
	
            cur2.moveToFirst();
            //String title2 = cur2.getString(cur.getColumnIndex("title"));
        	long dtstart2 = cur2.getLong(cur.getColumnIndex("dtstart"));
        	long dtend2 = cur2.getLong(cur.getColumnIndex("dtend"));
        	//String _id2 = cur2.getString(cur.getColumnIndex("_id"));
        	
    		long threeMinutesBefore = 60*1000*3;
    		long timeToSend;
    		// auslagern da in reciever auch
    		// which employees should be informed
        	if (dtend > dtend2) {
        		timeToSend  = dtstart-threeMinutesBefore;
        	} else {
        		timeToSend = dtstart2-threeMinutesBefore;
        	}
        	// TODO: check event
        	// delete call alarmmanager if delete
   		
        	
        	// start calendar retrieve intervaöl for overlap
        	Intent alarmIntent = new Intent(MainActivity.this, CalendarReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            
            //TODO:
            // get call time
            // catch delete event
            // only one no interval
            //manager.setExact(AlarmManager.RTC_WAKEUP,timeToSend, pendingIntent);
            manager.set(AlarmManager.RTC_WAKEUP, timeToSend,pendingIntent);
            System.out.println("now Calendar Receiver is set");

            // TODO: 
            // delete Alarmmanager
        	}
        	// TODO
        	// if title = Meeting
        	}
        	
        }

    public PendingIntent send() {
    		System.out.println("its send");
    		return null;
    }
    	
    public static Timestamp getTime(long i) {
    		java.util.Date time = new java.util.Date((long)i);
    	    Timestamp h= new Timestamp(time.getTime());
    	    return h;
    }
    	
    public static Termin getTermin() {
    		return termin;
    }

    public void onClickReceiveTermin(final View sfNormal) {
        //Startet Location Activity
    	Intent i = new Intent(this, ReceiveTermin.class);
    	startActivity(i);
    }
    
    public void onClickUserProfile(final View sfNormal) {
        //Startet Location Activity
    	Intent i = new Intent(this, Profile.class);
    	startActivity(i);
    }
    
    public void onClickHandlungsprofil(final View sfNormal) {
        //Startet Location Activity
    	Intent i = new Intent(this, Handlungsprofil.class);
    	startActivity(i);
    }
    
    public void onClickNotFallChat(final View sfNormal) {
    	defaultNotfallButton();
        //Startet Location Activity
    	Intent i = new Intent(this, NotfallChat.class);
    	//i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
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

