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
import android.widget.ListView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.data.ChapterInfo;
import com.leonhuang.xuetangx.data.LectureInfo;

public class ChapterActivity extends ListActivity {

	public static final String CHAPTER_NO = "com.leonhuang.xuetangx.android.ChapterActivity.Intent.ChapterNo";

	private ChapterInfo chapter;
	private ArrayList<LectureInfo> mLectures = new ArrayList<LectureInfo>();
	private ListView listView;
	private LectureAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		int position = extra.getInt(CHAPTER_NO);
		chapter = loadChapters().get(position);
		mLectures = chapter.getLectures();

		getActionBar().setTitle(chapter.getTitle());
		getActionBar().setDisplayHomeAsUpEnabled(false);

		listView = (ListView) findViewById(android.R.id.list);
		adapter = new LectureAdapter(this, mLectures);
		listView.setAdapter(adapter);
	}

	private ArrayList<ChapterInfo> loadChapters() {
		ArrayList<ChapterInfo> chapters = new ArrayList<ChapterInfo>();

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
					chapters.add(ChapterInfo.fromJSON(chaptersJSON
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
		return CourseActivity.CACHE_COURSE_CHAPTERS;
	}

}
