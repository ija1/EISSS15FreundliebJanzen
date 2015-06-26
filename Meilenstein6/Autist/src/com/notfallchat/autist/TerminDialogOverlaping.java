package com.notfallchat.autist;

import java.io.IOException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Dialog to display Termin
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class TerminDialogOverlaping extends Dialog{
	
	private Termin termin;
	
	public TerminDialogOverlaping(Context context) {
		super(context);
	}
	
	@Override
	protected void onStop() {
		termin = MainActivity.getTermin();
		termin.setAutist(termin.getAutist());
		String message = new Gson().toJson(termin);
		new send().execute(message);
		super.onStop();
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
				
				channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_OVERLAP, ConfigRabbitMQ.EXCHANGE_TYPE, true);
				//channel.queueDeclare(QUEUE_TERMIN, false, false, false, null);

				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];
				channel.basicPublish(ConfigRabbitMQ.EXCHANGE_OVERLAP, "",null, tempstr.getBytes());
				
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
}
