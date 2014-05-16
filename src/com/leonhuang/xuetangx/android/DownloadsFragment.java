package com.leonhuang.xuetangx.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leonhuang.xuetangx.R;

public class DownloadsFragment extends Fragment {
	
	public static final String DOWNLOAD_IDS = "com.leonhuang.xuetangx.download.IDs";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_downloads,
				container, false);

		return rootView;
	}

}
