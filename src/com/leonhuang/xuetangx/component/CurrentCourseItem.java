package com.leonhuang.xuetangx.component;

import java.util.Date;

public class CurrentCourseItem {
	private String img_path;
	private boolean is_started; // whether started
	private Date start_date;
	private String university;
	private String id;
	private String title;

	private String path = null;
	private String update_info = null;
	private String update_date = null;

	private CurrentCourseItem() {
	}

	public static CurrentCourseItem NotStartedCourse(String img_path, Date start_date,
			String university, String id, String title) {
		CurrentCourseItem self = new CurrentCourseItem();
		self.is_started = false;
		self.img_path = img_path;
		self.start_date = start_date;
		self.university = university;
		self.id = id;
		self.title = title;
		return self;
	}

	public static CurrentCourseItem StartedCourse(String img_path, Date start_date,
			String university, String id, String title, String path,
			String update_info, String update_date) {
		CurrentCourseItem self = NotStartedCourse(img_path, start_date,
				university, id, title);
		self.is_started = true;
		self.path = path;
		self.update_info = update_info;
		self.update_date = update_date;
		return self;
	}
	
	public String getImgPath() {
		return img_path;
	}

	public boolean isStarted() {
		return is_started;
	}

	public Date getStartDate() {
		return start_date;
	}

	public String getUniversity() {
		return university;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public String getUpdateInfo() {
		return update_info;
	}

	public String getUpdateDate() {
		return update_date;
	}
}
