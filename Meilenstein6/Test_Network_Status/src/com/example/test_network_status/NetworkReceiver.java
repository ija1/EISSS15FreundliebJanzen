package com.example.test_network_status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
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
//		    		Output = (TextView) findViewById(R.id.out3);
//					Output.append("networkName is the network name");
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

	}