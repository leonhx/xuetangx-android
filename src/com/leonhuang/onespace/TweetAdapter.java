package com.leonhuang.onespace;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TweetAdapter extends ArrayAdapter {
	private LayoutInflater inflater;

    public TweetAdapter(Activity activity, String[] items) {
    	super(activity, R.layout.row_tweet, items);
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	return inflater.inflate(R.layout.row_tweet, parent, false);
    }
}