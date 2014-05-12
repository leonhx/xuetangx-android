package com.leonhuang.xuetangx.android;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leonhuang.xuetangx.R;

public class DashboardFragment extends Fragment implements
		ActionBar.TabListener {

	private DashboardPagerAdapter mAppSectionsPagerAdapter;

	private ViewPager mViewPager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard,
				container, false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mAppSectionsPagerAdapter = new DashboardPagerAdapter(getActivity()
				.getSupportFragmentManager());

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		
		mViewPager.setCurrentItem(1);

		return rootView;
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

}
