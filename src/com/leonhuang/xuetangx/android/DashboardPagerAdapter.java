package com.leonhuang.xuetangx.android;

import com.leonhuang.xuetangx.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardPagerAdapter extends FragmentPagerAdapter {

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            default:
                // The other sections of the app are dummy placeholders.
                Fragment fragment = new PlaceholderFragment();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Section " + (position + 1);
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	
    	public static final String ARG_PLANET_NUMBER = "planet_number";

    	public PlaceholderFragment() {
    	}

    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		View rootView = inflater.inflate(R.layout.fragment_placeholder,
    				container, false);
    		return rootView;
    	}
    }
}
