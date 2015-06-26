package com.example.jobcoach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for chatting with autist and save sending data in statistic.json
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class ChatWithAutist extends Activity {
	
	private MessageConsumer ConsumNotfall;
	private TextView situationDescription;
	private TextView category;
	private TextView employeeincontextfirstname;
	private TextView employeeincontextlastname;
	private TextView employeeposition;
	private TextView from;
	boolean end = false;
	
	private EditText title;
	/**
	 * Every option has minimum one step
	 */
	private EditText step1;
	/**
	 * The reason for the given option
	 */
	private EditText reason;
	private int stepCount=1;
	/**
	 * optional more steps
	 */
	private List<String> steps;
	
	private User autist;
	private User coach;
	private Situation situationFromJobCoach;
	private LinearLayout stLayoutReason;
	private Button sendAction;
	private Button btnCancelSituation;
	
	/**
     * Method that call Method consumeSituation
     * 
     * @param savedInstanceState save last Instance state
	 *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notfallchat);
        
        // get Intent from Jobcoach, to display the receiving data
        Intent intent = this.getIntent();
        if (intent != null) {
           if (intent.hasExtra("toClass") ) {
	           String strdata = intent.getExtras().getString("toClass");
		       if(strdata.equals("To_Activity_NotfallChat"))  {
		          String msg = intent.getExtras().getString("message");
		          consumeSituation(msg);
		       }
	       } 
        }
    }
    
    /**
     *  Method to Chat with the autist
     *  @param msg json string
     */
	private void consumeSituation(String msg) {
		 
		situationFromJobCoach = new Gson().fromJson(msg, Situation.class);
		autist = situationFromJobCoach.getAutist();
		final LinearLayout stAction = (LinearLayout)findViewById(R.id.actionLayout);
		coach = Profile.getUser(getApplicationContext(), "profile.json");
		
		from = new TextView(getApplicationContext());
		situationDescription = new TextView(getApplicationContext());
        category = new TextView(getApplicationContext());
        employeeincontextfirstname = new TextView(getApplicationContext());
        employeeincontextlastname= new TextView(getApplicationContext());
        employeeposition = new TextView(getApplicationContext());
		
		if (situationFromJobCoach.getNotfallhandlung() == null) {
			end = false;
			
			setSituationLayout(situationFromJobCoach);

	        title = new EditText(getApplicationContext());
	        title.setId(R.id.situation_title);
	        title.setHint("Handlungsoption: ");
	    	
	        reason = new EditText(getApplicationContext());
	    	reason.setId(R.id.situation_reason);
	    	reason.setHint("Begründung: ");
	    	
	    	step1 = new EditText(getApplicationContext());
	    	step1.setId(R.id.situation_step1);
	    	step1.setHint("Schritt 1: ");
	    	
	    	Button add = new Button(getApplicationContext());
	    	add.setWidth(50);
	    	add.setHeight(30);
	    	add.setText("Schritt hinzufügen");
			add.setId(R.id.add_button);
	
	    	stAction.addView(title);
	    	stAction.addView(step1);
	    	add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					stepCount++;
					stAction.addView(steps("set",stepCount));
				}
			});
	    	
	    	stAction.addView(add);
	    	
	    	stLayoutReason = (LinearLayout)findViewById(R.id.reasonLayout);
	    	stLayoutReason.addView(reason);
	
	    	sendAction = (Button) findViewById(R.id.sendAction);
	    	sendAction.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					JobCoach.defaultNotfallButton();
					
					
					situationFromJobCoach.setFrom(coach.getJobcoachVorname());
					
					Handlungsoption option = new Handlungsoption();
					option.setTitle(title.getText().toString());
					option.setReason(reason.getText().toString());
					//option.setSituation(0, situationFromJobCoach);
					EditText Step1 = (EditText)findViewById(R.id.situation_step1);
					option.setSchritte(0, step1.getText().toString());
					for (int i = 2; i<=stepCount;i++) {
						EditText getSteps = steps("get",i);
						option.setSchritte(i-1, getSteps.getText().toString());
					}
					
					UUID actionId =  UUID.randomUUID();
					Log.i("id", actionId.toString());
					option.setActionId(actionId);
					
					situationFromJobCoach.setNotfallhandlung(option);
					
					String json = new Gson().toJson(situationFromJobCoach);
					new send().execute(json);
				}
			});
	    	
	    	btnCancelSituation = (Button) findViewById(R.id.btnCancelSituation);
	    	btnCancelSituation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), JobCoach.class);
					startActivity(i);
					
				}
			});
		} else if (situationFromJobCoach.getNotfallhandlung() != null && situationFromJobCoach.getNotfallhandlung().getChatAboutAction() == null && ( !situationFromJobCoach.getNotfallhandlung().isBadRatingFromAutist() && !situationFromJobCoach.getNotfallhandlung().isGoodRatingFromAutist() && !situationFromJobCoach.getNotfallhandlung().isCentralRatingFromAutist())) {
			if (situationFromJobCoach.getFrom() != coach.getVorName() ) {
				JobCoach.changeNotfallButton();
			}
			end = false;
			//stAction.removeAllViews();
			setSituationLayout(situationFromJobCoach);
			Handlungsoption optionFromCoach = situationFromJobCoach.getNotfallhandlung();
			setAction(optionFromCoach,situationFromJobCoach,stAction);
			
		} else if (situationFromJobCoach.getNotfallhandlung() != null && situationFromJobCoach.getNotfallhandlung().getChatAboutAction() != null && ( !situationFromJobCoach.getNotfallhandlung().isBadRatingFromAutist() && !situationFromJobCoach.getNotfallhandlung().isGoodRatingFromAutist() && !situationFromJobCoach.getNotfallhandlung().isCentralRatingFromAutist()) ) {
			if (situationFromJobCoach.getFrom() != coach.getVorName() ) {
				JobCoach.changeNotfallButton();
			}
			//stLayoutReason.removeAllViews();
			//stAction.removeAllViews();
			setSituationLayout(situationFromJobCoach);
			Handlungsoption optionFromCoach = situationFromJobCoach.getNotfallhandlung();
			setAction(optionFromCoach,situationFromJobCoach,stAction);
			// TODO: Correct the option
			
			if (optionFromCoach.getChatAboutAction() != null) {
				for (String conversation : optionFromCoach.getChatAboutAction()) {
					TextView chat = new TextView(getApplicationContext());
					chat.setText(conversation);	
					stAction.addView(chat);
				}
			}
			
			//begin to chat or set done
			EditText chatTextAutist = new EditText(getApplicationContext());
			chatTextAutist.setHint("Frage beantworten: ");
			chatTextAutist.setId(R.id.chat_about_action);
			stAction.addView(chatTextAutist);
			
			sendAction = (Button) findViewById(R.id.sendAction);
	    	sendAction.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					JobCoach.defaultNotfallButton();
					User coach= Profile.getUser(getApplicationContext(), "profile.json");
					situationFromJobCoach.setFrom(coach.getJobcoachVorname());
					
					Handlungsoption option = situationFromJobCoach.getNotfallhandlung();
					EditText chat = (EditText)findViewById(R.id.chat_about_action);
					
					option.setChatAboutAction("Antwort: " + chat.getText().toString());
					situationFromJobCoach.setNotfallhandlung(option);
					String json = new Gson().toJson(situationFromJobCoach);
					new send().execute(json);
				}
			});
		} else {
			//stLayoutReason.removeAllViews();
			if (!end) {
				end =true;
			//stAction.removeAllViews();
			setSituationLayout(situationFromJobCoach);
			Handlungsoption optionFromCoach = situationFromJobCoach.getNotfallhandlung();
			setAction(optionFromCoach,situationFromJobCoach,stAction);
			if (optionFromCoach.getChatAboutAction() != null) {
				for (String conversation : optionFromCoach.getChatAboutAction()) {
					TextView chat = new TextView(getApplicationContext());
					chat.setText(conversation);	
					chat.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
					stAction.addView(chat);
				}
				situationFromJobCoach.setNotfallhandlung(optionFromCoach);
			}
			
			TextView txRating = new TextView(getApplicationContext());
			txRating.setText("Bewertung: ");	
			txRating.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			stAction.addView(txRating);
			
			TextView checkRating = new TextView(getApplicationContext());
			checkRating.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			if (situationFromJobCoach.getNotfallhandlung().isBadRatingFromAutist()) {
				checkRating.setText("Schlecht");
			} else if (situationFromJobCoach.getNotfallhandlung().isGoodRatingFromAutist()) {
				checkRating.setText("Gut");
			} else {
				checkRating.setText("Mittel");
			} 
			stAction.addView(checkRating);

			TextView txReview = new TextView(getApplicationContext());
			txReview.setText("Bewertung der Handlung: " + optionFromCoach.getCommentRatingFromAutist());	
			txReview.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			TextView txFinish = new TextView(getApplicationContext());
			txFinish.setText("Chat Ende, zurück zum Hauptmenü ...");	
			txFinish.setTextColor(Color.DKGRAY);
			txFinish.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			stAction.addView(txReview);	
			stAction.addView(txFinish);	
			
			
			// put Situation with Notfallhandlug a with id only one time to statistics
			List<Situation> situationList = null;
			if (Profile.fileExistance("statistik.json", getApplicationContext())) {
				boolean isNotInFile = true;
				situationList = Profile.getSituationList(getApplicationContext(), "statistik.json");
				List<Situation> newSituationList = new ArrayList<Situation>();
				if (!situationList.isEmpty()) {
						for (Situation st : situationList ) {
							if ( situationFromJobCoach.getNotfallhandlung().getActionId().equals(st.getNotfallhandlung().getActionId())) {
								isNotInFile = false;
							} else {
								newSituationList.add(st);
							}
						}
						if(isNotInFile) {
							newSituationList.add(situationFromJobCoach);
						}
						String jsonSituationList = new Gson().toJson(newSituationList);
						Profile.writeProfiletoFile(jsonSituationList, getApplicationContext(), "statistik.json");
				}else{
					situationList = new ArrayList<Situation>();
					situationList.add(situationFromJobCoach);
					Gson gson = new Gson();  
			   	    String jsonSituation = gson.toJson(situationList);    	
			   	    Profile.writeProfiletoFile(jsonSituation,getApplicationContext(),"statistik.json");
				}
				} else {
					situationList = new ArrayList<Situation>();
					situationList.add(situationFromJobCoach);
					Gson gson = new Gson();  
			   	    String jsonSituation = gson.toJson(situationList);    	
			   	    Profile.writeProfiletoFile(jsonSituation,getApplicationContext(),"statistik.json");
				}

		   	    RelativeLayout buttons = (RelativeLayout)findViewById(R.id.buttonLayout);
		   	    buttons.setVisibility(View.GONE);
		   	    Button cancel = new Button(this);
		   	    cancel.setText("Hauptmenü");
		   	    cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent i = new Intent(getApplicationContext(), JobCoach.class);
						startActivity(i);
						
					}
				});
		   	 stAction.addView(cancel);
			}
		}
	}
	
	/**
	 * Method for Displaying the reeceiving Option data
	 * @param optionFromCoach given Option from Jobcoach
	 * @param situationFromJobCoach current Situation
	 * @param stAction Layout for adding the whole views
	 */
	private void setAction(Handlungsoption optionFromCoach, Situation situationFromJobCoach, LinearLayout stAction) {
		TextView from = new TextView(getApplicationContext());
		from.setText("Von: " + situationFromJobCoach.getFrom());
		from.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		TextView title = new TextView(getApplicationContext());
		title.setText("Handlungsoption: " + optionFromCoach.getTitle());
		title.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		stAction.addView(from);
		stAction.addView(title);
		int countStep=1;
		for (String step : optionFromCoach.getSchritte()) {
			TextView txStep = new TextView(getApplicationContext());
			txStep.setText("Schritt"+countStep+": " + step);
			txStep.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
			stAction.addView(txStep);
			countStep++;
		}
		TextView txReason = new TextView(getApplicationContext());
		txReason.setText("Begründung: " + optionFromCoach.getReason());	
		txReason.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		stAction.addView(txReason);
	}

	/**
	 * Method for Displaying the reeceiving Sitution data
	 * @param situationFromJobCoach current Situation
	 */
	private void setSituationLayout(Situation situationFromJobCoach) {
		LinearLayout stLayout = (LinearLayout)findViewById(R.id.situationLayout);
		from.setText("Von: " + situationFromJobCoach.getAskForSituation());
		from.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		situationDescription.setText("Situation: "+situationFromJobCoach.getSituationsbeschreibung());
		situationDescription.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        Kategorie cat = situationFromJobCoach.getKategorie();
        category.setText("Kategorie: "+cat.getName());
        category.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        User employee = situationFromJobCoach.getMitarbeiterImKontext(0);
        employeeincontextfirstname.setText("Mitarbeiter im Kontext Vorname:"+employee.getVorName());
        employeeincontextfirstname.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        employeeincontextlastname.setText("Mitarbeiter im Kontext Nachname:"+employee.getNachName());
        employeeincontextlastname.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        employeeposition.setText("Mitarbeiter im Kontext Position:"+employee.getPosition());
        employeeposition.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        stLayout.addView(from);
        stLayout.addView(situationDescription);
        stLayout.addView(category);
        stLayout.addView(employeeincontextfirstname);
        stLayout.addView(employeeincontextlastname);
        stLayout.addView(employeeposition);
	}

	/**
	 * Method for getting or setting Steps
	 * @param set values can be "get" or "set"
	 * @param count set or get the step number
	 * @return the setting or getting step
	 */
	private EditText steps(String set, int count) {
		EditText step = null;
		if ( count <=20 ) {
			if (set == "set") {
				step = new EditText(getApplicationContext());
				step.setHint("Schritt "+count+":");
			} 
			switch (count) {
			case 2:
				if (set == "set")
					step.setId(R.id.step2);
				else
					step = (EditText)findViewById(R.id.step2);
				break;
			case 3: 
				if (set == "set")
					step.setId(R.id.step3);
				else
					step = (EditText)findViewById(R.id.step3);
				break;
			case 4:
				if (set == "set")
					step.setId(R.id.step4);
				else
					step = (EditText)findViewById(R.id.step4);
				break;
			case 5:
				if (set == "set")
					step.setId(R.id.step5);
				else
					step = (EditText)findViewById(R.id.step5);
				break;
			case 6:
				if (set == "set")
					step.setId(R.id.step6);
				else
					step = (EditText)findViewById(R.id.step6);
				break;
			case 7:
				if (set == "set")
					step.setId(R.id.step7);
				else
					step = (EditText)findViewById(R.id.step7);
				break;
			case 8:
				if (set == "set")
					step.setId(R.id.step8);
				else
					step = (EditText)findViewById(R.id.step8);
				break;
			case 9:
				if (set == "set")
					step.setId(R.id.step9);
				else
					step = (EditText)findViewById(R.id.step9);
				break;
			case 10:
				if (set == "set")
					step.setId(R.id.step10);
				else
					step = (EditText)findViewById(R.id.step10);
				break;
			case 11:
				if (set == "set")
					step.setId(R.id.step11);
				else
					step = (EditText)findViewById(R.id.step11);
				break;
			case 12:
				if (set == "set")
					step.setId(R.id.step12);
				else
					step = (EditText)findViewById(R.id.step12);
				break;
			case 13:
				if (set == "set")
					step.setId(R.id.step13);
				else
					step = (EditText)findViewById(R.id.step13);
				break;
			case 14:
				if (set == "set")
					step.setId(R.id.step14);
				else
					step = (EditText)findViewById(R.id.step14);
				break;
			case 15:
				if (set == "set")
					step.setId(R.id.step15);
				else
					step = (EditText)findViewById(R.id.step15);
				break;
			case 16:
				if (set == "set")
					step.setId(R.id.step16);
				else
					step = (EditText)findViewById(R.id.step16);
				break;
			case 17:
				if (set == "set")
					step.setId(R.id.step17);
				else
					step = (EditText)findViewById(R.id.step17);
				break;
			case 18:
				if (set == "set")
					step.setId(R.id.step18);
				else
					step = (EditText)findViewById(R.id.step18);
				break;
			case 19:
				if (set == "set")
					step.setId(R.id.step19);
				else
					step = (EditText)findViewById(R.id.step19);
				break;
			case 20:
				if (set == "set")
					step.setId(R.id.step20);
				else
					step = (EditText)findViewById(R.id.step20);
				break;
		}
		} else {
			Toast.makeText(getApplicationContext(), "Sie dürfen nicht mehr als 20 Schritte vergeben!", Toast.LENGTH_SHORT).show();
		}
		return step;
	}
	
	/**
	 * 
	 * 
	 *
	 */
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
				channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_ACTION, ConfigRabbitMQ.EXCHANGE_TYPE, true);

				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
				tempstr += Message[i];
				String rk = "autistundjobcoach"+autist.getJobcoachVorname()+autist.getJobcoachNachname()+autist.getVorName()+autist.getNachName();

				channel.basicPublish(ConfigRabbitMQ.EXCHANGE_ACTION, rk, null,tempstr.getBytes());
				channel.close();
				connection.close();

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

	}
	
    public void btnCancel(View v) throws Exception {
		Intent i = new Intent(this, JobCoach.class);
    	startActivity(i);
	}
    
    public void btnMainMenu(final View sfNormal) {
        //Startet Location Activity
    	Intent i = new Intent(this, JobCoach.class);
    	startActivity(i);
    }
    
    @Override
    	protected void onPause() {
    		// TODO Auto-generated method stub
    		super.onPause();
    	} 
}
