package com.notfallchat.autist;

import com.google.gson.Gson;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class CalendarReceiver extends BroadcastReceiver {
	
	// TODO:
	// extra routing key for Job coach
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	if (Profile.fileExistance("profile.json", context)) {
    		final User autist = Profile.getUser(context,"profile.json");

	    	// TODO:
	    	// check events for beeing there
	    	Cursor cur = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), null, "dtend >= " + System.currentTimeMillis() + " AND dtstart <= " + System.currentTimeMillis() + " AND deleted=0", null, null);
	        // this will make it point to the first record, which is the last SMS sent
	    	
	    	if (cur != null) {
	    		cur.moveToLast();

		    	String title = cur.getString(cur.getColumnIndex("title"));
		    	long dtstart = cur.getLong(cur.getColumnIndex("dtstart"));
		    	long dtend = cur.getLong(cur.getColumnIndex("dtend"));
		    	String _id = cur.getString(cur.getColumnIndex("_id"));
		    	
		    	Cursor cur2 = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), null,  "_id != " + _id + " AND deleted =0 AND dtend >= " + dtstart + " AND dtstart <=" + dtend, null,null);
		    	
		    	if (cur2.getCount() > 0) {
			    	// need for inform the employees for which meeting he is sorry
			        cur2.moveToFirst();
			        String title2 = cur2.getString(cur.getColumnIndex("title"));
			    	long dtstart2 = cur2.getLong(cur.getColumnIndex("dtstart"));
			    	long dtend2 = cur2.getLong(cur.getColumnIndex("dtend"));
			    	String _id2 = cur2.getString(cur.getColumnIndex("_id"));
					
					Termin overlapTermin = new Termin();
					overlapTermin.setAutist(autist);
					overlapTermin.setBemerkungOverlap("Ich habe einen anderen Termin, desshalb muss ich diesen Termin verlassen, Könnten Sie mir bitte alle wichtigen Informationen zukommen lassen.");
					overlapTermin.setEndDatum(dtend);
					overlapTermin.setEndDatumOverlap(dtend2);
					overlapTermin.setStartDatum(dtstart);
					overlapTermin.setStartDatumOverlap(dtstart2);
					overlapTermin.setBetreff(title);
					overlapTermin.setBetreffOverlap(title2);
					
					String jsonOverlapTermin = new Gson().toJson(overlapTermin);
					
					Intent i = new Intent(context, MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setAction("android.intent.action.MAIN");
					i.putExtra("toClass","To_Activity_MainActivity"); 
					i.putExtra("message", jsonOverlapTermin);
					context.startActivity(i);
			        
					// TODO: what to do when time is reached
		    	}
	    	}
    	}
    }
}
    