package com.notfallchat.autist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
 

/**
 * Class Handlungsprofil to save new autistics who supervise in userlist.json.
*  This Class is manly for the user selection in Terminvergabe.java
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */

public class Handlungsprofil extends Activity {
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handlungsprofil);
		
		// TODO
		// display not only display the time critical situations
		
		if (Profile.fileExistance("handlungsprofil.json", getApplicationContext())) {
			List<Situation> situationsAndTheirOption = Profile.getSituationList(getApplicationContext(), "handlungsprofil.json");
			if (!situationsAndTheirOption.isEmpty()) {
					LinearLayout handlungsprofillayout = (LinearLayout) findViewById(R.id.handlungsprofillayout);
					
					for (Situation situation : situationsAndTheirOption ) {
						LinearLayout options = (LinearLayout)findViewById(R.id.handlungsprofillayout);
						
						Kategorie categoryOfSituation = situation.getKategorie();
						User employee = situation.getMitarbeiterImKontext(0);
						Handlungsoption optionFromCoach = situation.getNotfallhandlung();
						
						TextView stDescription = new TextView(getApplicationContext());
						stDescription.setText(situation.getSituationsbeschreibung());
						handlungsprofillayout.addView(stDescription);
						
						TextView stCategory = new TextView(getApplicationContext());
						stCategory.setText(categoryOfSituation.getName());
						handlungsprofillayout.addView(stCategory);
						
						TextView stEmployee = new TextView(getApplicationContext());
						stEmployee.setText(employee.getVorName() + " " + employee.getNachName());
						handlungsprofillayout.addView(stEmployee);
						
						TextView stEmployeePosition = new TextView(getApplicationContext());
						stEmployeePosition.setText(employee.getPosition());
						handlungsprofillayout.addView(stEmployeePosition);
						
						TextView action = new TextView(getApplicationContext());
						action.setText(optionFromCoach.getTitle());
						handlungsprofillayout.addView(action);
						
						
						int countStep=1;
						for (String step : optionFromCoach.getSchritte()) {
							TextView txStep = new TextView(getApplicationContext());
							txStep.setText("Schritt"+countStep+": " + step);
							handlungsprofillayout.addView(txStep);
							countStep++;
						}
							
						TextView txReason = new TextView(getApplicationContext());
						txReason.setText(optionFromCoach.getReason());	
						handlungsprofillayout.addView(txReason);
							
						if (optionFromCoach.getChatAboutAction() != null) {
							for (String conversation : optionFromCoach.getChatAboutAction()) {
								TextView chat = new TextView(getApplicationContext());
								chat.setText(conversation);	
								handlungsprofillayout.addView(chat);
							}
						}
							
						TextView rating = new TextView(getApplicationContext());
						if (optionFromCoach.isGoodRatingFromAutist()) {
							rating.setText("Gut");
						} 
						if (optionFromCoach.isBadRatingFromAutist()) {
							rating.setText("Schlecht");
						} 
						if (optionFromCoach.isCentralRatingFromAutist()) {
							rating.setText("Mittel");
						} 
						
						handlungsprofillayout.addView(rating);
						
						TextView review = new TextView(getApplicationContext());
						review.setText(optionFromCoach.getCommentRatingFromAutist());
						handlungsprofillayout.addView(review);
					}
				}
		}
	}

	public void btnhpMainMenu(View v) throws Exception {
	   Intent i = new Intent(this, MainActivity.class);
	   startActivity(i);
	}
		
	
}
	