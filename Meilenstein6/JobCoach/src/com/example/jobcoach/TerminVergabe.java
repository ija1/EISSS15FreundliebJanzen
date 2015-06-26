package com.example.jobcoach;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import com.example.jobcoach.MessageConsumer.OnReceiveMessageHandler;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.example.jobcoach.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.ArrayList;


/**
 * Activity arrange a termin
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class TerminVergabe extends Activity {
	private MessageConsumer ConsumerKritisch;
	private MessageConsumer ConsumerTermin;
	
	private EditText betreff;
	private DatePicker startDate;
	private DatePicker endDate;
	private TimePicker starttime;
	private TimePicker endtime;
	private EditText bemerkung;
	private User[] users;
	private User Chooseuser;
	private List<User> userlist;
	private boolean rpcOneTime = true;
	private Spinner spinner2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminvergabe);
		
		betreff = (EditText) findViewById(R.id.betreff);
		startDate = (DatePicker) findViewById(R.id.startDate);
		starttime = (TimePicker) findViewById(R.id.startTime);
		endDate = (DatePicker) findViewById(R.id.endDate);
		endtime = (TimePicker) findViewById(R.id.endTime);
		bemerkung = (EditText) findViewById(R.id.bemerkung);
	
//		// Create the consumer
//		ConsumerKritisch = new MessageConsumer("192.168.178.20", this.EXCHANGE_KRITISCH, "topic","test");
//		new consumerconnect().execute();
//		// register for messages
//		ConsumerKritisch.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
//
//			public void onReceiveMessage(byte[] message) {
//				String text = "";
//				try {
//					text = new String(message, "UTF8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		addUserList();
		
		
		final Button sendButton = (Button)findViewById(R.id.sendTermin);
		
		sendButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Termin termin = new Termin();
				// set title
				termin.setBetreff(betreff.getText().toString());
				
				// set start time
				int sYear = startDate.getYear();
				int sMonth = startDate.getMonth();
				int sDay = startDate.getDayOfMonth();
				int sHour = starttime.getCurrentHour();
				int sMinutes = starttime.getCurrentMinute();
				GregorianCalendar sCalendarDate = new GregorianCalendar(sYear, sMonth, sDay, sHour, sMinutes);
				Date sDate = sCalendarDate.getTime();
				long sTimestamp = sDate.getTime();
				termin.setStartDatum(sTimestamp);
				
				// set end time
				int eYear = endDate.getYear();
				int eMonth = endDate.getMonth();
				int eDay = endDate.getDayOfMonth();
				int eHour = endtime.getCurrentHour();
				int eMinutes = endtime.getCurrentMinute();
				GregorianCalendar eCalendarDate = new GregorianCalendar(eYear, eMonth, eDay, eHour, eMinutes);
				Date eDate = eCalendarDate.getTime();
				long eTimestamp = eDate.getTime();
				termin.setEndDatum(eTimestamp);
				
				// take selected user
				spinner2.getSelectedItem();
				Chooseuser = userlist.get(spinner2.getSelectedItemPosition());
				// set info
				termin.setBemerkung(bemerkung.getText().toString());
				
				// serializiered
				String jsonString = new Gson().toJson(termin);
				User coach = Profile.getUser(getApplicationContext(),"profile.json");
				final String[] rk = {"autist"+coach.getVorName()+coach.getNachName()+Chooseuser.getVorName()+Chooseuser.getNachName()};
				//wait for confirmation
				ConsumerTermin = new MessageConsumer(ConfigRabbitMQ.IP,ConfigRabbitMQ.EXCHANGE_TERMIN, ConfigRabbitMQ.EXCHANGE_TYPE,rk );
				new consumerconnect().execute();
				// register for messages
				ConsumerTermin.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
					
					@Override
					public void onReceiveMessage(byte[] message) {
						String text = "";
						try {
							text = new String(message, "UTF8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent i = new Intent(getApplicationContext(), JobCoach.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.setAction("android.intent.action.MAIN");
						i.putExtra("toClass","To_Activity_TerminVergabe"); 
						i.putExtra("message", text);
						getApplicationContext().startActivity(i);
					}
				});
					
				
				//send to selected autist
				new send().execute(jsonString);	
				Intent intent = new Intent(getApplicationContext(), JobCoach.class);
				startActivity(intent);
			}
		});
				
			
	
	}
	
	// send new termin to autist
//	public void btnSendTermin(View v) throws Exception {
//		
//		Termin termin = new Termin();
//		// set title
//		termin.setBetreff(betreff.getText().toString());
//		
//		// set start time
//		int sYear = startDate.getYear();
//		int sMonth = startDate.getMonth();
//		int sDay = startDate.getDayOfMonth();
//		int sHour = starttime.getCurrentHour();
//		int sMinutes = starttime.getCurrentMinute();
//		GregorianCalendar sCalendarDate = new GregorianCalendar(sYear, sMonth, sDay, sHour, sMinutes);
//		Date sDate = sCalendarDate.getTime();
//		long sTimestamp = sDate.getTime();
//		termin.setStartDatum(sTimestamp);
//		
//		// set end time
//		int eYear = endDate.getYear();
//		int eMonth = endDate.getMonth();
//		int eDay = endDate.getDayOfMonth();
//		int eHour = endtime.getCurrentHour();
//		int eMinutes = endtime.getCurrentMinute();
//		GregorianCalendar eCalendarDate = new GregorianCalendar(eYear, eMonth, eDay, eHour, eMinutes);
//		Date eDate = eCalendarDate.getTime();
//		long eTimestamp = eDate.getTime();
//		termin.setEndDatum(eTimestamp);
//		
//		// take selected user
//		spinner2.getSelectedItem();
//		Chooseuser = userlist.get(spinner2.getSelectedItemPosition());
//		// set info
//		termin.setBemerkung(bemerkung.getText().toString());
//		
//		// serializiered
//		String jsonString = new Gson().toJson(termin);
//		User coach = Profile.getUser(getApplicationContext(),"profile.json");
//		final String rk = "autist"+coach.getVorName()+coach.getNachName()+Chooseuser.getJobcoachVorname()+Chooseuser.getNachName();
//		//wait for confirmation
//		ConsumerTermin = new MessageConsumer("192.168.178.20","Termin", "topic",rk );
//		new consumerconnect().execute();
//		// register for messages
//		ConsumerTermin.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
//			
//			@Override
//			public void onReceiveMessage(byte[] message) {
//				String text = "";
//				try {
//					text = new String(message, "UTF8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Intent i = new Intent(getApplicationContext(), MainActivity.class);
//				i.putExtra("toClass","From_Activity_TerminVergabe"); 
//				i.putExtra("rotingkey", rk);
//				i.putExtra("message", text);
//				getApplicationContext().startActivity(i);
//		    	startActivity(i);
//			}
//		});
//			
//		
//		//send to selected autist
//		new send().execute(jsonString);	
//		Intent intent = new Intent(this, MainActivity.class);
//		startActivity(intent);
//		
//		
//	}
	
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
	
	public void btnCancel(View v) throws Exception {
		Intent i = new Intent(this, JobCoach.class);
    	startActivity(i);
	}
	

	public void addUserList() {
		
// first idea was to get list via rpc call
		
//		RPCClient fibonacciRpc = null;
//		String response = null;
//		try {
//	    fibonacciRpc = new RPCClient();
//
//	    // wie bei Autist
//	    User jobcoach = Profile.getUser(getApplicationContext(),"profile.json");
//		response = fibonacciRpc.call(jobcoach);
//		
//		//User[] list = new Gson().fromJson(response, User[].class);
//		
//		Type type = new TypeToken<List<User>>(){}.getType();
//	    List<User> inpList = new Gson().fromJson(response, type);
//	    users = new User[inpList.size()];
//	    for (int i=0;i<inpList.size();i++) {
//	    	User usr = inpList.get(i);
//	    	users[i] = usr;
//	    }
//
//	    rpcOneTime = false;
//
//		System.out.println("response2client:"+response);
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//    	}
//		finally {
//				if ( fibonacciRpc != null) {
//					try {
//						fibonacciRpc.close();
//					} catch (Exception ignore) {
//						ignore.printStackTrace();
//					}		
//				}
//		}
//		
//		
		userlist = Profile.getUserList(getApplicationContext(), "userlist.json");
		spinner2 = (Spinner) findViewById(R.id.userlist);
		List<String> list = new ArrayList<String>();
//		for ( int i=0 ; i<users.length; i++) {
//			list.add(i, users[i].getVorName() +","+users[i].getNachName() );
//		}
		int i=0;
		for (User autist : userlist) {
			list.add(i, autist.getVorName() +","+autist.getNachName() );
			i++;
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	  }
	
	private class send extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			
			try {
				// set access data via factory-Instanz.
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(ConfigRabbitMQ.IP);

				factory.setUsername(ConfigRabbitMQ.USER);
				factory.setPassword(ConfigRabbitMQ.PASSWORD);
				
				factory.setPort(ConfigRabbitMQ.PORT);
				factory.setConnectionTimeout(0);
				System.out.println(""+factory.getHost()+factory.getPort()+factory.getRequestedHeartbeat()+factory.getUsername());
				Connection connection = factory.newConnection();
				
				Channel channel = connection.createChannel();
				
				channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_TERMIN, ConfigRabbitMQ.EXCHANGE_TYPE, true);
				//channel.queueDeclare(QUEUE_TERMIN, false, false, false, null);
				User jobcoach = Profile.getUser(getApplicationContext(),"profile.json");
				String routingkey = "jobcoach"+jobcoach.getVorName()+jobcoach.getNachName()+Chooseuser.getVorName()+Chooseuser.getNachName();
				
				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];

				channel.basicPublish(ConfigRabbitMQ.EXCHANGE_TERMIN, routingkey, null,tempstr.getBytes());

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

	@Override
	protected void onResume() {
		super.onPause();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//ConsumerTermin.dispose();
	}
}