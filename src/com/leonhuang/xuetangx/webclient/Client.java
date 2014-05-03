package com.leonhuang.xuetangx.webclient;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.leonhuang.xuetangx.component.User;

public abstract class Client {
	protected Pattern csrf_re = Pattern.compile("csrftoken=([^&=;]+);");
	protected Cookie cookie = new Cookie();
	protected User user = null;

	public Client(User user) {
		this.user = user;
	}

	public Client(Cookie cookie, User user) {
		this.cookie = cookie;
		this.user = user;
	}

	public static Client loadJSON(String json) {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) JSONValue.parse(json);
		User user = new User(map.get("user_email"), map.get("user_password"));
		Cookie cookie = Cookie.fromString(map.get("cookie"));
		if (map.get("type").equals("HTTP")) {
			HTTPClient client = new HTTPClient(cookie, user);
			return client;
		} else {
			HTTPSClient client = new HTTPSClient(cookie, user);
			return client;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String dumpJSON() {
		JSONObject obj = new JSONObject();
		obj.put("type", this.type());
		obj.put("user_email", this.user.email());
		obj.put("user_password", this.user.password());
		obj.put("cookie", this.cookie.toString());
		
		return obj.toJSONString();
	}

	public HTTPClient getHTTP() {
		return new HTTPClient(cookie, user);
	}

	public HTTPSClient getHTTPS() {
		return new HTTPSClient(cookie, user);
	}

	public void setUserName(String name) {
		this.user.setName(name);
	}

	public void setUserNickname(String nickname) {
		this.user.setNickname(nickname);
	}

	public User getUser() {
		return this.user;
	}

	public Cookie getCookie() {
		return this.cookie;
	}

	abstract public Response get(URL url) throws IOException;

	abstract public Response post(URL url, Param query) throws IOException;
	
	abstract public String type();
}
