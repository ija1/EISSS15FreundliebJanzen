package test.rabbit.net;

import java.io.IOException;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
 
public class SampleProducer implements Runnable {
 private final String message;

 public SampleProducer(final String message) {
 this.message = message;
 }
 
 @Override
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