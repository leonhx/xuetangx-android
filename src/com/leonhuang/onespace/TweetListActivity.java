package com.leonhuang.onespace;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.leonhuang.pulltorefresh.library.PullToRefreshBase;
import com.leonhuang.pulltorefresh.library.PullToRefreshListView;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.leonhuang.pulltorefresh.library.PullToRefreshBase.State;
import com.leonhuang.pulltorefresh.library.extras.SoundPullEventListener;

public class TweetListActivity extends ListActivity {
    static final int MENU_MANUAL_REFRESH = 0;

	private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private TweetAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_list);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				new GetDataTask().execute();
			}
		});

		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				Toast.makeText(TweetListActivity.this, getString(R.string.toast_end_of_list), Toast.LENGTH_SHORT).show();
			}
		});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		registerForContextMenu(actualListView);

		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mStrings));

		mAdapter = new TweetAdapter(this, mListItems);

		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);

		actualListView.setAdapter(mAdapter);
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
		menu.add(0, MENU_MANUAL_REFRESH, 0, R.string.action_refresh);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle("Item: " + getListView().getItemAtPosition(info.position));
		menu.add("Item 1");
		menu.add("Item 2");
		menu.add("Item 3");
		menu.add("Item 4");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case MENU_MANUAL_REFRESH:
				new GetDataTask().execute();
				mPullRefreshListView.setRefreshing(false);
				break;
			case R.id.action_post:
                openPost();
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

		return super.onOptionsItemSelected(item);
	}

	private void openPost() {
		Intent intent = new Intent(this, PostActivity.class);
	    startActivity(intent);
	}
    
    private void openFavorite() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show();
	}

	private void openSettings() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mListItems.addFirst("Added after refresh...");
			mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler" };

}
