package com.leonhuang.xuetangx.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.SimpleChapterInfo;
import com.leonhuang.xuetangx.data.SimpleLectureInfo;

public class LectureListActivity extends ListActivity {

	public static final String CHAPTER_NO = "com.leonhuang.xuetangx.android.ChapterActivity.Intent.ChapterNo";
	public static final String COURSE_CACHE_PATH = "com.leonhuang.xuetangx.android.ChapterActivity.Intent.CourseCachePath";

	private SimpleChapterInfo chapter;
	private ArrayList<SimpleLectureInfo> mLectures = new ArrayList<SimpleLectureInfo>();
	private String courseCachePath;
	private ListView listView;
	private LectureAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture_list);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		int position = extra.getInt(CHAPTER_NO);
		courseCachePath = extra.getString(COURSE_CACHE_PATH);
		chapter = loadChapters().get(position);
		mLectures = chapter.getLectures();

		getActionBar().setTitle(chapter.getTitle());
		getActionBar().setDisplayHomeAsUpEnabled(false);

		listView = (ListView) findViewById(android.R.id.list);
		adapter = new LectureAdapter(this, mLectures);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Intent intent = new Intent(this, ChapterActivity.class);
		// intent.putExtra(ChapterActivity.CHAPTER_NO, position);
		// startActivity(intent);// TODO
		super.onListItemClick(l, v, position, id);
	}

	private ArrayList<SimpleChapterInfo> loadChapters() {
		ArrayList<SimpleChapterInfo> chapters = new ArrayList<SimpleChapterInfo>();

		String filename = getStoragePath();
		if (null != filename) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(openFileInput(filename)));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				JSONArray chaptersJSON = new JSONArray(sb.toString());
				for (int i = 0; i < chaptersJSON.length(); i++) {
					chapters.add(SimpleChapterInfo.fromJSON(chaptersJSON
							.getJSONObject(i)));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return chapters;
	}

	private String getStoragePath() {
		return courseCachePath;
	}

}
