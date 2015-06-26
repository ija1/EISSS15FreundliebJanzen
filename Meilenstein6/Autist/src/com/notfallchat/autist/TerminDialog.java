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
 * Activity to confirm the termin
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class TerminDialog extends Dialog{
	
	private Termin termin;

	public TerminDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onStop() {
		// if a button is pressed in TerminDialog, send decision to jobcoach
		termin = MainActivity.getTermin();
		termin.setAutist(termin.getAutist());
		String message = new Gson().toJson(termin);
		new send().execute(message);
		super.onStop();
	}
	
	// send termin decision
	private class send extends AsyncTask<String, Void, Void> {

			@Override
			protected Void doInBackground(String... Message) {
				try {
					// put the access data via factory-instance
					ConnectionFactory factory = new ConnectionFactory();
					factory.setHost(ConfigRabbitMQ.IP);

					factory.setUsername(ConfigRabbitMQ.USER);
					factory.setPassword(ConfigRabbitMQ.PASSWORD);
					
					factory.setPort(5672);
					factory.setConnectionTimeout(0);
					
					System.out.println(""+factory.getHost()+factory.getPort()+factory.getRequestedHeartbeat()+factory.getUsername());
					Connection connection = factory.newConnection();
					Channel channel = connection.createChannel();
					channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_TERMIN, ConfigRabbitMQ.EXCHANGE_TYPE, true);
					
					String tempstr = "";
					for (int i = 0; i < Message.length; i++)
						tempstr += Message[i];

					// set the routing key to only send to the jobcoach who send the termin
					User autist = Profile.getUser(getContext(),"profile.json");
					String routingkey= "autist"+autist.getJobcoachVorname()+autist.getJobcoachNachname()+autist.getVorName()+autist.getNachName();
					channel.basicPublish(ConfigRabbitMQ.EXCHANGE_TERMIN, routingkey, null,tempstr.getBytes());
					channel.close();
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
}
