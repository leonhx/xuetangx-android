package com.leonhuang.xuetangx.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.ItemInfo;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class ItemListActivity extends ListActivity {

	public static final String CHAPTER_NO = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.ChapterNo";
	public static final String LECTURE_NO = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.LectureNo";
	public static final String COURSE_CACHE_PATH = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.CourseCachePath";

	private static final String CACHE_LECTURE_PATH = "com.leonhuang.xuetangx.android.ItemListActivity.Cache.Items";

	private SimpleLectureInfo lecture;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
	private ListView listView;
	private String courseCachePath;
	private ItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		int chap_position = extra.getInt(CHAPTER_NO);
		int lect_position = extra.getInt(LECTURE_NO);
		courseCachePath = extra.getString(COURSE_CACHE_PATH);
		lecture = loadChapters().get(chap_position).getLectures()
				.get(lect_position);

		getActionBar().setTitle(lecture.getTitle());
		getActionBar().setDisplayHomeAsUpEnabled(false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.items_swipe_layout);
		mSwipeRefreshLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_orange_dark, R.color.holo_blue_bright,
				R.color.holo_red_dark);

		listView = (ListView) mSwipeRefreshLayout
				.findViewById(android.R.id.list);
		adapter = new ItemAdapter(this, mItems);

		new GetItemsTask(new Runnable() {

			@Override
			public void run() {
				listView.setAdapter(adapter);
			}
		}).execute();

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetItemsTask(new Runnable() {

					@Override
					public void run() {
						adapter.notifyDataSetInvalidated();
					}
				}).execute();
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ItemInfo item = mItems.get(position);
		Intent intent;
		switch (item.getType()) {
		case PROBLEM:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item
					.getProblemPageUrl()));
			startActivity(intent);
			break;
		case VIDEO:
			intent = new Intent(ItemListActivity.this, WebViewActivity.class);
			Log.i("Video URL", item.getLowQualityVideoUrls()[0]);
			intent.putExtra(WebViewActivity.WEB_VIEW_URL,
					item.getLowQualityVideoUrls()[0]);
			startActivity(intent);
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	private class GetItemsTask extends
			AsyncTask<Void, Void, ArrayList<ItemInfo>> {

		private Runnable runOnPostExecute;

		public GetItemsTask(Runnable runOnPostExecute) {
			this.runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected ArrayList<ItemInfo> doInBackground(Void... params) {
			mSwipeRefreshLayout.setRefreshing(true);

			ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();

			if (!new NetworkConnectivityManager(ItemListActivity.this)
					.isConnectingToInternet(false)) {
				items = loadItems();
				return items;
			}

			try {
				Log.i("ChapterListTask", "Get From Internet");
				UserInfo user = UserInfo.load(ItemListActivity.this);
				items = Courses.lecture(user.getEmail(), user.getPassword(),
						lecture);
				new SignInStatusManager(ItemListActivity.this)
						.checkSignInStatus(items);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return items;
		}

		@Override
		protected void onPostExecute(ArrayList<ItemInfo> result) {

			new SignInStatusManager(ItemListActivity.this)
					.checkSignInStatus(result);

			if (result.isEmpty()) {
				return;
			}

			mItems.clear();
			mItems.addAll(result);
			saveItems(result);

			if (null != runOnPostExecute) {
				runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

	private void saveItems(ArrayList<ItemInfo> items) {
		String filename = getStoragePath(lecture);
		if (null != filename) {
			JSONArray coursesJSON = new JSONArray();
			for (ItemInfo item : items) {
				coursesJSON.put(item.toJSON());
			}

			try {
				FileOutputStream fos = openFileOutput(filename,
						Context.MODE_PRIVATE);
				fos.write(coursesJSON.toString().getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<ItemInfo> loadItems() {
		ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();

		String filename = getStoragePath(lecture);
		if (null != filename) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(openFileInput(filename)));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				JSONArray chaptersJSON = new JSONArray(sb.toString());
				for (int i = 0; i < chaptersJSON.length(); i++) {
					items.add(ItemInfo.fromJSON(chaptersJSON.getJSONObject(i)));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

	private ArrayList<SimpleChapterInfo> loadChapters() {
		ArrayList<SimpleChapterInfo> chapters = new ArrayList<SimpleChapterInfo>();

		String filename = courseCachePath;
		if (null != filename) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(openFileInput(filename)));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				JSONArray chaptersJSON = new JSONArray(sb.toString());
				for (int i = 0; i < chaptersJSON.length(); i++) {
					chapters.add(SimpleChapterInfo.fromJSON(chaptersJSON
							.getJSONObject(i)));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return chapters;
	}

	public static String getStoragePath(SimpleLectureInfo lecture) {
		return CACHE_LECTURE_PATH + "." + lecture.getUrl().replaceAll("/", "");
	}
}
