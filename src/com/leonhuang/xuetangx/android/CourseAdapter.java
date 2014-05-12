package com.leonhuang.xuetangx.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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
import com.leonhuang.xuetangx.data.SimpleCourseInfo;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;

public class CourseAdapter extends ArrayAdapter<SimpleCourseInfo> {
	private LayoutInflater inflater;
	private ArrayList<SimpleCourseInfo> items;
	private Activity activity;

	public CourseAdapter(Activity activity,
			ArrayList<SimpleCourseInfo> mListItems) {
		super(activity, R.layout.row_course, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_course, parent, false);
		TextView title = (TextView) view.findViewById(R.id.tweetTitle);
		TextView text = (TextView) view.findViewById(R.id.tweetBrief);
		TextView date = (TextView) view.findViewById(R.id.tweetDate);
		TextView platform = (TextView) view.findViewById(R.id.tweetSource);
		final SimpleCourseInfo course = items.get(position);
		title.setText(course.getId() + " " + course.getTitle());
		if (course.getStatus() == SimpleCourseStatus.CURRENT) {
			text.setText(R.string.course_current);
		} else if (course.getStatus() == SimpleCourseStatus.UPCOMING) {
			text.setText(R.string.course_upcoming);
		} else {
			text.setText(R.string.course_past);
		}
		SimpleDateFormat df = new SimpleDateFormat(
				activity.getString(R.string.course_start_date_format),
				Locale.CHINA);
		date.setText(df.format(course.getStartDate().getTime()));
		new DownloadImageTask((ImageView) view.findViewById(R.id.tweetAvatar))
				.execute(course.getImgUrl());

		platform.setText(course.getUniversity());
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
			bmImage.setImageBitmap(result);
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
