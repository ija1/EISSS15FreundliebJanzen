package com.poc.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.poc.communication.R;
import com.poc.communication.MessageConsumer.OnReceiveMessageHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// POC Kommunikation

public class MainActivity extends Activity {
	private MessageConsumer mConsumer;
	private String EXCHANGE_NAME = "queuetest";
	private boolean consumFlag = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toast.makeText(MainActivity.this, "RabbitMQ Comunication Test!",
				Toast.LENGTH_LONG).show();

		
		
		LinearLayout newText = (LinearLayout)findViewById(R.id.newText);
		
		final EditText etv = new EditText(getApplicationContext());
		etv.setText("Text eingeben");
		
		Button send = new Button(getApplicationContext());
		send.setText("Sende an Queue:" + EXCHANGE_NAME);

		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new send().execute(etv.getText().toString());
			}
		});
		
		newText.addView(etv);
		newText.addView(send);
		
		final LinearLayout oldText = (LinearLayout)findViewById(R.id.oldtext);
		
		// Create the consumer
		mConsumer = new MessageConsumer("192.168.178.20",EXCHANGE_NAME, "direct");
		new consumerconnect().execute();
		// register for messages
		mConsumer.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
			public void onReceiveMessage(byte[] message) {
				etv.setText("");
				String text = "";
				try {
					text = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				TextView receiveText = new TextView(getApplicationContext());
				receiveText.setText(text);
				oldText.addView(receiveText);
			}
		});
		consumFlag = true;
	}

	private class send extends AsyncTask<String, Void, Void> {
		public String username = "Tester";
		public String password = "testtest1";
		public String host = "192.168.178.20";

		@Override
		protected Void doInBackground(String... Message) {
			try {
				// Setzen der Zugangsdaten mittels factory-Instanz.
				// Mit der channel-Instanz wird ein Thread erzeugt der sich mit dem RabbitMQ Message Broker verbindet
				// die Methode bsicPublish versendet die Nachricht an die definierte Exchange des Message Brokers
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(host);
				factory.setUsername(username);
				factory.setPassword(password);
				factory.setPort(5672);
				factory.setConnectionTimeout(0);
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, "direct",true);
				
				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];
				
				channel.basicPublish(EXCHANGE_NAME, "", null, tempstr.getBytes());

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

	
	private class consumerconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {

				// Connect to broker
				mConsumer.connectToRabbitMQ();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	@Override
	protected void onResume() {
		if (!consumFlag) {
			new consumerconnect().execute();	
		} else {
			consumFlag = false;
		}
		super.onPause();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mConsumer.dispose();
	}
}