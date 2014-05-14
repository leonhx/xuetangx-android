package com.leonhuang.xuetangx.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.leonhuang.xuetangx.R;

public class WebViewActivity extends Activity {

	public static final String WEB_VIEW_URL = "com.leonhuang.xuetangx.android.WebViewActivity.Intent.URL";

	WebView webView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		url = extra.getString(WEB_VIEW_URL);

		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl(url);

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}
}
