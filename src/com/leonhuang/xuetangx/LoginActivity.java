package com.leonhuang.xuetangx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leonhuang.xuetangx.component.User;
import com.leonhuang.xuetangx.parser.XuetangX;
import com.leonhuang.xuetangx.webclient.Response;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		Button btn = (Button) findViewById(R.id.login_button);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EditText email_text = (EditText) findViewById(R.id.edit_email);
				EditText password_text = (EditText) findViewById(R.id.edit_password);
				String email = email_text.getText().toString();
				String password = password_text.getText().toString();
				tryLogin(new User(email, password));
			}
		});
	}

	private void tryLogin(final User user) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Response resp = XuetangX.logIn(user);
					if (!XuetangX.isLogIn(resp)) {
						throw new Exception("Not real exception. Log in failed actually! "
								+ resp.getContent());
					}
					String clientJSON = resp.getClient().dumpJSON();
					MainActivity.saveClientJSON(clientJSON, LoginActivity.this);
					LoginActivity.this.startCourseListActivity(clientJSON);
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
	
	private void startCourseListActivity(String clientJSON) {
		Intent intent = new Intent(LoginActivity.this,
				CourseListActivity.class);
		intent.putExtra(MainActivity.CLIENT_JSON, clientJSON);
		startActivity(intent);
		finish();
	}
}
