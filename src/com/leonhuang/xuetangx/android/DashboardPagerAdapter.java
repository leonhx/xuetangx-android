package com.leonhuang.xuetangx.android;

import java.util.Map;

import com.leonhuang.xuetangx.data.SimpleCourseStatus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DashboardPagerAdapter extends FragmentPagerAdapter {

	private Map<SimpleCourseStatus, String> courseTitleMap;

	public DashboardPagerAdapter(FragmentManager fm,
			Map<SimpleCourseStatus, String> map) {
		super(fm);
		this.courseTitleMap = map;
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new CourseListFragment();
		Bundle args = new Bundle();
		SimpleCourseStatus status = positionToStatus(i);

		if (null != status) {
			args.putSerializable(CourseListFragment.COURSE_STATUS, status);
		}

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		SimpleCourseStatus status = positionToStatus(position);
		if (null != status) {
			return courseTitleMap.get(status);
		} else {
			return "";
		}
	}

	private SimpleCourseStatus positionToStatus(int position) {
		switch (position) {
		case 0:
			return SimpleCourseStatus.PAST;
		case 1:
			return SimpleCourseStatus.CURRENT;
		case 2:
			return SimpleCourseStatus.UPCOMING;
		default:
			return null;
		}
	}

}
