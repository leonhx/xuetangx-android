package com.leonhuang.xuetangx.component;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CourseListItem {
	private String status;
	private String path;
	private String img_path;
	private String title;
	private String teacher;
	private String id;
	private String brief;
	private boolean ta_status;
	private String update_before;

	public CourseListItem(String path, String img_path, String title,
			String teacher, String id, String brief, boolean ta_status,
			String status, String update_before) {
		this.path = path;
		this.img_path = img_path;
		this.title = title;
		this.teacher = teacher;
		this.id = id;
		this.brief = brief;
		this.ta_status = ta_status;
		this.status = status;
		this.update_before = update_before;
	}

	public static CourseListItem fromJSON(String json) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) JSONValue.parse(json);
		return new CourseListItem((String) map.get("path"),
				(String) map.get("img_path"), (String) map.get("title"),
				(String) map.get("teacher"), (String) map.get("id"),
				(String) map.get("brief"), (boolean) map.get("ta_status"),
				(String) map.get("status"), (String) map.get("update_before"));
	}

	@SuppressWarnings("unchecked")
	public String toJSON() {
		JSONObject json = new JSONObject();
		json.put("path", path);
		json.put("img_path", img_path);
		json.put("title", title);
		json.put("teacher", teacher);
		json.put("id", id);
		json.put("brief", brief);
		json.put("ta_status", ta_status);
		json.put("status", status);
		json.put("update_before", update_before);
		return json.toJSONString();
	}

	public String getPath() {
		return path;
	}

	public String getImgPath() {
		return img_path;
	}

	public String getTitle() {
		return title;
	}

	public String getTeacher() {
		return teacher;
	}

	public String getId() {
		return id;
	}

	public String getBrief() {
		return brief;
	}

	public boolean isTaOnline() {
		return ta_status;
	}

	public String getStatus() {
		return status;
	}

	public String getUpdateBefore() {
		return update_before;
	}

}
