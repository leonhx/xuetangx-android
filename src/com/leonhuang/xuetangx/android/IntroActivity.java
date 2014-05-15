package com.leonhuang.xuetangx.android;

import java.io.IOException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.Student;
import com.leonhuang.xuetangx.android.asyntask.UpdateUserInfoTask;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;

public class IntroActivity extends Activity {

	private View mLoginStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);

		UserInfo user = UserInfo.load(IntroActivity.this);
		if (null != user) {
			mLoginStatusView = findViewById(R.id.main_login_status);
			findViewById(R.id.main_msg_status);
			tryLogin(user.getEmail(), user.getPassword());
		} else {
			IntroActivity.this.startLoginActivity();
		}
	}

	private void tryLogin(final String email, final String password) {
		new UserLoginTask(email, password).execute();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private String email;
		private String password;

		public UserLoginTask(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			showProgress(true);

			if (!new NetworkConnectivityManager(IntroActivity.this)
					.isConnectingToInternet(true)) {
				return null;
			}

			try {
				boolean success = Student.verify(email, password);
				if (!success) {
					throw new IOException(
							"Not real exception. Log in failed actually!");
				}
			} catch (IOException e) {
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (null == success) {
				IntroActivity.this.showProgress(false);
				IntroActivity.this.startMainActivity();
				showProgress(false);
				return;
			}

			if (success) {
				new UpdateUserInfoTask(new UserInfo(email, password, "", ""),
						IntroActivity.this, new Runnable() {

							@Override
							public void run() {
								IntroActivity.this.startMainActivity();
								IntroActivity.this.showProgress(false);
							}
						}).execute();
			} else {
				IntroActivity.this.startLoginActivity();
				showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}

	private void startLoginActivity() {
		Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void startMainActivity() {
		Intent intent = new Intent(IntroActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}
