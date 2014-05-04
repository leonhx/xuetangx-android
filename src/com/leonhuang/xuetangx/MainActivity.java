package com.leonhuang.xuetangx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.leonhuang.xuetangx.component.User;
import com.leonhuang.xuetangx.parser.XuetangX;
import com.leonhuang.xuetangx.webclient.Client;
import com.leonhuang.xuetangx.webclient.Response;

public class MainActivity extends Activity {
	public static final String CLIENT_JSON = "com.leonhuang.xuetangx.MainActivity.ClientJSON";
	public static final String USER_INFO = "com.leonhuang.xuetangx.LoginActivity.UserInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final String clientJSON = getClientJSON(MainActivity.this, "");
		if (!clientJSON.equals("")) {
			tryLogin(Client.loadJSON(clientJSON).getUser());
		} else {
			MainActivity.this.startLoginActivity();
		}
	}
	
	public static String getClientJSON(Activity activity, String defaultResult) {
		SharedPreferences sharedPref = activity.getSharedPreferences(
				USER_INFO, Context.MODE_PRIVATE);
		return sharedPref.getString(CLIENT_JSON, defaultResult);
	}
	
	public static void saveClientJSON(String clientJSON, Activity activity) {
		SharedPreferences sharedPref = activity.getSharedPreferences(
				USER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(CLIENT_JSON, clientJSON);
		editor.commit();
	}

	private void tryLogin(final User user) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Response resp = XuetangX.logIn(user);
					if (!XuetangX.isLogIn(resp)) {
						throw new Exception(
								"Not real exception. Log in failed actually! "
										+ resp.getContent());
					}
					String clientJSON = resp.getClient().dumpJSON();
					saveClientJSON(clientJSON, MainActivity.this);
					MainActivity.this.startCourseListActivity(clientJSON);
				} catch (Exception e) {
					e.printStackTrace();
					MainActivity.this.startLoginActivity();
				}
			}
		}).start();
	}
	
	private void startLoginActivity() {
		Intent intent = new Intent(MainActivity.this,
				LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void startCourseListActivity(String clientJSON) {
		Intent intent = new Intent(MainActivity.this,
				CourseListActivity.class);
		intent.putExtra(CLIENT_JSON, clientJSON);
		startActivity(intent);
		finish();
	}
}
