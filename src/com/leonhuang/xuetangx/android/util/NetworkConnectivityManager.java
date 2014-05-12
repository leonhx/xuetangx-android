package com.leonhuang.xuetangx.android.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.leonhuang.xuetangx.R;

public class NetworkConnectivityManager {

	private Activity __activity;

	public NetworkConnectivityManager(Activity activity) {
		__activity = activity;
	}

	public boolean isConnectingToInternet(boolean toastIfNot) {
		ConnectivityManager __cm = (ConnectivityManager) __activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (__cm != null) {
			NetworkInfo[] info = __cm.getAllNetworkInfo();
			if (null != info)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}

		if (toastIfNot) {
			__activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(__activity, R.string.util_internet_inavail,
							Toast.LENGTH_SHORT).show();

				}
			});
		}

		return false;
	}

	public boolean isConnectingViaWifi() {
		ConnectivityManager __cm = (ConnectivityManager) __activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = __cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != info) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	public boolean isConnectingViaWiMAX() {
		ConnectivityManager __cm = (ConnectivityManager) __activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = __cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		if (null != info) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isConnectingViaWifiOrWiMAX() {
		return isConnectingViaWifi() || isConnectingViaWiMAX();
	}

}
