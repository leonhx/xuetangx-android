package com.leonhuang.xuetangx.android;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class ChapterAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private ArrayList<SimpleChapterInfo> items;

	public ChapterAdapter(Activity activity,
			ArrayList<SimpleChapterInfo> mListItems) {
		inflater = activity.getWindow().getLayoutInflater();
		this.items = mListItems;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return items.get(groupPosition).getLectures().get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosititon) {
		return childPosititon;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_lecture, parent, false);
		TextView title = (TextView) view.findViewById(R.id.lecture_title);

		SimpleLectureInfo lecture = (SimpleLectureInfo) getChild(groupPosition,
				childPosition);
		title.setText(lecture.getTitle());

		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return items.get(groupPosition).getLectures().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return items.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_chapter, parent, false);
		TextView title = (TextView) view.findViewById(R.id.chapter_title);

		SimpleChapterInfo chapter = (SimpleChapterInfo) getGroup(groupPosition);
		title.setText(chapter.getTitle());

		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
