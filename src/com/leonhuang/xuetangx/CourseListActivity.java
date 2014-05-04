package com.leonhuang.xuetangx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.leonhuang.pulltorefresh.library.PullToRefreshBase;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.State;
import com.leonhuang.pulltorefresh.library.PullToRefreshListView;
import com.leonhuang.pulltorefresh.library.extras.SoundPullEventListener;
import com.leonhuang.xuetangx.component.CurrentCourseItem;
import com.leonhuang.xuetangx.parser.XuetangX;
import com.leonhuang.xuetangx.webclient.Client;
import com.leonhuang.xuetangx.webclient.HTTPClient;

public class CourseListActivity extends ListActivity {
	public static final String COURSE_URL = "com.leonhuang.xuetangx.CourseListActivity.CourseUrl";
	public static final String COURSE_LIST = "com.leonhuang.xuetangx.CourseListActivity.CourseList";

	public static HTTPClient client = null;

	private LinkedList<CurrentCourseItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CourseAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_list);

		if (null == client) {
			Intent intent = getIntent();
			client = (HTTPClient) Client.loadJSON(intent
					.getStringExtra(MainActivity.CLIENT_JSON));
			new UpdateUserInfoTask().execute(client);
		}

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

						new GetDataTask().execute(client);
					}
				});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		registerForContextMenu(actualListView);

		mListItems = new LinkedList<CurrentCourseItem>();
		mAdapter = new CourseAdapter(this, mListItems);
		new GetCoursesTask(mListItems, mAdapter, actualListView)
				.execute(client);

		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, CourseDetailActivity.class);
		intent.putExtra(COURSE_URL,
				XuetangX.absPath(mListItems.get(position - 1).getPath()));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
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
		case R.id.action_refresh:
			new GetDataTask().execute(client);
			mPullRefreshListView.setRefreshing(false);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return super.onOptionsItemSelected(item);
	}

	private class GetDataTask extends
			AsyncTask<HTTPClient, Void, ArrayList<CurrentCourseItem>> {

		@Override
		protected ArrayList<CurrentCourseItem> doInBackground(
				HTTPClient... clients) {
			ArrayList<CurrentCourseItem> courses = new ArrayList<CurrentCourseItem>();
			for (HTTPClient client : clients) {
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
			mListItems.removeAll(mListItems);
			mListItems.addAll(result);
			mAdapter.notifyDataSetChanged();
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

	private class GetCoursesTask extends
			AsyncTask<HTTPClient, Void, ArrayList<CurrentCourseItem>> {
		LinkedList<CurrentCourseItem> items;
		CourseAdapter mAdapter;
		ListView actualListView;

		public GetCoursesTask(LinkedList<CurrentCourseItem> items,
				CourseAdapter mAdapter, ListView actualListView) {
			this.items = items;
			this.mAdapter = mAdapter;
			this.actualListView = actualListView;
		}

		@Override
		protected ArrayList<CurrentCourseItem> doInBackground(
				HTTPClient... clients) {
			ArrayList<CurrentCourseItem> courses = new ArrayList<CurrentCourseItem>();
			for (HTTPClient client : clients) {
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
			actualListView.setAdapter(mAdapter);
		}

	}
}
