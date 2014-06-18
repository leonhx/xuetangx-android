package com.leonhuang.xuetangx.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
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
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class ItemAdapter extends ArrayAdapter<ItemInfo> {

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
			OnClickListener showProblemListener = new OnClickListener() {
				@Override
				public void onClick(final View view) {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(item.getProblemPageUrl()));
					__activity.startActivity(intent);
				}
			};
			type.setOnClickListener(showProblemListener);
			title.setOnClickListener(showProblemListener);
		} else {
			download.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					download.setEnabled(false);
					new GetDownloadUrlTask(item, new OnPostExecuteRunnable() {
						@Override
						public void run(final String rawUrl,
								final String realUrl) {
							Runnable toDownload = new Runnable() {
								@Override
								public void run() {
									DownloadManager.Request request = new DownloadManager.Request(
											Uri.parse(realUrl));
									DownloadManager mgr = (DownloadManager) __activity
											.getSystemService(Context.DOWNLOAD_SERVICE);
									request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
											| DownloadManager.Request.NETWORK_MOBILE);
									request.setAllowedOverRoaming(false);
									request.setTitle(item.getTitle());
									request.setDescription(item.getTitle());
									request.setDestinationInExternalPublicDir(
											Environment.DIRECTORY_MOVIES,
											getVideoStorageDir(item, rawUrl));

									mgr.enqueue(request);
									Toast.makeText(__activity,
											R.string.download_enqueue_succeed,
											Toast.LENGTH_SHORT).show();
									__activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											download.setEnabled(true);
										}
									});
								}
							};

							File file = new File(
									Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
									getVideoStorageDir(item, rawUrl));
							if (file.exists()) {
								Log.i("Download", "exist");
								alert(toDownload);
							} else {
								toDownload.run();
							}

						}
					}, new OnPostExecuteRunnable() {
						@Override
						public void run(String rawUrl, String realUrl) {
							Toast.makeText(__activity,
									R.string.download_network_fail,
									Toast.LENGTH_SHORT).show();
							download.setEnabled(true);
						}
					}).execute();
				}
			});
			OnClickListener playVideoListener = new OnClickListener() {
				@Override
				public void onClick(final View view) {
					String path = null;
					for (String url : item.getVideoUrls()) {
						File file = new File(
								Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
								getVideoStorageDir(item, url));
						if (file.exists()) {
							path = file.getPath();
							break;
						}
					}

					if (null != path) {
						Log.i("Video Player", path);
						startVideoPlayer(__activity, path);
					} else {
						Log.i("Video Player", "Get From Internet");
						new GetDownloadUrlTask(item,
								new OnPostExecuteRunnable() {
									@Override
									public void run(String rawUrl,
											String realUrl) {
										startVideoPlayer(__activity, realUrl);
									}
								}, new OnPostExecuteRunnable() {
									@Override
									public void run(String rawUrl,
											String realUrl) {
										Toast.makeText(__activity,
												R.string.util_internet_inavail,
												Toast.LENGTH_SHORT).show();
									}
								}).execute();
					}
				}
			};
			type.setOnClickListener(playVideoListener);
			title.setOnClickListener(playVideoListener);
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

		private String __url;
		private OnPostExecuteRunnable __runIfNetworkFailed;
		private OnPostExecuteRunnable __runIfSucceed;

		public GetDownloadUrlTask(ItemInfo item,
				OnPostExecuteRunnable runIfSucceed,
				OnPostExecuteRunnable runIfFailed) {
			__url = item.getLowQualityVideoUrls()[0];
			__runIfSucceed = runIfSucceed;
			__runIfNetworkFailed = runIfFailed;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (!new NetworkConnectivityManager(__activity)
					.isConnectingToInternet(true)) {
				return null;
			}

			try {
				String url = Courses.videoUrl(__url);
				new SignInStatusManager(__activity).checkSignInStatus(url);
				return url;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String url) {
			if (null == url) {
				if (null != __runIfNetworkFailed) {
					__runIfNetworkFailed.run(__url, url);
				}
			} else {
				if (null != __runIfSucceed) {
					__runIfSucceed.run(__url, url);
				}
			}
		}

	}

	public static String getVideoStorageDir(ItemInfo item, String rawUrl) {
		StringBuilder sb = new StringBuilder("../XuetangX/video/");

		SimpleLectureInfo lecture = item.getSimpleLecture();
		SimpleChapterInfo chapter = lecture.getChapter();
		SimpleCourseInfo course = chapter.getCourse();

		sb.append(course.getTitle() + course.getId());
		sb.append("/");
		sb.append(lecture.getTitle());
		sb.append("/");

		String dir = sb.toString();
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
				dir);
		if (!file.exists() && !file.mkdirs()) {
			Log.e("Create File", "Create folders failed");
		} else {
			file = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
					dir + ".nomedia");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("Create File", "Create .nomedia failed");
				}
			}
		}

		sb.append(item.getTitle());
		return sb.toString();
	}

	public static void startVideoPlayer(Activity activity, String uri) {
		Intent intent = new Intent(activity, VideoPlayerActivity.class);
		intent.putExtra(VideoPlayerActivity.VIDEO_URI, uri);
		activity.startActivity(intent);
	}

	private void alert(final Runnable runIfYes) {
		__activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Builder builder = new AlertDialog.Builder(__activity);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(R.string.download_confirm);
				builder.setMessage(R.string.download_confirm_info);
				builder.setPositiveButton(R.string.download_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								runIfYes.run();
							}
						});
				builder.setNegativeButton(R.string.download_no, null);
				builder.show();
			}
		});
	}
}
