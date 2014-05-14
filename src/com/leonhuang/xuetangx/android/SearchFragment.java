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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.leonhuang.xuetangx.CourseInfoActivity;
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
									sendMessage();
								}
								return true;
							}
							return false;
						}

						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE) {
							sendMessage();
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
		}, "", "", false, false).execute();

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

	private void sendMessage() {
		final String query = searchField.getText().toString();
		if (searchable) {
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
			}, query, "", false, false).execute();
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

		public GetSearchResultTask(Runnable runOnPostExecute, String query,
				String cid, boolean started, boolean hasTA) {
			__runOnPostExecute = runOnPostExecute;
			__query = query;
			__cid = cid;
			__started = started;
			__hasTA = hasTA;
		}

		@Override
		protected ArrayList<CourseInfo> doInBackground(Void... params) {
			mSwipeRefreshLayout.setRefreshing(true);

			ArrayList<CourseInfo> courses = new ArrayList<CourseInfo>();

			if (!new NetworkConnectivityManager(mActivity)
					.isConnectingToInternet(true)) {
				return courses;
			}

			try {
				courses = Courses.search(__query, __cid, __started, __hasTA);
				new SignInStatusManager(mActivity).checkSignInStatus(courses);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return courses;
		}

		@Override
		protected void onPostExecute(ArrayList<CourseInfo> result) {

			if (null != result && !result.isEmpty()) {
				mCourses.clear();
				mCourses.addAll(result);
			}

			if (null != __runOnPostExecute) {
				__runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

}
