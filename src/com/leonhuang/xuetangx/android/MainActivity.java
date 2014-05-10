package com.leonhuang.xuetangx.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.Student;
import com.leonhuang.xuetangx.android.asyntask.UpdateUserInfoTask;
import com.leonhuang.xuetangx.android.model.UserInfo;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UserInfo user = UserInfo.load(MainActivity.this);
		if (null != user) {
			tryLogin(user.getEmail(), user.getPassword());
		} else {
			MainActivity.this.startLoginActivity();
		}
	}

	private void tryLogin(final String email, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean success = Student.verify(email, password);
					if (!success) {
						throw new Exception(
								"Not real exception. Log in failed actually!");
					}
					UserInfo user = new UserInfo(email, password, "", "");
					user.save(MainActivity.this);
					new UpdateUserInfoTask(user, MainActivity.this,
							new Runnable() {

								@Override
								public void run() {
									MainActivity.this.startCourseListActivity();

								}
							}).execute();
				} catch (Exception e) {
					e.printStackTrace();
					MainActivity.this.startLoginActivity();
				}
			}
		}).start();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void startCourseListActivity() {
		Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
		startActivity(intent);
		finish();
	}

}
