package com.leonhuang.xuetangx.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.UserInfo;

public class MainActivity extends FragmentActivity {

	public static final String INITIAL_FRAGMENT_NO = "com.leonhuang.xuetangx.MainActivity.InitialFragmentNo";
	public static final int FRAGMENT_DASHBOARD = 0;
	public static final int FRAGMENT_SEARCH = 1;
	// public static final int FRAGMENT_ACTION_VIEW_DOWNLOADS = 2;
	public static final int FRAGMENT_ACTION_FEEDBACK = 2;
	public static final int FRAGMENT_ABOUT = 3;
	public static final int FRAGMENT_ACTION_LOGOUT = 4;
	public static final int FRAGMENT_NUMBER = 5;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mFragmentTitles = new String[FRAGMENT_NUMBER];

	private boolean doubleBackToExitPressedOnce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFragmentTitles[FRAGMENT_DASHBOARD] = getString(R.string.drawer_dashboard);
		mFragmentTitles[FRAGMENT_SEARCH] = getString(R.string.drawer_search);
		// mFragmentTitles[FRAGMENT_ACTION_VIEW_DOWNLOADS] =
		// getString(R.string.drawer_downloads);
		mFragmentTitles[FRAGMENT_ACTION_FEEDBACK] = getString(R.string.drawer_feedback);
		mFragmentTitles[FRAGMENT_ABOUT] = getString(R.string.drawer_about);
		mFragmentTitles[FRAGMENT_ACTION_LOGOUT] = getString(R.string.drawer_logout);

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mFragmentTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			selectItem(intent.getIntExtra(INITIAL_FRAGMENT_NO,
					FRAGMENT_DASHBOARD));
		}

		// registerReceiver(new DownloadNotificationClickedReceiver(),
		// new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(new DownloadNotificationClickedReceiver());
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			android.os.Process.killProcess(android.os.Process.myPid());
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.click_twice_to_quit, Toast.LENGTH_SHORT)
				.show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Fragment fragment = null;
		Intent intent;

		switch (position) {
		case FRAGMENT_DASHBOARD:
			fragment = new DashboardFragment();
			break;
		case FRAGMENT_SEARCH:
			fragment = new SearchFragment();
			break;
		// case FRAGMENT_ACTION_VIEW_DOWNLOADS: // TODO
		// Uri startDir = Uri.fromFile(new File(Environment
		// .getExternalStorageDirectory().getAbsolutePath()
		// + "/../XuetangX/"));
		// intent = new Intent();
		// intent.setData(startDir);
		// intent.setType("*/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivity(intent);
		// startActivity(Intent.createChooser(intent,
		// getString(R.string.choose_file_manager)));
		// return;
		case FRAGMENT_ACTION_FEEDBACK:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "leon@njuopen.com" });
			intent.putExtra(Intent.EXTRA_SUBJECT, "[XuetangX FEEDBACK]");

			startActivity(Intent.createChooser(intent,
					getString(R.string.choose_mail_client)));
			return;
		case FRAGMENT_ABOUT:
			fragment = new AboutFragment();
			break;
		case FRAGMENT_ACTION_LOGOUT:
			new UserInfo("", "", "", "").save(MainActivity.this);
			intent = new Intent(MainActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			MainActivity.this.startActivity(intent);
			return;
		default:
			return;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mFragmentTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
