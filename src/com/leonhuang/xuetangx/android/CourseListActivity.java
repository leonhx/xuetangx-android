package com.leonhuang.xuetangx.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.ListActivity;
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
import android.widget.Toast;

import com.leonhuang.pulltorefresh.library.PullToRefreshBase;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.State;
import com.leonhuang.pulltorefresh.library.PullToRefreshListView;
import com.leonhuang.pulltorefresh.library.extras.SoundPullEventListener;
import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutShareUrlParam;

public class CourseListActivity extends ListActivity {
	public static final String COURSE_URL = "com.leonhuang.xuetangx.CourseListActivity.CourseUrl";

	private LinkedList<SimpleCourseInfo> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CourseAdapter mAdapter;

	private static final String RENREN_APP_ID = "267586";
	private static final String RENREN_API_KEY = "89137a23b30d4a9d9b1acd8c0faaba40";
	private static final String RENREN_SECRET_KEY = "7e611e75794e474a98f12c872d731ba5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_list);

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

						new GetDataTask(null).execute();
					}
				});

		mListItems = new LinkedList<SimpleCourseInfo>();
		mAdapter = new CourseAdapter(this, mListItems);
		new GetDataTask(new Runnable() {

			@Override
			public void run() {
				ListView actualListView = mPullRefreshListView
						.getRefreshableView();
				registerForContextMenu(actualListView);
				actualListView.setAdapter(mAdapter);

			}
		}).execute();

		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO
		// CurrentCourseItem course = mListItems.get(position - 1);
		// String url = mListItems.get(position - 1).getPath();
		// if (!course.isStarted()) {
		// url = "/courses/" + course.getUniversity() + "/" + course.getId()
		// + "/_/about";
		// }
		// Intent intent = new Intent(this, CourseDetailActivity.class);
		// intent.putExtra(COURSE_URL, XuetangX.absPath(url));
		// startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_refresh:
			new GetDataTask(null).execute();
			mPullRefreshListView.setRefreshing(false);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("分享课程到");
		menu.add(0, 0, 0, "人人");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int position = info.position;

		switch (item.getItemId()) {
		case 0:
			SimpleCourseInfo course = mListItems.get(position - 1);
			StringBuilder commentBuilder = new StringBuilder();
			commentBuilder.append("我正在#学堂在线#学习来自 ");
			commentBuilder.append(course.getUniversity());
			commentBuilder.append(" 的课程《");
			commentBuilder.append(course.getTitle());
			commentBuilder.append("》，来和我一起学习吧！");
			final String comment = commentBuilder.toString();
			String cinfo = course.getCourseInfoUrl();
			if (cinfo.endsWith("info")) {
				cinfo = cinfo.substring(0, cinfo.length() - 4) + "about";
			}
			final String url = cinfo;

			final RennClient rennClient = RennClient.getInstance(this);
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
										Toast.makeText(CourseListActivity.this,
												"分享成功", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void onFailed(String errorCode,
											String errorMessage) {
										Toast.makeText(CourseListActivity.this,
												"分享失败", Toast.LENGTH_SHORT)
												.show();
									}

								});
					} catch (RennException e) {
						Toast.makeText(CourseListActivity.this, "分享失败",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}

				@Override
				public void onLoginCanceled() {
					Toast.makeText(CourseListActivity.this,
							getString(R.string.login_failed),
							Toast.LENGTH_SHORT).show();
				}

			});
			rennClient.login(this);
			break;
		default:
			break;
		}

		return false;
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, ArrayList<SimpleCourseInfo>> {

		private Runnable runOnPostExecute;

		public GetDataTask(Runnable runOnPostExecute) {
			this.runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected ArrayList<SimpleCourseInfo> doInBackground(Void... params) {
			try {
				UserInfo user = UserInfo.load(CourseListActivity.this);
				return Courses.selected(user.getEmail(), user.getPassword());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ArrayList<SimpleCourseInfo>();
		}

		@Override
		protected void onPostExecute(ArrayList<SimpleCourseInfo> result) {
			mListItems.removeAll(mListItems);
			mListItems.addAll(result);

			if (null != runOnPostExecute) {
				runOnPostExecute.run();
			}

			mAdapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}

	}

}
