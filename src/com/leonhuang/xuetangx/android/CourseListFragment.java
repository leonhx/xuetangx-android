package com.leonhuang.xuetangx.android;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutShareUrlParam;

public class CourseListFragment extends ListFragment {
	public static final String COURSE_STATUS = "com.leonhuang.xuetangx.android.CourseListFragment.CourseStatus";

	private static final String RENREN_APP_ID = "267586";
	private static final String RENREN_API_KEY = "89137a23b30d4a9d9b1acd8c0faaba40";
	private static final String RENREN_SECRET_KEY = "7e611e75794e474a98f12c872d731ba5";

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private final ArrayList<SimpleCourseInfo> mListItems = new ArrayList<SimpleCourseInfo>();
	private SimpleCourseStatus courseStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		courseStatus = (SimpleCourseStatus) args.getSerializable(COURSE_STATUS);

		mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
				R.layout.fragment_course_list, container, false);
		mSwipeRefreshLayout.setColorScheme(R.color.holo_green_dark,
				R.color.holo_orange_dark, R.color.holo_blue_bright,
				R.color.holo_red_dark);

		final ListView listView = (ListView) mSwipeRefreshLayout
				.findViewById(android.R.id.list);
		final CourseAdapter adapter = new CourseAdapter(getActivity(),
				mListItems);

		new GetDataTask(new Runnable() {

			@Override
			public void run() {
				listView.setAdapter(adapter);

			}
		}).execute();

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.e(getClass().getSimpleName(), "refresh");
				new GetDataTask(new Runnable() {

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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(mSwipeRefreshLayout.getResources().getString(
				R.string.course_share_to));
		menu.add(
				0,
				0,
				0,
				mSwipeRefreshLayout.getResources().getString(
						R.string.share_renren));

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case 0:
			shareToRenren(info.position);
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	private void shareToRenren(int position) {
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

		final RennClient rennClient = RennClient.getInstance(getActivity());
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
									Toast.makeText(getActivity(), "分享成功",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onFailed(String errorCode,
										String errorMessage) {
									Toast.makeText(getActivity(), "分享失败",
											Toast.LENGTH_SHORT).show();
								}

							});
				} catch (RennException e) {
					Toast.makeText(getActivity(), "分享失败", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				}
			}

			@Override
			public void onLoginCanceled() {
				Toast.makeText(getActivity(), getString(R.string.login_failed),
						Toast.LENGTH_SHORT).show();
			}

		});

		rennClient.login(getActivity());
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, ArrayList<SimpleCourseInfo>> {

		private Runnable runOnPostExecute;

		public GetDataTask(Runnable runOnPostExecute) {
			this.runOnPostExecute = runOnPostExecute;
		}

		@Override
		protected ArrayList<SimpleCourseInfo> doInBackground(Void... params) {
			if (!new NetworkConnectivityManager(getActivity())
					.isConnectingToInternet()) {
				return null;
			}
			try {
				UserInfo user = UserInfo.load(getActivity());
				ArrayList<SimpleCourseInfo> courses;
				switch (courseStatus) {
				case PAST:
					courses = Courses.past(user.getEmail(), user.getPassword());
					break;
				case CURRENT:
					courses = Courses.current(user.getEmail(),
							user.getPassword());
					break;
				case UPCOMING:
					courses = Courses.upcoming(user.getEmail(),
							user.getPassword());
					break;
				default:
					courses = new ArrayList<SimpleCourseInfo>();
				}
				new SignInStatusManager(getActivity())
						.checkSignInStatus(courses);
				return courses;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ArrayList<SimpleCourseInfo>();
		}

		@Override
		protected void onPostExecute(ArrayList<SimpleCourseInfo> result) {

			if (null == result) {
				return;
			}

			mListItems.removeAll(mListItems);
			mListItems.addAll(result);

			if (null != runOnPostExecute) {
				runOnPostExecute.run();
			}

			super.onPostExecute(result);
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

}
