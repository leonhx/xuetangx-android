package com.leonhuang.xuetangx.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.ItemInfo;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class ItemListActivity extends ListActivity {

	public static final String SIMPLE_COURSE_INFO = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.SimpleCourseInfo";
	public static final String COURSE_STATUS = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.CourseStatus";
	public static final String SIMPLE_CHAPTER_INFO = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.SimpleChapterInfo";
	public static final String SIMPLE_LECTURE_INFO = "com.leonhuang.xuetangx.android.ItemListActivity.Intent.SimpleLectureInfo";

	private static final String CACHE_LECTURE_PATH = "com.leonhuang.xuetangx.android.ItemListActivity.Cache.Items";

	private SimpleLectureInfo lecture;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
	private ListView listView;
	private ItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		try {
			SimpleCourseInfo course = SimpleCourseInfo.fromJSON(new JSONObject(
					extra.getString(SIMPLE_COURSE_INFO)),
					(SimpleCourseStatus) extra.getSerializable(COURSE_STATUS));
			SimpleChapterInfo chapter = SimpleChapterInfo.fromJSON(
					new JSONObject(extra.getString(SIMPLE_CHAPTER_INFO)),
					course);
			lecture = SimpleLectureInfo.fromJSON(
					new JSONObject(extra.getString(SIMPLE_LECTURE_INFO)),
					chapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
					.isConnectingToInternet(true)) {
				items = loadItems();
				return items;
			}

			try {
				Log.i("ItemListTask", "Get From Internet");
				UserInfo user = UserInfo.load(ItemListActivity.this);
				items = Courses.lecture(user.getEmail(), user.getPassword(),
						lecture);
				if (!new SignInStatusManager(ItemListActivity.this)
						.checkSignInStatus(items)) {
					return null;
				}
			} catch (IOException e) {
				ItemListActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								ItemListActivity.this,
								ItemListActivity.this
										.getString(R.string.err_server),
								Toast.LENGTH_SHORT).show();
					}
				});
				e.printStackTrace();
			}

			return items;
		}

		@Override
		protected void onPostExecute(ArrayList<ItemInfo> result) {

			if (null != result) {
				if (result.isEmpty()) {
					ItemListActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
									ItemListActivity.this,
									ItemListActivity.this
											.getString(R.string.empty_item_list),
									Toast.LENGTH_SHORT).show();
						}
					});
				}
				mItems.clear();
				mItems.addAll(result);
				saveItems(result);
			}

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
					items.add(ItemInfo.fromJSON(chaptersJSON.getJSONObject(i),
							lecture));
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

	public static String getStoragePath(SimpleLectureInfo lecture) {
		return CACHE_LECTURE_PATH + "." + lecture.getUrl().replaceAll("/", "");
	}
}
