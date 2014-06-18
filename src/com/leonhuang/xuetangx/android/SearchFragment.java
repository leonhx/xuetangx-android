package com.leonhuang.xuetangx.android;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.CourseInfo;

public class SearchFragment extends ListFragment {

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private SearchResultAdapter adapter;
	private ListView listView;
	private final ArrayList<CourseInfo> mCourses = new ArrayList<CourseInfo>();

	private Activity mActivity;
	private EditText searchField;
	private boolean searchable;

	private int nextOffset = 0;
	private int LIMIT = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search, container,
				false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.search_widget);
		mSwipeRefreshLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_orange_dark, R.color.holo_blue_bright,
				R.color.holo_red_dark);

		searchField = (EditText) rootView.findViewById(R.id.search_field);

		searchField
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null) {
							// if shift key is down, then we want to insert the
							// '\n' char in the TextView;
							// otherwise, the default action is to send the
							// message.
							if (!event.isShiftPressed()) {
								if (isPreparedForSending(actionId, event)) {
									sendMessage(false);
								}
								return true;
							}
							return false;
						}

						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE) {
							sendMessage(false);
							return true;
						}

						return false;
					}

					private boolean isPreparedForSending(int actionId,
							KeyEvent event) {
						return actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE
								|| (event != null
										&& event.getAction() == KeyEvent.ACTION_DOWN && event
										.getKeyCode() == KeyEvent.KEYCODE_ENTER);
					}
				});
		searchable = true;

		listView = (ListView) mSwipeRefreshLayout
				.findViewById(android.R.id.list);
		adapter = new SearchResultAdapter(getActivity(), mCourses);

		new GetSearchResultTask(new Runnable() {
			@Override
			public void run() {
				listView.setAdapter(adapter);
			}
		}, "", "", false, false, false).execute();

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (nextOffset < 0) {
					Toast.makeText(mActivity, R.string.search_no_more_result,
							Toast.LENGTH_SHORT).show();
					mSwipeRefreshLayout.setRefreshing(false);
				} else {
					sendMessage(true);
				}
			}
		});

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		CourseInfo course = mCourses.get(position);
		Intent intent = new Intent(mActivity, CourseInfoActivity.class);
		intent.putExtra(CourseInfoActivity.COURSE_INFO, course.toString());
		intent.putExtra(WebViewActivity.WEB_VIEW_URL,
				course.getCourseAboutUrl());
		startActivity(intent);

		super.onListItemClick(l, v, position, id);
	}

	private void sendMessage(boolean append) {
		final String query = searchField.getText().toString();
		if (searchable && null != query) {
			searchable = false;
			InputMethodManager imm = (InputMethodManager) mActivity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
			new GetSearchResultTask(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetInvalidated();
					searchable = true;
				}
			}, query, "", false, false, append).execute();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	private class GetSearchResultTask extends
			AsyncTask<Void, Void, ArrayList<CourseInfo>> {

		private Runnable __runOnPostExecute;
		private String __query;
		private String __cid;
		private boolean __started;
		private boolean __hasTA;
		private boolean __append;

		public GetSearchResultTask(Runnable runOnPostExecute, String query,
				String cid, boolean started, boolean hasTA, boolean append) {
			__runOnPostExecute = runOnPostExecute;
			__query = query;
			__cid = cid;
			__started = started;
			__hasTA = hasTA;
			__append = append;
		}

		@Override
		protected ArrayList<CourseInfo> doInBackground(Void... params) {
			mSwipeRefreshLayout.setRefreshing(true);

			ArrayList<CourseInfo> courses = new ArrayList<CourseInfo>();

			if (!new NetworkConnectivityManager(mActivity)
					.isConnectingToInternet(true)) {
				return null;
			}

			try {
				int offset = nextOffset;
				if (!__append) {
					offset = 0;
				}
				Pair<ArrayList<CourseInfo>, Integer> pair = Courses.search(
						__query, __cid, __started, __hasTA, offset, LIMIT);
				if (!new SignInStatusManager(mActivity).checkSignInStatus(pair)) {
					return null;
				}
				courses = pair.first;
				nextOffset = pair.second;
			} catch (IOException e) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity,
								mActivity.getString(R.string.err_server),
								Toast.LENGTH_SHORT).show();
					}
				});
				return null;
			}

			return courses;
		}

		@Override
		protected void onPostExecute(ArrayList<CourseInfo> result) {

			if (null != result) {
				if (!result.isEmpty()) {
					if (!__append) {
						mCourses.clear();
					}
					mCourses.addAll(0, result);
				} else {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mActivity,
									R.string.search_no_result,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}

			if (null != __runOnPostExecute) {
				__runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
