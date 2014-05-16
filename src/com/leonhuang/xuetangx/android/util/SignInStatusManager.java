package com.leonhuang.xuetangx.android.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.LoginActivity;

public class SignInStatusManager {

	private Activity __activity;

	public SignInStatusManager(Activity activity) {
		__activity = activity;
	}

	public boolean checkSignInStatus(Object info) {
		if (null == info) {
			__activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(__activity, R.string.util_relogin_plz,
							Toast.LENGTH_SHORT).show();
				}
			});
			Intent intent = new Intent(__activity, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			__activity.startActivity(intent);
			return false;
		}
		return true;
	}
}
