package com.leonhuang.xuetangx.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;

public class CourseListFragment extends Fragment {
	public static final String COURSE_STATUS = "com.leonhuang.xuetangx.android.CourseListFragment.CourseStatus";
	public static final String COURSE_UPCOMING = "upcoming";
	public static final String COURSE_CURRENT = "current";
	public static final String COURSE_PAST = "past";

	private SimpleCourseStatus courseStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_placeholder,
				container, false);

		Bundle args = getArguments();
		courseStatus = (SimpleCourseStatus) args.getSerializable(COURSE_STATUS);

		TextView text = (TextView) rootView.findViewById(R.id.placeholder_text);
		text.setText(String.valueOf(courseStatus));

		return rootView;
	}

}
