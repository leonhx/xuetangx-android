package com.leonhuang.xuetangx.android;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.DownloadInfo;

public class DownloadsFragment extends ListFragment {

	public static final String DOWNLOAD_IDS = "com.leonhuang.xuetangx.download.pref.IDs";

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<DownloadInfo> ids = new ArrayList<DownloadInfo>();
	private ListView listView;
	private DownloadAdapter adapter;

	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
				R.layout.fragment_downloads, container, false);

		mSwipeRefreshLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_orange_dark, R.color.holo_blue_bright,
				R.color.holo_red_dark);

		listView = (ListView) mSwipeRefreshLayout
				.findViewById(android.R.id.list);
		adapter = new DownloadAdapter(getActivity(), ids);

		new GetDownloadsTask(new Runnable() {
			@Override
			public void run() {
				listView.setAdapter(adapter);
			}
		}).execute();

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetDownloadsTask(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetInvalidated();
					}
				}).execute();
			}
		});

		return mSwipeRefreshLayout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	private class GetDownloadsTask extends
			AsyncTask<Void, Void, ArrayList<DownloadInfo>> {

		private Runnable runOnPostExecute;

		public GetDownloadsTask(Runnable runOnPostExecute) {
			this.runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected ArrayList<DownloadInfo> doInBackground(Void... params) {
			mSwipeRefreshLayout.setRefreshing(true);

			ArrayList<DownloadInfo> ids = new ArrayList<DownloadInfo>();

			SharedPreferences sharedPref = mActivity.getSharedPreferences(
					DOWNLOAD_IDS, Context.MODE_PRIVATE);
			@SuppressWarnings("unchecked")
			Map<String, Long> idsMap = (Map<String, Long>) sharedPref.getAll();

			DownloadManager mgr = (DownloadManager) mActivity
					.getSystemService(Context.DOWNLOAD_SERVICE);

			for (Entry<String, Long> download : idsMap.entrySet()) {
				long id = download.getValue();
				String url = download.getKey();
				DownloadManager.Query query = new Query();
				query.setFilterById(id);
				Cursor c = mgr.query(query);
				if (null != c) {
					int statusIdx = c
							.getColumnIndex(DownloadManager.COLUMN_STATUS);
					int titleIdx = c
							.getColumnIndex(DownloadManager.COLUMN_TITLE);
					int status = c.getInt(statusIdx);
					if (status != DownloadManager.STATUS_FAILED) {
						String title = c.getString(titleIdx);
						ids.add(new DownloadInfo(id, url, title, status, mgr
								.getUriForDownloadedFile(id)));
					}
					c.close();
				}
			}

			return ids;
		}

		@Override
		protected void onPostExecute(ArrayList<DownloadInfo> result) {

			if (null != result && !result.isEmpty()) {
				ids.clear();
				ids.addAll(result);
			}

			if (null != runOnPostExecute) {
				runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}
}
