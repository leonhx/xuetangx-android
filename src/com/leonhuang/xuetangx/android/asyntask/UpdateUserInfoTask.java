package com.leonhuang.xuetangx.android.asyntask;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;

import com.leonhuang.xuetangx.Student;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.StudentInfo;

public class UpdateUserInfoTask extends AsyncTask<Void, Void, Void> {
	UserInfo user;
	Activity activity;
	Runnable postExecute;

	public UpdateUserInfoTask(UserInfo user, Activity activity,
			Runnable postExecute) {
		this.user = user;
		this.activity = activity;
		this.postExecute = postExecute;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			StudentInfo studentInfo = Student.info(user.getEmail(),
					user.getPassword());
			if (!new SignInStatusManager(activity)
					.checkSignInStatus(studentInfo)) {
				return null;
			}
			user.update(studentInfo.getName(), studentInfo.getNickname());
			user.save(this.activity);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		postExecute.run();
	}

}
