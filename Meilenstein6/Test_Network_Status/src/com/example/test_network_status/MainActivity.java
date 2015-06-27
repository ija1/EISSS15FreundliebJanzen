package com.example.test_network_status;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity{
	private BroadcastReceiver receiver;
	
	
	public void onResume()
	  {
		super.onResume();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	    registerReceiver(receiver, filter);  

	  }

	  public void onPause()
	  {
		super.onPause();
	    unregisterReceiver(receiver);
	  }
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        
        receiver = new BroadcastReceiver() {
           

			@Override
			public void onReceive(Context context, Intent intent) {
				
				// ConnectivityManager gibt auskunft über den Status der Netzwerkverbindung
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);     
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				if (activeNetwork != null) { 
					// connected to the internet
				    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				    	// if device is connected to a wifi network, the name can get via wifimanager 
				    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				    	String wifiName = wifiManager.getConnectionInfo().getSSID();
				    	if (wifiName != null && !wifiName.contains("unknown ssid")){
				    		Toast.makeText(
				    				context,
		                            "Wifi- Network Name: "+wifiName,
		                            Toast.LENGTH_LONG).show();
				    	} else {
				    		Toast.makeText(
				    				context,
		                            "Network Name unknown",
		                            Toast.LENGTH_LONG).show();
				    	}
				    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				    	// If device is connected to the internet via a mobile network, the name can get via TelephonyManager 
				    	TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				    	String networkName = tm.getNetworkOperatorName();
				    	if (networkName != null){
//				    		Output = (TextView) findViewById(R.id.out3);
//							Output.append("networkName is the network name");
				    		Toast.makeText(
				    				context,
				    				"Mobile Network Name: "+networkName,
		                            Toast.LENGTH_LONG).show();
				    	   
				    	} else {
				    		
				    		Toast.makeText(
				    				context,
		                            "network name unknown",
		                            Toast.LENGTH_LONG).show();
				    	}
				    }
				} else {
					Toast.makeText(
							context,
		                    "not connected to the internet",
		                    Toast.LENGTH_LONG).show();
				}
			}

        };
        
        //registerReceiver(receiver, filter);
        
        
        // listen for changes in the device’s Internet connectivity state, and call the “onReceive” method every time a change happens.
        //BroadcastReceiver networkChangeReceiver = null;
		// register the receiver
        //registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
