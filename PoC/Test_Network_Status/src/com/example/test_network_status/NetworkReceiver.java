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
		if (activeNetwork != null) { // connected to the Internet
		    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
		    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		    	String wifiName = wifiManager.getConnectionInfo().getSSID();
		    	if (wifiName != null && !wifiName.contains("unknown ssid")){
		    		Toast.makeText(
		    				null,
                            wifiName,
                            Toast.LENGTH_LONG).show();
		    	} else {
		    	    // network name unknown
		    	}
		    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
		    	TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		    	String networkName = tm.getNetworkOperatorName();
		    	if (networkName != null){
		    	    // networkName is the network name
		    	} else {
		    	    // network name unknown
		    	}
		    }
		} else {
		// not connected to the Internet
		}
	}

	

}