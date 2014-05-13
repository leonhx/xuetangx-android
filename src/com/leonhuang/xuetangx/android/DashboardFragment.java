package com.leonhuang.xuetangx.android;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleCourseStatus;

public class DashboardFragment extends Fragment {

	private DashboardPagerAdapter mAppSectionsPagerAdapter;
	private ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Dahsboard", "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_dashboard,
				container, false);

		PagerTabStrip pagerTabStrip = (PagerTabStrip) rootView
				.findViewById(R.id.pager_header);
		pagerTabStrip.setDrawFullUnderline(true);
		pagerTabStrip.setTabIndicatorColor(rootView.getResources().getColor(
				R.color.holo_blue_dark));

		Map<SimpleCourseStatus, String> courseTitleMap = new HashMap<SimpleCourseStatus, String>();
		courseTitleMap.put(SimpleCourseStatus.PAST, rootView.getResources()
				.getString(R.string.course_tab_past));
		courseTitleMap.put(SimpleCourseStatus.CURRENT, rootView.getResources()
				.getString(R.string.course_tab_current));
		courseTitleMap.put(SimpleCourseStatus.UPCOMING, rootView.getResources()
				.getString(R.string.course_tab_upcoming));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mAppSectionsPagerAdapter = new DashboardPagerAdapter(getActivity()
				.getSupportFragmentManager(), courseTitleMap);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);

		mViewPager.setCurrentItem(1);
		
		Log.i("Dahsboard", "onCreateView Finished");

		return rootView;
	}

}
