package com.leonhuang.onespace;

import java.util.LinkedList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<String> {
	private LayoutInflater inflater;
	private LinkedList<String> items;

    public TweetAdapter(Activity activity, LinkedList<String> items) {
    	super(activity, R.layout.row_tweet, items);
        inflater = activity.getWindow().getLayoutInflater();
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View view = inflater.inflate(R.layout.row_tweet, parent, false);
    	Log.i("0", view.toString());
    	TextView text = (TextView) view.findViewById(R.id.tweetBrief); 
    	Log.i("0", text.getText().toString());
    	text.setText(items.get(position));
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