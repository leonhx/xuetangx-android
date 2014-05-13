package com.leonhuang.xuetangx.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container,
				false);
		TextView note = (TextView) rootView.findViewById(R.id.text_note);
		TextView declaration = (TextView) rootView
				.findViewById(R.id.text_copyright);
		TextView author = (TextView) rootView.findViewById(R.id.text_author);
		TextView email = (TextView) rootView.findViewById(R.id.text_email);

		note.setText(R.string.about_note);
		declaration.setText(R.string.about_copyright);
		author.setText(R.string.about_author);
		email.setText(R.string.about_author_email);

		return rootView;
	}

}
