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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutShareUrlParam;

public class ChapterListActivity extends ListActivity {

	public static final String SIMPLE_COURSE_INFO = "com.leonhuang.xuetangx.android.CourseActivity.Intent.SimpleCourseInfo";
	public static final String COURSE_STATUS = "com.leonhuang.xuetangx.android.CourseActivity.Intent.CourseStatus";
	public static final String CACHE_COURSE_CHAPTERS = "com.leonhuang.xuetangx.android.CourseActivity.Cache.CourseChapters";

	private static final String RENREN_APP_ID = "267586";
	private static final String RENREN_API_KEY = "89137a23b30d4a9d9b1acd8c0faaba40";
	private static final String RENREN_SECRET_KEY = "7e611e75794e474a98f12c872d731ba5";

	private SimpleCourseInfo course;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<SimpleChapterInfo> mChapters = new ArrayList<SimpleChapterInfo>();
	private ListView listView;
	private ChapterAdapter adapter;

	private View mUnenrollStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_list);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		SimpleCourseStatus courseStatus = (SimpleCourseStatus) extra
				.getSerializable(COURSE_STATUS);
		try {
			course = SimpleCourseInfo.fromJSON(
					new JSONObject(extra.getString(SIMPLE_COURSE_INFO)),
					courseStatus);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		getActionBar().setTitle(course.getTitle());

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chapters_swipe_layout);
		mSwipeRefreshLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_orange_dark, R.color.holo_blue_bright,
				R.color.holo_red_dark);

		mUnenrollStatusView = findViewById(R.id.unenroll_status);

		listView = (ListView) mSwipeRefreshLayout
				.findViewById(android.R.id.list);
		adapter = new ChapterAdapter(this, mChapters);

		new GetContentTask(new Runnable() {

			@Override
			public void run() {
				listView.setAdapter(adapter);
			}
		}).execute();

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetContentTask(new Runnable() {

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
		Intent intent = new Intent(this, LectureListActivity.class);
		intent.putExtra(LectureListActivity.CHAPTER_NO, position);
		intent.putExtra(LectureListActivity.COURSE_CACHE_PATH,
				getStoragePath(course));
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.course, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("我正在#学堂在线#学习来自 ");
		commentBuilder.append(course.getUniversity());
		commentBuilder.append(" 的课程《");
		commentBuilder.append(course.getTitle());
		commentBuilder.append("》，来和我一起学习吧！");
		String comment = commentBuilder.toString();

		String cinfo = course.getCourseInfoUrl();
		if (cinfo.endsWith("info")) {
			cinfo = cinfo.substring(0, cinfo.length() - 4) + "about";
		}
		String url = cinfo;

		switch (item.getItemId()) {
		case R.id.action_share_renren:
			shareToRenren(comment, url);
			return true;
		case R.id.action_unenroll:
			showProgress(true);
			new TryUnenrollTask(this, new Runnable() {

				@Override
				public void run() {
					showProgress(false);
				}
			}).execute();
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class TryUnenrollTask extends AsyncTask<Void, Void, Boolean> {

		private Activity __activity;
		private Runnable __runOnPostExecute;

		public TryUnenrollTask(Activity activity, Runnable runOnPostExecute) {
			__activity = activity;
			__runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean success = false;

			if (!new NetworkConnectivityManager(__activity)
					.isConnectingToInternet(true)) {
				return null;
			}

			try {
				UserInfo user = UserInfo.load(__activity);
				success = Courses.unenroll(user.getEmail(), user.getPassword(),
						course);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {

			if (null == success) {
				return;
			}

			if (success) {
				__activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(ChapterListActivity.this,
								R.string.unenroll_succeed, Toast.LENGTH_SHORT)
								.show();

					}
				});
			} else {
				__activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(ChapterListActivity.this,
								R.string.unenroll_failed, Toast.LENGTH_SHORT)
								.show();

					}
				});
			}

			if (null != __runOnPostExecute) {
				__runOnPostExecute.run();
			}

			super.onPostExecute(success);
		}

	}

	private class GetContentTask extends
			AsyncTask<Void, Void, ArrayList<SimpleChapterInfo>> {

		private Runnable runOnPostExecute;

		public GetContentTask(Runnable runOnPostExecute) {
			this.runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected ArrayList<SimpleChapterInfo> doInBackground(Void... params) {
			mSwipeRefreshLayout.setRefreshing(true);

			ArrayList<SimpleChapterInfo> chapters = new ArrayList<SimpleChapterInfo>();

			if (!new NetworkConnectivityManager(ChapterListActivity.this)
					.isConnectingToInternet(false)) {
				chapters = loadChapters();
				return chapters;
			}

			try {
				Log.i("ChapterListTask", "Get From Internet");
				UserInfo user = UserInfo.load(ChapterListActivity.this);
				chapters = Courses.lectures(user.getEmail(),
						user.getPassword(), course);
				new SignInStatusManager(ChapterListActivity.this)
						.checkSignInStatus(chapters);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return chapters;
		}

		@Override
		protected void onPostExecute(ArrayList<SimpleChapterInfo> result) {

			if (null == result) {
				return;
			}

			if (result.isEmpty()) {
				return;
			}

			mChapters.clear();
			mChapters.addAll(result);
			saveChapters(result);

			if (null != runOnPostExecute) {
				runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

	private void saveChapters(ArrayList<SimpleChapterInfo> chapters) {
		String filename = getStoragePath(course);
		if (null != filename) {
			JSONArray coursesJSON = new JSONArray();
			for (SimpleChapterInfo chapter : chapters) {
				coursesJSON.put(chapter.toJSON());
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

	private ArrayList<SimpleChapterInfo> loadChapters() {
		ArrayList<SimpleChapterInfo> chapters = new ArrayList<SimpleChapterInfo>();

		String filename = getStoragePath(course);
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

	public static String getStoragePath(SimpleCourseInfo course) {
		return CACHE_COURSE_CHAPTERS + "."
				+ course.getCourseInfoUrl().replaceAll("/", "");
	}

	private void shareToRenren(final String comment, final String url) {
		final RennClient rennClient = RennClient
				.getInstance(ChapterListActivity.this);
		rennClient.init(RENREN_APP_ID, RENREN_API_KEY, RENREN_SECRET_KEY);
		rennClient.setScope("publish_share publish_feed");
		rennClient.setTokenType("mac");
		rennClient.setLoginListener(new LoginListener() {
			@Override
			public void onLoginSuccess() {
				PutShareUrlParam param = new PutShareUrlParam();
				param.setComment(comment);
				param.setUrl(url);
				try {
					rennClient.getRennService().sendAsynRequest(param,
							new CallBack() {
								@Override
								public void onSuccess(RennResponse response) {
									Toast.makeText(ChapterListActivity.this,
											R.string.share_succeed,
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onFailed(String errorCode,
										String errorMessage) {
									Toast.makeText(ChapterListActivity.this,
											R.string.share_failed,
											Toast.LENGTH_SHORT).show();
								}

							});
				} catch (RennException e) {
					Toast.makeText(ChapterListActivity.this,
							R.string.share_failed, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onLoginCanceled() {
				Toast.makeText(ChapterListActivity.this,
						getString(R.string.login_failed), Toast.LENGTH_SHORT)
						.show();
			}

		});

		rennClient.login(ChapterListActivity.this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mUnenrollStatusView.setVisibility(View.VISIBLE);
			mUnenrollStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mUnenrollStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			listView.setVisibility(View.VISIBLE);
			listView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							listView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mUnenrollStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			listView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
