package com.leonhuang.xuetangx.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.leonhuang.xuetangx.component.CourseListItem;
import com.leonhuang.xuetangx.component.CurrentCourseItem;
import com.leonhuang.xuetangx.component.User;
import com.leonhuang.xuetangx.webclient.HTTPClient;
import com.leonhuang.xuetangx.webclient.HTTPSClient;
import com.leonhuang.xuetangx.webclient.Param;
import com.leonhuang.xuetangx.webclient.Response;

public class XuetangX {
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	public final static String ROOT = "www.xuetangx.com";
	public final static String LOGIN_PAGE = HTTPS + ROOT + "/login";
	public final static String LOGIN_URL = HTTPS + ROOT + "/login_ajax";
	public final static String DASHBOARD = "/dashboard";
	public final static String COURSES = "/courses";
	
	public static String absPath(String path) {
		return HTTP + ROOT + path;
	}

	public static Response logIn(User user) throws MalformedURLException,
			IOException {
		HTTPSClient client = new HTTPSClient(user);
		client.get(new URL(LOGIN_PAGE)).getResponseCode();
		Param postdata = new Param();
		postdata.put("email", user.email());
		postdata.put("password", user.password());
		Response response = client.post(new URL(LOGIN_URL), postdata);
		return response;
	}

	public static boolean isLogIn(Response response) {
		@SuppressWarnings("unchecked")
		Map<String, Boolean> result = (Map<String, Boolean>) JSONValue
				.parse(response.getContent());
		return result.get("success");
	}

	public static User updateUserInfo(HTTPClient client)
			throws MalformedURLException, IOException {
		Response resp = client.get(new URL(HTTP + ROOT + DASHBOARD));
		String page = resp.getContent();
		Pattern nn_pattern = Pattern
				.compile("<h1 class=\"user-name\">([^<]*)</h1>");
		Matcher nn = nn_pattern.matcher(page);
		if (nn.find()) {
			client.setUserNickname(nn.group(1));
		}
		Pattern name_pattern = Pattern
				.compile("<span class=\"data\">([^<]*)</span>");
		Matcher name = name_pattern.matcher(page);
		if (name.find()) {
			client.setUserName(name.group(1));
		}

		return client.getUser();
	}

	public static ArrayList<CourseListItem> getAllCourses(HTTPClient client) throws MalformedURLException, IOException {
		Response resp = client.get(new URL(HTTP + ROOT + COURSES));
		String page = resp.getContent();
		ArrayList<CourseListItem> courses = new ArrayList<CourseListItem>();
		//TODO
		return courses;
	}

	public static ArrayList<CurrentCourseItem> getCurrentCourses(
			HTTPClient client) throws MalformedURLException, IOException {
		Response resp = client.get(new URL(HTTP + ROOT + DASHBOARD));
		String page = resp.getContent();
		ArrayList<CurrentCourseItem> courses = new ArrayList<CurrentCourseItem>();

		Document doc = Jsoup.parse(page);
		Elements my_courses = doc.getElementsByClass("my-course");
		for (Element course : my_courses) {
			Element img = course.child(0);
			boolean is_started = true;
			if (img.tagName().equals("div")) {
				is_started = false;
			}
			if (!is_started) {
				courses.add(getNotStartedCourseItem(course));
			} else {
				courses.add(getStartedCourseItem(course));
			}
		}

		return courses;
	}

	private static CurrentCourseItem getNotStartedCourseItem(Element course) {
		String img_path = course.getElementsByTag("img").first().attr("src");
		DateFormat df = new SimpleDateFormat("课程开始 - yyyy-mm-dd", Locale.CHINA);
		String raw_status = course.getElementsByClass("date-block").first()
				.ownText();
		Date start_date = null;
		try {
			start_date = df.parse(raw_status);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String university = course.getElementsByClass("university").first()
				.ownText();
		String[] id_title = course.getElementsByTag("h3").first()
				.getElementsByTag("span").first().ownText().split(" ");
		String id = id_title[0];
		String title = id_title[1];
		return CurrentCourseItem.NotStartedCourse(img_path, start_date,
				university, id, title);
	}

	private static CurrentCourseItem getStartedCourseItem(Element course) {
		String img_path = course.getElementsByTag("img").first().attr("src");
		String raw_status = course.getElementsByClass("date-block").first()
				.ownText();
		DateFormat df = new SimpleDateFormat("课程已开始 - yyyy-mm-dd", Locale.CHINA);
		Date start_date = null;
		try {
			start_date = df.parse(raw_status);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String university = course.getElementsByClass("university").first()
				.ownText();
		String[] id_title = course.getElementsByTag("h3").first()
				.getElementsByTag("a").first().ownText().split(" ");
		String id = id_title[0];
		String title = id_title[1];
		String path = course.getElementsByTag("h3").first()
				.getElementsByTag("a").first().attr("href");
		Element update = course.getElementsByClass("message-copy").first();
		String update_info = update.ownText();
		String update_date = update.child(2).ownText();
		return CurrentCourseItem.StartedCourse(img_path, start_date,
				university, id, title, path, update_info, update_date);
	}

	public static void main(String[] argv) throws MalformedURLException,
			IOException {
		Response resp = logIn(new User("xerxes.h.hiz@gmail.com",
				"wqe"));
		System.out.println(isLogIn(resp));
		HTTPClient client = resp.getClient();
		updateUserInfo(client);
		System.out.println(client.getUser().name());
		System.out.println(client.getUser().nickname());
		ArrayList<CurrentCourseItem> courses = getCurrentCourses(client);
		for (CurrentCourseItem course : courses) {
			System.out.println(course.getTitle());
		}
		updateUserInfo(client);
		System.out.println(client.getUser().name());
		System.out.println(client.getUser().nickname());
		courses = getCurrentCourses(client);
		for (CurrentCourseItem course : courses) {
			System.out.println(course.getTitle());
		}
	}
}
