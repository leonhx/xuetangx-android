package com.leonhuang.xuetangx.android.model;

import android.net.Uri;

public class DownloadInfo {
	private String title;
	private int status;
	private Uri path;
	private long id;
	private String url;

	public DownloadInfo(long id, String url, String title, int status, Uri path) {
		this.id = id;
		this.title = title;
		this.status = status;
		this.path = path;
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public int getStatus() {
		return status;
	}

	public Uri getPath() {
		return path;
	}

}
