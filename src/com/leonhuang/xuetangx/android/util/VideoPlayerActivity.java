package com.leonhuang.xuetangx.android.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.leonhuang.xuetangx.R;

public class VideoPlayerActivity extends Activity {

	public static final String VIDEO_URI = "com.leonhuang.xuetangx.VideoPlayer.Intent.Uri";
	public static final String START_TIME = "com.leonhuang.xuetangx.VideoPlayer.Intent.StartTime";

	public static final String VIDEO_PLAYER_INFO = "com.leonhuang.xuetangx.VideoPlayer.Pref.Info";

	private VideoView __video;
	private MediaController __controller;
	private int __progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);

		Intent intent = getIntent();
		String videoUri = intent.getStringExtra(VIDEO_URI);
		__progress = intent.getIntExtra(START_TIME, 0);

		__video = (VideoView) findViewById(R.id.video_view);
		__controller = new MediaController(this);
		__controller.setAnchorView(__video);
		__video.setMediaController(__controller);
		__video.setKeepScreenOn(true);
		__video.setVideoPath(videoUri);
	}

	@Override
	protected void onResume() {
		__video.seekTo(betterProgress(__progress));
		__video.start();
		__video.requestFocus();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (__video.canPause()) {
			__video.pause();
		}
		__progress = __video.getCurrentPosition();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(START_TIME, __video.getCurrentPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int p = savedInstanceState.getInt(START_TIME, 0);
		if (0 != p) {
			__progress = p;
			__video.seekTo(betterProgress(__progress));
		}
	}
	
	private static int betterProgress(int msec) {
		return Math.max(msec - 3000, 0);
	}

}
