package test.rabbit.net;


import java.io.IOException;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


 
public class SampleConsumer implements Runnable {
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(ConfigRabbitMQ.USERNAME);
		factory.setPassword(ConfigRabbitMQ.PASSWORD);
		factory.setHost(ConfigRabbitMQ.HOSTNAME);
		factory.setPort(ConfigRabbitMQ.PORT);
		Connection conn;
		try {
			 conn = factory.newConnection();
			 Channel channel = conn.createChannel();
			 channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_NAME, ConfigRabbitMQ.EXCHANGE_TYPE,true);
			 String queueName = channel.queueDeclare().getQueue();
			 channel.queueBind(queueName, ConfigRabbitMQ.EXCHANGE_NAME,"");
			 QueueingConsumer consumer = new QueueingConsumer(channel);
			 channel.basicConsume(queueName, true, consumer);
			

			 while (true) { 

				 QueueingConsumer.Delivery delivery;
				 try {
					 delivery = consumer.nextDelivery();
					 byte[] message = delivery.getBody();
					 String termin = new String(message, "UTF8");
					 System.out.println("received message: " + new String(delivery.getBody()) + " in thread: " + Thread.currentThread().getName());
					 // publish to employees
					 
					 publish(termin);
//					SampleProducer producer = new SampleProducer(termin);
//					new Thread(producer).start();
				 } catch (InterruptedException ie) {
				 continue;
				 }
			}
		} catch (IOException e) {
				 e.printStackTrace();
		}
	}
	
	public void publish(String message) {
		 ConnectionFactory factory = new ConnectionFactory();
		 factory.setUsername(ConfigRabbitMQ.USERNAME);
		 factory.setPassword(ConfigRabbitMQ.PASSWORD);
		 factory.setHost(ConfigRabbitMQ.HOSTNAME);
		 factory.setPort(ConfigRabbitMQ.PORT);
		 Connection conn;
		 try {
		 conn = factory.newConnection();
		 Channel channel = conn.createChannel();
		 
		 channel.exchangeDeclare(ConfigRabbitMQ.EXCHANGE_NAME, ConfigRabbitMQ.EXCHANGE_TYPE,true);
		 System.out.println("Producing message: " + message + " in thread: " + Thread.currentThread().getName());
		 
		 Termin termin = new Gson().fromJson(message, Termin.class);
		 String[] employees = DBManagement.selectEmployees(termin);
		 
		 for (String employee : employees) {
			 channel.basicPublish(ConfigRabbitMQ.EXCHANGE_NAME,employee, null, message.getBytes());	 
			 Thread.sleep(1000);    
		}
		 
		 channel.close();
		 conn.close();
		 } catch (IOException e) {
		 e.printStackTrace();
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}