package com.leonhuang.xuetangx.android;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class ItemAdapter extends ArrayAdapter<SimpleLectureInfo> {
	private LayoutInflater inflater;
	private ArrayList<SimpleLectureInfo> items;

	public ItemAdapter(Activity activity, ArrayList<SimpleLectureInfo> mListItems) {
		super(activity, R.layout.row_lecture, mListItems);
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_lecture, parent, false);
		TextView title = (TextView) view.findViewById(R.id.lecture_title);

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
