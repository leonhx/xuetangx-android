package com.leonhuang.xuetangx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leonhuang.xuetangx.component.User;
import com.leonhuang.xuetangx.parser.XuetangX;
import com.leonhuang.xuetangx.webclient.Response;

public class LoginActivity extends Activity {
	public static final String CLIENT_JSON = "com.leonhuang.xuetangx.LoginActivity.Client";

	private static final String USER_INFO = "com.leonhuang.xuetangx.LoginActivity.UserInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SharedPreferences sharedPref = getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		String clientJSON = sharedPref.getString("client", "");
		if (clientJSON.equals("")) {
			setContentView(R.layout.activity_login);
			Button btn = (Button) findViewById(R.id.login_button);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							EditText email_text = (EditText) findViewById(R.id.edit_email);
							EditText password_text = (EditText) findViewById(R.id.edit_password);
							String email = email_text.getText().toString();
							String password = password_text.getText().toString();
							try {
								Response resp = XuetangX.logIn(new User(email,
										password));
								if (!XuetangX.isLogIn(resp)) {
									throw new Exception(
											"Not really. Log in Failed!"+resp.getContent());
								}
								String clientJSON = resp.getClient().dumpJSON();
								SharedPreferences.Editor editor = sharedPref
										.edit();
								editor.putString("client", clientJSON);
								editor.commit();
								Intent intent = new Intent(LoginActivity.this,
										CourseListActivity.class);
								intent.putExtra(CLIENT_JSON, clientJSON);
								startActivity(intent);
								finish();
							} catch (Exception e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(
												LoginActivity.this,
												LoginActivity.this
														.getString(R.string.login_failed),
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}).start();
				}
			});
		} else {
			Intent intent = new Intent(this, CourseListActivity.class);
			intent.putExtra(CLIENT_JSON, clientJSON);
			startActivity(intent);
			finish();
		}
	}
}
