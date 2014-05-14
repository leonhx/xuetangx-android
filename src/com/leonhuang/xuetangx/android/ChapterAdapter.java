package com.leonhuang.xuetangx.android;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;

public class ChapterAdapter extends ArrayAdapter<SimpleChapterInfo> {
	private LayoutInflater inflater;
	private ArrayList<SimpleChapterInfo> items;

	public ChapterAdapter(Activity activity,
			ArrayList<SimpleChapterInfo> mListItems) {
		super(activity, R.layout.row_chapter, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_chapter, parent, false);
		TextView title = (TextView) view.findViewById(R.id.chapter_title);

		title.setText(items.get(position).getTitle());

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
