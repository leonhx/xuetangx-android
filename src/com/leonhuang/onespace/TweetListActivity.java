package com.leonhuang.onespace;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TweetListActivity extends ListActivity {
    private ArrayAdapter<String> tweetItemArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_list);

        tweetItemArrayAdapter = new TweetAdapter(this, new String[10]);
        setListAdapter(tweetItemArrayAdapter);
	}
	
	@Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
		 Intent intent = new Intent(this, TweetDetailActivity.class);
	     startActivity(intent);
	 }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                openPost();
                return true;
            case R.id.action_refresh:
                refreshNow();
                return true;
            case R.id.action_favorite:
            	openFavorite();
            	return true;
            case R.id.action_settings:
            	openSettings();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }                
    }

	private void openPost() {
		Intent intent = new Intent(this, PostActivity.class);
	    startActivity(intent);
	}
    
    private void refreshNow() {
		// TODO Auto-generated method stub
    	Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
	}

	private void openFavorite() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show();
	}

	private void openSettings() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
	}

}
