package com.leonhuang.xuetangx.android.util;

import android.app.Activity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.leonhuang.xuetangx.R;

public class VideoPlayerActivity extends Activity {

	public static final String VIDEO_URI = "com.leonhuang.xuetangx.VideoPlayer.Intent.Uri";
	public static final String START_TIME = "com.leonhuang.xuetangx.VideoPlayer.Intent.StartTime";

	private VideoView __video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);

		String videoUri = getIntent().getStringExtra(VIDEO_URI);

		__video = (VideoView) findViewById(R.id.video_view);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(__video);
		__video.setMediaController(mediaController);
		__video.setKeepScreenOn(true);
		__video.setVideoPath(videoUri);

	}

	@Override
	protected void onStart() {
		__video.start();
		__video.requestFocus();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

}
