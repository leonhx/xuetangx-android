package com.leonhuang.xuetangx.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.util.NetworkConnectivityManager;
import com.leonhuang.xuetangx.data.CourseInfo;

public class SearchResultAdapter extends ArrayAdapter<CourseInfo> {
	private LayoutInflater inflater;
	private ArrayList<CourseInfo> items;
	private Activity activity;

	public SearchResultAdapter(Activity activity,
			ArrayList<CourseInfo> mListItems) {
		super(activity, R.layout.row_search_result, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_search_result, parent, false);
		ImageView image = (ImageView) view.findViewById(R.id.course_image);
		TextView teacher = (TextView) view.findViewById(R.id.course_teacher);
		TextView title_univ_id = (TextView) view
				.findViewById(R.id.course_title_univ_id);
		TextView subtitle = (TextView) view.findViewById(R.id.course_subtitle);

		CourseInfo course = items.get(position);

		new DownloadImageTask(image).execute(course.getImgUrl());
		teacher.setText(course.getTeacher().getTitle() + " "
				+ course.getTeacher().getName());
		title_univ_id.setText(course.getTitle() + " " + course.getUniversity()
				+ " " + course.getId());
		subtitle.setText(course.getSubtitle());

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

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			try {
				return BitmapFactory.decodeStream(activity
						.openFileInput(urldisplay.replaceAll("/", "")));
			} catch (FileNotFoundException e1) {
				if (!new NetworkConnectivityManager(activity)
						.isConnectingViaWifiOrWiMAX()) {
					Log.i("Network", "Using Mobile Data");
					return null;
				}

				Bitmap mIcon11 = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				storeImage(mIcon11, urldisplay.replaceAll("/", ""));

				return mIcon11;
			}
		}

		protected void onPostExecute(Bitmap result) {
			if (null != result) {
				bmImage.setImageBitmap(result);
			} else {
				bmImage.setImageDrawable(activity.getResources().getDrawable(
						R.drawable.placeholder));
			}
		}
	}

	private void storeImage(Bitmap image, String path) {
		final String TAG = "Store Image";
		try {
			FileOutputStream fos = activity.openFileOutput(path,
					Context.MODE_PRIVATE);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}
}
