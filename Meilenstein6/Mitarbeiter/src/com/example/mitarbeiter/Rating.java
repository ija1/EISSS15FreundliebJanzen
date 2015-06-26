package com.example.mitarbeiter;

import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.google.gson.Gson; 
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Activity to display Rating Form and send rating data to jobcoach
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Rating extends Activity {
	
	private Situation situation;
	private User autist;
	private User coach;
	private LinearLayout ratingID;
	private EditText rating;
	private CheckBox good;
	private CheckBox central;
	private CheckBox bad;
	private EditText ratingComment;
	
	

	/**
     * Method to display rating form
     * 
     * @param savedInstanceState save last Instance state
	 *
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating);
		
		
		// get Intent from Consumer, to color the button of Receivetremin red
        Intent intent = this.getIntent();
        if (intent != null) {
           if (intent.hasExtra("toClass") ) {
	           String strdata = intent.getExtras().getString("toClass");
		       if(strdata.equals("To_Activity_Rating"))  {
		          String msg = intent.getExtras().getString("message");
		          consumeSituation(msg);
		       }
	       } 
        }	
	}
	
	/**
	 * Handle receiving Situation Data and display rating Form
	 * @param msg json message
	 */
	private void consumeSituation(String msg) {
		 
		situation = new Gson().fromJson(msg, Situation.class);
		autist = situation.getAutist();

		// option from job coach
		TextView description = new TextView(getApplicationContext());
		description.setText("Sie waren mit dem Mitarbeiter (Autisten) " + autist.getVorName() + " " + autist.getNachName() + " in Kontakt, können sie den sozialen Kontext kurz bewerten: ");
		description.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		LinearLayout descriptionLayout = (LinearLayout)findViewById(R.id.descriptionID);
		descriptionLayout.addView(description);

		// get option
		final Handlungsoption option = situation.getNotfallhandlung();

		ratingID = (LinearLayout)findViewById(R.id.ratingID);
		LinearLayout ratingLayout = new LinearLayout(getApplicationContext());
		
		
		/*************** Rating Good  **********************/
		
		RelativeLayout goodLayout = new RelativeLayout(getApplicationContext());
		TextView txGood = new TextView(getApplicationContext());
		txGood.setText("Gut");
		txGood.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		
		good = new CheckBox(getApplicationContext());
		good.setId(R.id.action_good);
		good.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
		    		option.setGoodRatingFromEmployee(true);
				} else {
				    option.setGoodRatingFromEmployee(false);	
				}
			}
		});

		goodLayout.addView(txGood);
		goodLayout.addView(good);
		ratingLayout.addView(goodLayout);
		/***********************************************************/
		
		/**********************Rating Central *********************/
		RelativeLayout centralLayout = new RelativeLayout(getApplicationContext());
		TextView txCentral = new TextView(getApplicationContext());
		txCentral.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		txCentral.setText("Mittel ");
		
		central = new CheckBox(getApplicationContext());
		central.setId(R.id.action_cenral);
		central.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
	    			option.setCentralRatingFromEmployee(true);
				} else {
			    	option.setCentralRatingFromEmployee(false);
				}
			}
		});
		
		centralLayout.addView(txCentral);
		centralLayout.addView(central);
		ratingLayout.addView(centralLayout);
		/********************************************************************/
		
		/******************* Rating Bad **************************************/
		
		RelativeLayout badLayout = new RelativeLayout(getApplicationContext());
		TextView txBad = new TextView(getApplicationContext());
		txBad.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		txBad.setText("Schlecht ");

		bad = new CheckBox(getApplicationContext());
		bad.setId(R.id.action_bad);
		bad.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
	    			option.setBadRatingFromEmployee(true);
				} else {
			    	option.setBadRatingFromEmployee(false);
				}
			}
		});
		
		badLayout.addView(txBad);
		badLayout.addView(bad);
		ratingLayout.addView(badLayout);
		ratingID.addView(ratingLayout);
		/**************************************************************************/
		
		// Ratting Comment

		ratingComment = new EditText(getApplicationContext());
		ratingComment.setText("Bemerkung zu der Bewertung ...");
		ratingComment.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		ratingID.addView(ratingComment);
		/**************************************************************************/
		
		// Button Back to Main Menu
		Button mainMenu = new Button(getApplicationContext());
		LinearLayout.LayoutParams layoutParamsMainMenu = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParamsMainMenu.bottomMargin += 50;
		mainMenu.setLayoutParams(layoutParamsMainMenu);
		mainMenu.setLayoutParams(layoutParamsMainMenu);
		mainMenu.setText("Hauptmenü");
		mainMenu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
			}
		});
			
		Button sendToCoach = new Button(getApplicationContext());
		sendToCoach.setText("Senden");
		LinearLayout.LayoutParams layoutParamsSendCoach =new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParamsSendCoach.bottomMargin += 150;
		sendToCoach.setLayoutParams(layoutParamsSendCoach);
			
		sendToCoach.setLayoutParams(layoutParamsSendCoach);
		sendToCoach.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rating != null) {
					if (rating.getText().toString() != "" ) {
						option.setCommentRatingRatingFromEmployee(rating.getText().toString());
					}
				}
				if (good.isChecked()) {
					option.setGoodRatingFromEmployee(true);
				}
				if (bad.isChecked()) {
					option.setBadRatingFromEmployee(true);
				}
				if (central.isChecked()) {
					option.setCentralRatingFromEmployee(true);
				}
				situation.setNotfallhandlung(option);
				String jsonAction = new Gson().toJson(situation);
				new send().execute(jsonAction);
				MainActivity.defaultRatingButton();
				
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
			}
		});
		
		LinearLayout buttons = (LinearLayout)findViewById(R.id.buttons);
		buttons.addView(mainMenu);
		buttons.addView(sendToCoach);
	}
	
    private class send extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			
			try {
				// Setzen der Zugangsdaten mittels factory-Instanz.
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(ConfigRabbitMQ.IP);

				factory.setUsername(ConfigRabbitMQ.USER);
				factory.setPassword(ConfigRabbitMQ.PASSWORD);
				
				factory.setPort(ConfigRabbitMQ.PORT);
				factory.setConnectionTimeout(0);
				Connection connection = factory.newConnection();
				
				Channel channel = connection.createChannel();
				
				channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_RATING, ConfigRabbitMQ.EXCHANGE_TYPE, true);

				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];
				String rk = "ratingjobcoach"+autist.getJobcoachVorname()+autist.getJobcoachNachname();
				channel.basicPublish(ConfigRabbitMQ.EXCHANGE_RATING, rk, null,tempstr.getBytes());

				channel.close();

				connection.close();

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}
	}
}
	