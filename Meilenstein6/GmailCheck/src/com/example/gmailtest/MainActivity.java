package com.example.gmailtest;


import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// EMAIL und Überlappung
public class MainActivity extends ActionBarActivity {
	
    private PendingIntent pendingIntent;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
	
	


	// TODO Remove and set Observer
	public void btnStartSchedule(View v) {

		try {
			// Das Postfach soll Stündlich nach neuen eingehenden Mails untersucht werden um diese nach Einträgen wie "Meetings" 
			// zu untersuchen um diese dann in den Kalender einzutragen,, die Idee ist hierbei einen 
			// Scheduler in Form eines AlarmManagers zu initialisieren der diesen Job mit Hilfe eines Service ausführt.  
			AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(getApplicationContext(),EmailReceiver.class);
			final PendingIntent pIntent = PendingIntent.getBroadcast(this,0, intent, 0);
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 5000, pIntent);

			toast("Started...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	// TODO Remove and set Observer
	public void btnCancelSchedules(View v) {

		Intent intent = new Intent(getApplicationContext(),
				EmailReceiver.class);
		

		final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);

		AlarmManager alarms = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);

		alarms.cancel(pIntent);
		toast("Canceled...");
	}
	
	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
	

	@Override
    protected void onResume() {
        super.onResume(); 
    }

	@Override
    protected void onPause() {
        super.onPause();
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    @Override
    protected void onDestroy() {
    	//this.gmailObserver2.unregister();
    	System.out.println("testdestroy");
    	super.onDestroy();
    }

    	public PendingIntent send() {
    		System.out.println("send");
    		return null;
        }
}




