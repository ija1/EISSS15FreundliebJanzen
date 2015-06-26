package com.example.jobcoach;

import java.io.IOException;
import android.os.Handler;
import android.util.Log;

import com.rabbitmq.client.QueueingConsumer;

/**
*Consumes messages from a RabbitMQ broker
*
*/
public class MessageConsumer extends  IConnectToRabbitMQ{
	
	private String[] rt;
	
  public MessageConsumer(String server, String exchange, String exchangeType, String[] routingkey) {
      super(server, exchange, exchangeType, routingkey);
      this.rt = routingkey;
  }

  //The Queue name for this consumer
  private String queueName;
  private QueueingConsumer consumeMessage;

  //last message to post back
  private byte[] message;

  // An interface to be implemented by an object that is interested in messages(listener)
  public interface OnReceiveMessageHandler{
	  String test = "";
      public void onReceiveMessage(byte[] message);
  };

  //A reference to the listener, we can only have one at a time(for now)
  private OnReceiveMessageHandler mOnReceiveMessageHandler;

  /**
   *
   * Set the callback for received messages
   * @param handler The callback
   */
  public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler){
      mOnReceiveMessageHandler = handler;
  };

  private Handler handler = new Handler();
  private Handler mConsumeHandler = new Handler();

  // Create runnable for posting back to main thread
  final Runnable mReturnMessage = new Runnable() {
      public void run() {
          mOnReceiveMessageHandler.onReceiveMessage(message);
      }
  };

  final Runnable mConsumeRunner = new Runnable() {
      public void run() {
          Consume();
      }
  };

  /**
   * Create Exchange and then start consuming. A binding needs to be added before any messages will be delivered
   */
  @Override
  public boolean connectToRabbitMQ()
  {
     if(super.connectToRabbitMQ())
     {

         try {
        	 
             queueName = channel.queueDeclare().getQueue();
             channel.exchangeDeclare(mExchange, "direct",true);
             consumeMessage = new QueueingConsumer(channel);
             
             
             for(String severity : rt){    
            	 channel.queueBind(queueName, mExchange,severity);
             }
             
            
             channel.basicConsume(queueName, true, consumeMessage);
          } catch (IOException e) {
              e.printStackTrace();
              return false;
          }
//           if (MyExchangeType == "fanout")
//                 AddBinding("");//fanout has default binding

          Running = true;
          mConsumeHandler.post(mConsumeRunner);

         return true;
     }
     return false;
  }

  /**
   * Add a binding between this consumers Queue and the Exchange with routingKey
   * @param routingKey the binding key eg GOOG
   */
  public void AddBinding(String routingKey)
  {
      try {
          channel.queueBind(queueName, mExchange, routingKey);
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
  }

  /**
   * Remove binding between this consumers Queue and the Exchange with routingKey
   * @param routingKey the binding key eg GOOG
   */
  public void RemoveBinding(String routingKey)
  {
      try {
          channel.queueUnbind(queueName, mExchange, routingKey);
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
  }

  private void Consume()
  {
      Thread thread = new Thread()
      {

           @Override
              public void run() {
               while(Running){
            	  // QueueingConsumer for receiving Messages
            	  // method nextDelivery receive messages and return
                  QueueingConsumer.Delivery delivery;
                  try {
                	  delivery = consumeMessage.nextDelivery();
                      message = delivery.getBody();
                      Log.v("message ", message.toString());
                      handler.post(mReturnMessage);
                  } catch (InterruptedException ie) {
                      ie.printStackTrace();
                  }
               }
           }
      };
      thread.start();

  }

  public void dispose(){
      Running = false;
  }
}
