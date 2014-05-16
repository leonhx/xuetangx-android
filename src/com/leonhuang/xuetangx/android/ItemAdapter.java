package com.leonhuang.xuetangx.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.ItemInfo;
import com.leonhuang.xuetangx.data.ItemType;

public class ItemAdapter extends ArrayAdapter<ItemInfo> {

	public static int notification_id = 0;

	private LayoutInflater inflater;
	private ArrayList<ItemInfo> items;
	private Activity __activity;

	public ItemAdapter(Activity activity, ArrayList<ItemInfo> mListItems) {
		super(activity, R.layout.row_lecture, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
		this.__activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_item, parent, false);
		TextView title = (TextView) view.findViewById(R.id.item_title);
		ImageButton download = (ImageButton) view
				.findViewById(R.id.item_download_image_button);
		ImageView type = (ImageView) view.findViewById(R.id.item_type_image);

		final ItemInfo item = items.get(position);

		if (item.getType() == ItemType.PROBLEM) {
			type.setImageDrawable(view.getResources().getDrawable(
					R.drawable.ic_action_edit));
			download.setVisibility(ImageButton.INVISIBLE);
		} else {
			download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO
					final DownloadManager mgr = (DownloadManager) __activity
							.getSystemService(Context.DOWNLOAD_SERVICE);
					String url = item.getLowQualityVideoUrls()[0];
					DownloadManager.Request request = new DownloadManager.Request(
							Uri.parse(url));
					request.setAllowedNetworkTypes(
							DownloadManager.Request.NETWORK_WIFI
									| DownloadManager.Request.NETWORK_MOBILE)
							.setAllowedOverRoaming(false)
							.setTitle(item.getTitle())
							.setDescription(item.getTitle())
							.setDestinationInExternalPublicDir(
									Environment.DIRECTORY_DOWNLOADS,
									url.replaceAll("/", ""));
					Log.i("Download", url);
					long id = mgr.enqueue(request);
					SharedPreferences sharedPref = __activity
							.getSharedPreferences(
									DownloadsFragment.DOWNLOAD_IDS,
									Context.MODE_PRIVATE);
					Editor editor = sharedPref.edit();
					editor.putLong(url, id);
					editor.commit();

					final NotificationManager mNotifyManager = (NotificationManager) __activity
							.getSystemService(Context.NOTIFICATION_SERVICE);
					final Builder mBuilder = new NotificationCompat.Builder(
							__activity);
					mBuilder.setContentTitle(item.getTitle())
							.setContentText(
									__activity
											.getString(R.string.download_in_progress))
							.setSmallIcon(R.drawable.ic_action_download);

					final DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(id);

					final int notify_id = notification_id++;
					new Thread(new Runnable() {
						@Override
						public void run() {
							int incr = 0;
							while (incr <= 100) {
								Cursor c = mgr.query(query);
								if (null != c && c.moveToFirst()) {
									int downloadedIndex = c
											.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
									int totalIndex = c
											.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
									incr = 100 * c.getInt(downloadedIndex)
											/ c.getInt(totalIndex);

									Log.i("Download Progress", "" + incr);
									mBuilder.setProgress(100, incr, false);
									mNotifyManager.notify(notify_id,
											mBuilder.build());
								}

								if (null != c) {
									c.close();
								}

								try {
									Thread.sleep(5 * 1000);
								} catch (InterruptedException e) {
									Log.d("Download Progress", "sleep failure");
								}
							}
							mBuilder.setContentText(
									__activity
											.getString(R.string.download_complete))
									.setProgress(0, 0, false);
							mNotifyManager.notify(notify_id, mBuilder.build());
						}
					}).start();
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
}
