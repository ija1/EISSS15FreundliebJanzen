package com.example.jobcoach;

import android.widget.EditText;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;

public class RPCClient {

	private Connection connection;
	private Channel channel;
	private String requestQueueName = "userlist";
	private String replyQueueName;
	private QueueingConsumer consumer;

	public RPCClient() throws Exception {
	    
		try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost("192.168.178.20");
		    factory.setUsername("guest");
			factory.setPassword("guest");
			//factory.setConnectionTimeout(0);
			factory.setPort(5672);
			
			connection = factory.newConnection();
		    channel = connection.createChannel();

		    replyQueueName = channel.queueDeclare().getQueue(); 
		    consumer = new QueueingConsumer(channel);
		    channel.basicConsume(replyQueueName, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}

	public String call(User user) throws Exception {     
	    String response = null;
	    String corrId = java.util.UUID.randomUUID().toString();

	    
	    String jsonString = new Gson().toJson(user);
	    
	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .correlationId(corrId)
	                                .replyTo(replyQueueName)
	                                .build();

	    
	    
	    channel.basicPublish("", requestQueueName, props, jsonString.getBytes());
	   

	    while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            response = new String(delivery.getBody());
	            System.out.println("response3client:"+response);
	            break;
	        }
	    }
	    System.out.println("response client:" + response);
	    return response; 
	}

	public void close() {
	    try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}