package com.example.jobcoach;

import com.google.gson.Gson;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

/**
 * Dialog to display Termin
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class TerminDialog extends Dialog{
	
	public TerminDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onStop() {
		Termin t =JobCoach.getTermin();
		if(t.isCheck()) {
			Intent i = new Intent(getContext(), JobCoach.class);
			getContext().startActivity(i);
		} 
		super.onStop();
	}
}
