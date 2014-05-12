package com.leonhuang.xuetangx.android.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.LoginActivity;
import com.leonhuang.xuetangx.data.BaseInfo;

public class SignInStatusManager {

	private Context __context;

	public SignInStatusManager(Context context) {
		__context = context;
	}

	public void checkSignInStatus(BaseInfo info) {
		if (null == info) {
			Toast.makeText(__context, R.string.util_relogin_plz,
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(__context, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			__context.startActivity(intent);
		}
	}
}
