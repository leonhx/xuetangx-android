package com.leonhuang.xuetangx.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.leonhuang.xuetangx.R;

public class CourseInfoActivity extends WebViewActivity {

	public static final String COURSE_INFO = "com.leonhuang.xuetangx.android.CourseInfoActivity.Intent.CourseInfo";

//	private CourseInfo __course;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Intent intent = getIntent();
		// Bundle extra = intent.getExtras();
		// try {
		// __course = CourseInfo.fromJSON(new JSONObject(extra
		// .getString(COURSE_INFO)));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// __course = null;
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.course_info, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_open_in_broswer:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(__url));
			startActivity(intent);
			return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
