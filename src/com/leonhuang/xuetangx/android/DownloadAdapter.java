package com.leonhuang.xuetangx.android;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.leonhuang.xuetangx.R;
import com.leonhuang.xuetangx.android.model.DownloadInfo;

public class DownloadAdapter extends ArrayAdapter<DownloadInfo> {

	public static final String DOWNLOAD_IDS = "com.leonhuang.xuetangx.download.pref.IDs";

	private LayoutInflater inflater;
	private ArrayList<DownloadInfo> __ids;
	private Activity __activity;

	public DownloadAdapter(Activity activity, ArrayList<DownloadInfo> ids) {
		super(activity, R.layout.row_download, ids);
		inflater = activity.getWindow().getLayoutInflater();
		this.__ids = ids;
		this.__activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.row_download, parent, false);
		TextView title = (TextView) view.findViewById(R.id.download_title);
		TextView status = (TextView) view.findViewById(R.id.download_status);
		Button cancel = (Button) view.findViewById(R.id.download_cancel_button);

		final DownloadInfo download = __ids.get(position);
		final DownloadManager mgr = (DownloadManager) __activity
				.getSystemService(Context.DOWNLOAD_SERVICE);

		title.setText(download.getTitle());
		OnClickListener cancelListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				mgr.remove(download.getId());
				ItemAdapter.resetDownloadID(__activity, download.getUrl());
			}
		};
		OnClickListener removeListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				mgr.remove(download.getId());
				ItemAdapter.resetDownloadID(__activity, download.getUrl());
				File file = new File(download.getPath().getPath());
				file.delete();
			}
		};

		switch (download.getStatus()) {
		case DownloadManager.STATUS_PAUSED:
			status.setText(R.string.download_paused);
			cancel.setOnClickListener(cancelListener);
			break;
		case DownloadManager.STATUS_PENDING:
			status.setText(R.string.download_pending);
			cancel.setOnClickListener(cancelListener);
			break;
		case DownloadManager.STATUS_RUNNING:
			status.setText(R.string.download_in_progress);
			cancel.setOnClickListener(cancelListener);
			break;
		case DownloadManager.STATUS_SUCCESSFUL:
			status.setText(R.string.download_complete);
			cancel.setText(R.string.download_remove);
			cancel.setOnClickListener(removeListener);
			break;
		default:
			status.setText(R.string.download_wrong);
			cancel.setVisibility(Button.INVISIBLE);
			break;
		}

		return view;
	}

	@Override
	public int getCount() {
		return __ids.size();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
