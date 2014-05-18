package com.leonhuang.xuetangx.android.util;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			DownloadManager dm = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Query query = new Query();
			query.setFilterById(id);
			Cursor c = dm.query(query);
			if (null != c && c.moveToFirst()) {
				int statusIdx = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
				int urlIdx = c.getColumnIndex(DownloadManager.COLUMN_URI);
				int fileIdx = c
						.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
				if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(statusIdx)) {
					SharedPref.saveKeyValuePair(context, c.getString(urlIdx),
							c.getString(fileIdx));
					Log.i("Receiver", c.getString(fileIdx));
				}
			}
			if (null != c) {
				c.close();
			}
		}
	}

}
