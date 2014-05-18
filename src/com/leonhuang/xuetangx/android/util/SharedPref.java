package com.leonhuang.xuetangx.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {
	public static final String DOWNLOAD_IDS = "com.leonhuang.xuetangx.download.pref.IDs";

	public static void saveKeyValuePair(Context context, String key,
			String value) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				DOWNLOAD_IDS, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void removeKeyValuePair(Context context, String key) {
		if (null != key) {
			SharedPreferences sharedPref = context.getSharedPreferences(
					DOWNLOAD_IDS, Context.MODE_PRIVATE);
			Editor editor = sharedPref.edit();
			editor.remove(key);
			editor.apply();
		}
	}

	public static String getValue(Context context, String key) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				DOWNLOAD_IDS, Context.MODE_PRIVATE);
		return sharedPref.getString(key, null);
	}
}
