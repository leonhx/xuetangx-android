package com.leonhuang.xuetangx.android;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leonhuang.xuetangx.Courses;
import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.android.util.SignInStatusManager;
import com.leonhuang.xuetangx.android.util.VideoPlayerActivity;
import com.leonhuang.xuetangx.data.ItemInfo;
import com.leonhuang.xuetangx.data.ItemType;

public class ItemAdapter extends ArrayAdapter<ItemInfo> {

	public static final String DOWNLOAD_IDS = "com.leonhuang.xuetangx.download.pref.IDs";

	private LayoutInflater inflater;
	private ArrayList<ItemInfo> items;
	private Activity __activity;

	public ItemAdapter(Activity activity, ArrayList<ItemInfo> mListItems) {
		super(activity, R.layout.row_item, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
		this.__activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_item, parent, false);
		TextView title = (TextView) view.findViewById(R.id.item_title);
		final ImageButton download = (ImageButton) view
				.findViewById(R.id.item_download_image_button);
		ImageButton type = (ImageButton) view
				.findViewById(R.id.item_type_image);

		final ItemInfo item = items.get(position);

		if (item.getType() == ItemType.PROBLEM) {
			type.setImageDrawable(view.getResources().getDrawable(
					R.drawable.ic_action_edit));
			download.setVisibility(ImageButton.INVISIBLE);
		} else {
			download.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					download.setEnabled(false);// TODO
					new GetDownloadUrlTask(item, new OnPostExecuteRunnable() {
						@Override
						public void run(String rawUrl, String realUrl) {
							DownloadManager.Request request = new DownloadManager.Request(
									Uri.parse(realUrl));
							DownloadManager mgr = (DownloadManager) __activity
									.getSystemService(Context.DOWNLOAD_SERVICE);
							request.setAllowedNetworkTypes(
									DownloadManager.Request.NETWORK_WIFI
											| DownloadManager.Request.NETWORK_MOBILE)
									.setAllowedOverRoaming(false)
									.setTitle(item.getTitle())
									.setDescription(item.getTitle())
									.setDestinationInExternalPublicDir(
											Environment.DIRECTORY_MOVIES,
											"." + item.getTitle()
													+ rawUrl.hashCode());

							Log.i("Download", realUrl);
							long id = mgr.enqueue(request);
							saveDownloadID(__activity, rawUrl, id);
						}
					}, new OnPostExecuteRunnable() {
						@Override
						public void run(String rawUrl, String realUrl) {
							Toast.makeText(__activity,
									R.string.download_network_fail,
									Toast.LENGTH_SHORT).show();
							__activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									download.setEnabled(true);
								}
							});
						}
					}).execute();
				}
			});
			type.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// TODO check whether download successfule before
					// if so: play local file
					// else: play online stream
					new GetDownloadUrlTask(item, new OnPostExecuteRunnable() {
						@Override
						public void run(String rawUrl, String realUrl) {
							Intent intent = new Intent(__activity,
									VideoPlayerActivity.class);
							intent.putExtra(VideoPlayerActivity.VIDEO_URI,
									realUrl);
							__activity.startActivity(intent);
						}
					}, new OnPostExecuteRunnable() {
						@Override
						public void run(String rawUrl, String realUrl) {
							Toast.makeText(__activity,
									R.string.util_internet_inavail,
									Toast.LENGTH_SHORT).show();
						}
					}).execute();
				}
			});
		}

		title.setText(item.getTitle());

		return view;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private interface OnPostExecuteRunnable {
		public void run(String rawUrl, String realUrl);
	}

	private class GetDownloadUrlTask extends AsyncTask<Void, Void, String> {

		private ItemInfo __item;
		private String __url;
		private OnPostExecuteRunnable __runIfNetworkFailed;
		private OnPostExecuteRunnable __runIfSucceed;

		public GetDownloadUrlTask(ItemInfo item,
				OnPostExecuteRunnable runIfSucceed,
				OnPostExecuteRunnable runIfFailed) {
			__item = item;
			__runIfSucceed = runIfSucceed;
			__runIfNetworkFailed = runIfFailed;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (!new NetworkConnectivityManager(__activity)
					.isConnectingToInternet(true)) {
				return null;
			}

			String url = null;
			try {
				Pair<String, String> pair = Courses.videoUrl(__item);
				if (!new SignInStatusManager(__activity)
						.checkSignInStatus(pair)) {
					return null;
				}
				__url = pair.first;
				url = pair.second;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return url;
		}

		@Override
		protected void onPostExecute(String url) {
			if (null == url) {
				if (null != __runIfNetworkFailed) {
					__runIfNetworkFailed.run(__url, url);
				}
			} else {
				__runIfSucceed.run(__url, url);
			}
		}

	}

	public static void saveDownloadID(Context context, String url, long id) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				DOWNLOAD_IDS, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		editor.putLong(url, id);
		editor.commit();
	}

	public static void resetDownloadID(Context context, String url) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				DOWNLOAD_IDS, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		editor.remove(url);
		editor.commit();
	}

	public static Long getDownloadID(Context context, String url) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				DOWNLOAD_IDS, Context.MODE_PRIVATE);
		return sharedPref.getLong(url, 0);
	}
}
