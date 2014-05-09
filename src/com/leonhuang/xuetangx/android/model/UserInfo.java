package com.leonhuang.xuetangx.android.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {
	public static final String USER_INFO = "com.leonhuang.xuetangx.android.model.UserInfo";
	public static final String USER_EMAIL = "com.leonhuang.xuetangx.android.model.UserInfo.UserEmail";
	public static final String USER_PASSWORD = "com.leonhuang.xuetangx.android.model.UserInfo.UserPassword";
	public static final String USER_NAME = "com.leonhuang.xuetangx.android.model.UserInfo.UserName";
	public static final String USER_NICKNAME = "com.leonhuang.xuetangx.android.model.UserInfo.UserNickname";

	private String email;
	private String password;
	private String name;
	private String nickname;

	public UserInfo(String email, String password, String name, String nickname) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
	}

	public static UserInfo load(Activity activity) {
		SharedPreferences sharedPref = activity.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		String email = sharedPref.getString(USER_EMAIL, null);
		String password = sharedPref.getString(USER_PASSWORD, null);
		String name = sharedPref.getString(USER_NAME, "");
		String nickname = sharedPref.getString(USER_NICKNAME, "");

		if (null == email || null == password || email.isEmpty()
				|| password.isEmpty()) {
			return null;
		} else {
			return new UserInfo(email, password, name, nickname);
		}
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getNickname() {
		return nickname;
	}
	
	public void update(String name, String nickname) {
		this.name = name;
		this.nickname = nickname;
	}

	public void save(Activity activity) {
		SharedPreferences sharedPref = activity.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(USER_EMAIL, this.email);
		editor.putString(USER_PASSWORD, this.password);
		editor.putString(USER_NAME, this.name);
		editor.putString(USER_NICKNAME, this.nickname);
		editor.commit();
	}

}
