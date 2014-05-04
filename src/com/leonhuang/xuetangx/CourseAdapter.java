package com.leonhuang.xuetangx;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

import android.app.Activity;
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

import com.leonhuang.xuetangx.component.CurrentCourseItem;
import com.leonhuang.xuetangx.parser.XuetangX;

public class CourseAdapter extends ArrayAdapter<CurrentCourseItem> {
	private LayoutInflater inflater;
	private LinkedList<CurrentCourseItem> items;

	public CourseAdapter(Activity activity,
			LinkedList<CurrentCourseItem> mListItems) {
		super(activity, R.layout.row_course, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_course, parent, false);
		TextView title = (TextView) view.findViewById(R.id.tweetTitle);
		TextView text = (TextView) view.findViewById(R.id.tweetBrief);
		TextView date = (TextView) view.findViewById(R.id.tweetDate);
		TextView platform = (TextView) view.findViewById(R.id.tweetSource);
		final CurrentCourseItem course = items.get(position);
		title.setText(course.getTitle());
		if (course.isStarted()) {
			text.setText(course.getUpdateDate() + "更新："
					+ course.getUpdateInfo());
		} else {
			text.setText("此课程尚未开始");
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy年mm月dd日", Locale.CHINA);
		date.setText(df.format(course.getStartDate()));
		new DownloadImageTask((ImageView) view.findViewById(R.id.tweetAvatar))
				.execute(XuetangX.absPath(course.getImgPath()));

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
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
}