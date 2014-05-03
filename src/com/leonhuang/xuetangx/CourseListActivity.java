package com.leonhuang.xuetangx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.leonhuang.pulltorefresh.library.PullToRefreshBase;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.State;
import com.leonhuang.pulltorefresh.library.PullToRefreshListView;
import com.leonhuang.pulltorefresh.library.extras.SoundPullEventListener;
import com.leonhuang.xuetangx.component.CurrentCourseItem;
import com.leonhuang.xuetangx.parser.XuetangX;
import com.leonhuang.xuetangx.webclient.Client;
import com.leonhuang.xuetangx.webclient.HTTPClient;

public class CourseListActivity extends ListActivity {
	public static final String TWEET_CONTENT = "com.leonhuang.xuetangx.TweetListActivity.tweet_content";

	private static final int MENU_MANUAL_REFRESH = 0;

	private LinkedList<CurrentCourseItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CourseAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_list);

		Intent intent = getIntent();
		final HTTPClient client = (HTTPClient) Client.loadJSON(intent
				.getStringExtra(LoginActivity.CLIENT_JSON));

		new UpdateUserInfoTask().execute(client);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						new GetDataTask().execute();
					}
				});

		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						Toast.makeText(CourseListActivity.this,
								getString(R.string.toast_end_of_list),
								Toast.LENGTH_SHORT).show();
					}
				});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		registerForContextMenu(actualListView);
		Toast.makeText(this, "afsaf", Toast.LENGTH_SHORT).show();

		mListItems = new LinkedList<CurrentCourseItem>();		
		new GetCoursesTask(mListItems).execute(client);

		mAdapter = new CourseAdapter(this, mListItems);

		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);

		actualListView.setAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, CourseDetailActivity.class);
		// intent.putExtra(TWEET_CONTENT, mListItems.get(position));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		menu.add(0, MENU_MANUAL_REFRESH, 0, R.string.action_refresh);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle("Item: "
				+ getListView().getItemAtPosition(info.position));
		menu.add("Item 1");
		menu.add("Item 2");
		menu.add("Item 3");
		menu.add("Item 4");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_MANUAL_REFRESH:
			new GetDataTask().execute();
			mPullRefreshListView.setRefreshing(false);
			break;
		case R.id.action_post:
			openPost();
			return true;
		case R.id.action_favorite:
			openFavorite();
			return true;
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

		return super.onOptionsItemSelected(item);
	}

	private void openPost() {
		Intent intent = new Intent(this, PostActivity.class);
		startActivity(intent);
	}

	private void openFavorite() {
		Intent intent = new Intent(this, FavoActivity.class);
		startActivity(intent);
	}

	private void openSettings() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");
			mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private class UpdateUserInfoTask extends AsyncTask<HTTPClient, Void, Void> {

		@Override
		protected Void doInBackground(HTTPClient... clients) {
			try {
				for (HTTPClient client : clients)
					XuetangX.updateUserInfo(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class GetCoursesTask extends AsyncTask<HTTPClient, Void, ArrayList<CurrentCourseItem>> {
		LinkedList<CurrentCourseItem> items;
		
		public GetCoursesTask(LinkedList<CurrentCourseItem> items) {
			this.items = items;
		}

		@Override
		protected ArrayList<CurrentCourseItem> doInBackground(
				HTTPClient... clients) {
			ArrayList<CurrentCourseItem> courses = new ArrayList<CurrentCourseItem>();
			for (HTTPClient client: clients) {
				try {
					courses.addAll(XuetangX.getCurrentCourses(client));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return courses;
		}
		
		@Override
		protected void onPostExecute(ArrayList<CurrentCourseItem> result) {
			this.items.addAll(result);
		}
		
	}
}
