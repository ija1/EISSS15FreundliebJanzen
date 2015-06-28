package com.notfallchat.autist;

import com.notfallchat.autist.MessageConsumer.OnReceiveMessageHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Class NotfallChat for Chatting with Jobcoach
 * 
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class NotfallChat extends Activity {

	private LinearLayout sendID;
	private RelativeLayout buttonsID;
	private EditText exStDescription;
	private EditText exStCategory;
	private EditText employeeincontextfirstname;
	private EditText employeeincontextlastname;
	private EditText employeeposition;
	private MessageConsumer ConsumerAction;
	private User autist;
	private TextView from;
	private TextView position;
	private TextView txStDescription;
	private TextView txStCategory;
	private TextView employee;
	private LinearLayout situationLayout;
	private EditText review = null;
	private CheckBox good;
	private CheckBox central;
	private CheckBox bad;
	private String rk;
	private LinearLayout reviewId;
	private String exchange;
	
	
	/**
     * Method for chatting
     * 
     * @param savedInstanceState save last Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notfallchat);
        
        // Display Fields for the receiving Situation Data
        from = new TextView(getApplicationContext());
        position = new TextView(getApplicationContext());
        txStDescription = new TextView(getApplicationContext());
        txStCategory = new TextView(getApplicationContext());
        employee = new TextView(getApplicationContext());
        review = null; 
        
        // Input Fields for the Situation	
        exStDescription = (EditText) findViewById(R.id.situationsbeschreibung);
        exStCategory = (EditText) findViewById(R.id.category);
        employeeincontextfirstname = (EditText) findViewById(R.id.employeeincontextfirstname);
        employeeincontextlastname= (EditText) findViewById(R.id.employeeincontextlastname);
        employeeposition = (EditText) findViewById(R.id.employeeposition);
        
        // Situation Layout
        situationLayout = (LinearLayout)findViewById(R.id.situationLayout);
        
        final Button sendSituation = (Button)findViewById(R.id.sendsituation);
        sendSituation.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				exchange = "Notfallchat";
				autist = Profile.getUser(getApplicationContext(), "profile.json");
				Kategorie kategorie = new Kategorie();
				kategorie.setName(exStCategory.getText().toString());
				Situation situation = new Situation();
				
				situation.setAutist(autist);
				situation.setKategorie(kategorie);
				situation.setAskForSituation(autist.getVorName()+ " " + autist.getNachName());
				User mitarbeiterImKontext = new User();
				
				// TODO
				// get List of all Employees
				// let the User select more then one employee
				
				mitarbeiterImKontext.setVorName(employeeincontextfirstname.getText().toString());
				mitarbeiterImKontext.setNachName(employeeincontextlastname.getText().toString());
				mitarbeiterImKontext.setPosition(employeeposition.getText().toString());
				situation.setMitarbeiterImKontext(0, mitarbeiterImKontext);
				situation.setSituationsbeschreibung(exStDescription.getText().toString());
				situation.setFrom(autist.getVorName());
				
				String jsonString = new Gson().toJson(situation);
				// Routing Key combination key jobcoach firstname + jobcoach lastname + autist firstname + autist lastname
				rk = "autistundjobcoach"+autist.getJobcoachVorname()+autist.getJobcoachNachname()+autist.getVorName()+autist.getNachName();

				//wait for confirmation
				ConsumerAction = new MessageConsumer(ConfigRabbitMQ.IP,exchange, ConfigRabbitMQ.EXCHANGE_TYPE,rk );
				new consumerconnect().execute();
				// register for messages
				ConsumerAction.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
					@Override
					public void onReceiveMessage(byte[] message) {
						String text = "";
						try {
							text = new String(message, "UTF8");
							
							situationLayout.removeAllViews();
							final Situation st = new Gson().fromJson(text, Situation.class);
							Kategorie whichCategory = st.getKategorie();
							User employeeInContext = st.getMitarbeiterImKontext(0);
							buttonsID = (RelativeLayout) findViewById(R.id.buttonsID);
							sendID = (LinearLayout)findViewById(R.id.sendID);
							
							// Situation receive
							if (st.getNotfallhandlung() == null) {
								setSituationLayout("Von: "+st.getAskForSituation(),"Position: "+employeeInContext.getPosition(),"Situationsbeschreibung: " + st.getSituationsbeschreibung(),whichCategory.getName(),"Mitarbeiter im Kontext: "+employeeInContext.getVorName() + " " +employeeInContext.getNachName(),true);
								buttonsID.removeAllViews();
								
							// Action receive
							} else if (st.getNotfallhandlung() != null && (!st.getNotfallhandlung().isBadRatingFromAutist() && !st.getNotfallhandlung().isGoodRatingFromAutist() && !st.getNotfallhandlung().isCentralRatingFromAutist())) {
								// change Color of Menu button when revceiving message is not from the same sender
								if (autist.getVorName() != st.getFrom())
									MainActivity.changeNotfallButton();
								
								// remove old Content to display new content
								sendID.removeAllViews();
								buttonsID.removeAllViews();
								situationLayout.removeAllViews();
								setSituationLayout("Von: "+st.getAskForSituation(),"Position: "+employeeInContext.getPosition(),"Situationsbeschreibung: " + st.getSituationsbeschreibung(),whichCategory.getName(),"Mitarbeiter im Kontext: "+employeeInContext.getVorName() + " " +employeeInContext.getNachName(),false);

								// option from job coach
								TextView fromCoach = new TextView(getApplicationContext());
								fromCoach.setText("Von: "+st.getFrom());	
								fromCoach.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
								situationLayout.addView(fromCoach);
								
								// get option
								final Handlungsoption optionFromCoach = st.getNotfallhandlung();
								
								// display option data
								TextView title = new TextView(getApplicationContext());
								title.setText("Handlungsoption: " + optionFromCoach.getTitle());
								title.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
								situationLayout.addView(title);
								
								// count steps 
								countSteps(optionFromCoach);
								
								// display Reason	
								TextView txReason = new TextView(getApplicationContext());
								txReason.setText("Begründung: " + optionFromCoach.getReason());	
								txReason.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
								situationLayout.addView(txReason);
									
								setConversation(optionFromCoach);

								//begin to chat or set done
								EditText chatTextAutist = new EditText(getApplicationContext());
								chatTextAutist.setHint("Frage zu dem gegebenen Handlungsvorschlag stellen");
								chatTextAutist.setId(R.id.chat_about_action);
								situationLayout.addView(chatTextAutist);
								
								/**************   Rating and Coment ******************/

								LinearLayout ratingLayout = new LinearLayout(getApplicationContext());
								TextView ratings = new TextView(getApplicationContext());
								ratings.setText("Bewertung:");
								ratings.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
								ratingLayout.addView(ratings);
								
								// Comment Rating
								reviewId = (LinearLayout)findViewById(R.id.reviewID);
								review = new EditText(getApplicationContext());
								review.setText("Bemerkung zu der Bewertung: ");
								review.setVisibility(View.GONE);
								
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
								    		optionFromCoach.setGoodRatingFromAutist(true);
											review.setVisibility(View.VISIBLE);
										} else {
										    optionFromCoach.setGoodRatingFromAutist(false);	
											review.setVisibility(View.GONE);
											isChecked = false;
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
								txCentral.setText("Mittel ");
								txCentral.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
								
								central = new CheckBox(getApplicationContext());
								central.setId(R.id.action_cenral);
								central.setOnCheckedChangeListener(new OnCheckedChangeListener(){
									@Override
									public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
										if (isChecked) {
							    			optionFromCoach.setCentralRatingFromAutist(true);
							    			review.setVisibility(View.VISIBLE);
										} else {
									    	optionFromCoach.setCentralRatingFromAutist(false);
											review.setVisibility(View.GONE);
											isChecked = false;
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
								txBad.setText("Schlecht ");
								txBad.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);

								bad = new CheckBox(getApplicationContext());
								bad.setId(R.id.action_bad);
								bad.setOnCheckedChangeListener(new OnCheckedChangeListener(){
									@Override
									public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
										if (isChecked) {
							    			optionFromCoach.setBadRatingFromAutist(true);
							    			review.setVisibility(View.VISIBLE);
										} else {
									    	optionFromCoach.setBadRatingFromAutist(false);
											review.setVisibility(View.GONE);
											isChecked = false;
										}
									}
								});
								
								badLayout.addView(txBad);
								badLayout.addView(bad);
								ratingLayout.addView(badLayout);
								/**************************************************************************/
								
								// add Rating to situationlayout
								situationLayout.addView(ratingLayout);
								reviewId.addView(review);
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
										exchange = "Notfallchat";
										MainActivity.defaultNotfallButton();
										EditText edChat = (EditText)findViewById(R.id.chat_about_action);
											String a =review.getText().toString();
										if (review != null) {
											if (review.getText().toString() != "" ) {
												optionFromCoach.setCommentRatingFromAutist(review.getText().toString());
											}
										}
										if (good.isChecked()) {
											optionFromCoach.setGoodRatingFromAutist(true);
										}
										if (bad.isChecked()) {
											optionFromCoach.setBadRatingFromAutist(true);
										}
										if (central.isChecked()) {
											optionFromCoach.setCentralRatingFromAutist(true);
										}
										
										optionFromCoach.setChatAboutAction("Frage" + edChat.getText().toString());
										st.setFrom(autist.getVorName());
										st.setNotfallhandlung(optionFromCoach);
										String jsonAction = new Gson().toJson(st);
										new send().execute(jsonAction);
									}
								});
								
								
								sendID = (LinearLayout)findViewById(R.id.sendID);
								sendID.addView(sendToCoach);
								
								// finish and save
								} else {
											exchange = "Rating";
											String jsonAction = new Gson().toJson(st);
											new send().execute(jsonAction);
											
											buttonsID.removeAllViews();
											sendID.removeAllViews();
											MainActivity.defaultNotfallButton();
	
											if (autist.getVorName() != st.getFrom())
												MainActivity.changeNotfallButton();
											
											if (st.getNotfallhandlung().isBadRatingFromAutist()) {
												 Intent i = new Intent(getApplicationContext(), Handlungsprofil.class);
												 startActivity(i);
											} else {
												List<Situation> situations = null;
												if (Profile.fileExistance("handlungsprofil.json", getApplicationContext())) {
													situations = Profile.getSituationList(getApplicationContext(), "handlungsprofil.json");
												} else {
													situations = new ArrayList<Situation>();
												}
												situations.add(st);
												Gson gson = new Gson();  
											    String json = gson.toJson(situations);  
											    Profile.writeProfiletoFile(json,getApplicationContext(),"handlungsprofil.json");
											    
											    Intent i = new Intent(getApplicationContext(), Handlungsprofil.class);
											    startActivity(i);
											}
										   
								}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}	
				});
				//send to selected autist
				new send().execute(jsonString);	
			}
		});
    }
    
    /**
     * count Steps of Option 
     * @params optionFromCoach for displaying steps from Option
     */
    private void countSteps(Handlungsoption optionFromCoach) {
    	int countStep=1;
		for (String step : optionFromCoach.getSchritte()) {
			TextView txStep = new TextView(getApplicationContext());
			txStep.setText("Schritt"+countStep+": " + step);
			txStep.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			situationLayout.addView(txStep);
			countStep++;
		}
	}
    
    /**
     * show conversation
     */
    private void setConversation(Handlungsoption optionFromCoach) {
    	if (optionFromCoach.getChatAboutAction() != null) {
			for (String conversation : optionFromCoach.getChatAboutAction()) {
				TextView chat = new TextView(getApplicationContext());
				chat.setText(conversation);	
				chat.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
				situationLayout.addView(chat);
			}
		}
	}

    /**
     *  Display Situation
     */
	private void setSituationLayout(String txfrom, String txPosition,String txBeschreibung, String txCategory, String txEmployee, boolean showMainMenu) {
		from.setText(txfrom);
		from.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		txStDescription.setText(txBeschreibung);
		txStDescription.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
 		position.setText(txPosition);
 		position.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		exStCategory.setText(txCategory);
		exStCategory.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		employee.setText(txEmployee);
		employee.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		situationLayout.addView(from);
		situationLayout.addView(txStDescription);
		situationLayout.addView(txStCategory);
		situationLayout.addView(employee);
		situationLayout.addView(position);
		if (showMainMenu) {
			Button mainMenu = new Button(getApplicationContext());
			mainMenu.setText("Hauptmenü");
	
			mainMenu.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
		
					
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					
					//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				
				}
			});
			situationLayout.addView(mainMenu);
		}
	}
	
	private class consumerconnect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {
				// Connect to broker
				ConsumerAction.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}
	}

    private class send extends AsyncTask<String, Void, Void> {

		
		@Override
		protected Void doInBackground(String... Message) {
			
			try {
				// set access data via factory-Instance.
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(ConfigRabbitMQ.IP);

				factory.setUsername(ConfigRabbitMQ.USER);
				factory.setPassword(ConfigRabbitMQ.PASSWORD);
				
				factory.setPort(ConfigRabbitMQ.PORT);
				factory.setConnectionTimeout(0);
				Connection connection = factory.newConnection();
				
				Channel channel = connection.createChannel();
				
				channel.exchangeDeclare(exchange, ConfigRabbitMQ.EXCHANGE_TYPE, true);
				//channel.queueDeclare(QUEUE_TERMIN, false, false, false, null);

				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];
				
				if (exchange == "Notfallchat") {
					rk = "autistundjobcoach"+autist.getJobcoachVorname()+autist.getJobcoachNachname()+autist.getVorName()+autist.getNachName();
				} else {
					rk = "ratingemployee"+employeeincontextfirstname.getText().toString()+employeeincontextlastname.getText().toString();
				}
				
				Log.i("schlüssel", rk);
				Log.i("exchangee", exchange);
				
				channel.basicPublish(exchange, rk, null,tempstr.getBytes());

				channel.close();

				connection.close();

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}

	}
    
    public void btnCancelSituation(final View sfNormal) {
    	
        //Startet Location Activity
    	Intent i = new Intent(this, MainActivity.class);
    	//i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    	startActivity(i);
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }

    public void btnCancel(View v) throws Exception {
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
    	startActivity(i);
	}
    
}
