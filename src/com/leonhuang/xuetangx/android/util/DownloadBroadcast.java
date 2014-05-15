package com.leonhuang.xuetangx.android.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Pair;

public class DownloadBroadcast {
	public static Pair<BroadcastReceiver, IntentFilter> getDownloadBroadcastReceiverAndIntentFilter(
			Context context) {
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		BroadcastReceiver reveiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

			}

		};

		return new Pair<BroadcastReceiver, IntentFilter>(reveiver, filter);
	}
}
