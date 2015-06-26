package com.example.jobcoach;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Base class to connect to RabbitMQ Broker
 */
public abstract class IConnectToRabbitMQ {
	public String mServer;
    public String mExchange;
    public String[] routingkey;

    protected Channel channel = null;
    protected Connection  mConnection;

    protected boolean Running ;

    protected  String MyExchangeType ;

    /**
     *
     * @param server The server address
     * @param exchange The named exchange
     * @param exchangeType The exchange type name
     */
    public IConnectToRabbitMQ(String server, String exchange, String exchangeType, String[] rk)
    {
  	  mServer = server;
  	  mExchange = exchange;
      MyExchangeType = exchangeType;
      routingkey = rk;
    }

    public void Dispose()
    {
        Running = false;

			try {
				if (mConnection!=null)
					mConnection.close();
				if (channel != null)
					channel.abort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    }

    /**
     * Connect to the broker and create the exchange
     * @return success
     */
    public boolean connectToRabbitMQ()
    {
  	  if(channel!= null && channel.isOpen() )//already declared
  		  return true;
        try
        {
      	  ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(mServer);
            connectionFactory.setConnectionTimeout(0);
            mConnection = connectionFactory.newConnection();
            channel = mConnection.createChannel();
            channel.exchangeDeclare(mExchange, MyExchangeType, true);
            

            return true;
        }
        catch (Exception e)
        {
      	  e.printStackTrace();
            return false;
        }
    }
}