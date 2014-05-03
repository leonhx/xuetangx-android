package com.leonhuang.xuetangx.webclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cookie {
	Map<String, String> cookie = new HashMap<String, String>();

	public static Cookie fromString(String str) {
		Cookie self = new Cookie();
		for (String pair : str.split("; ")) {
			String[] kv = pair.split("=");
			if (2 == kv.length && !kv[0].equals("expires")
					&& !kv[0].equals("Max-Age") && !kv[0].equals("Path"))
				self.cookie.put(kv[0], kv[1]);
		}
		return self;
	}

	public void update(Cookie other) {
		for (String key : other.cookie.keySet()) {
			this.cookie.put(key, other.cookie.get(key));
		}
	}
	
	public String get(String key) {
		return cookie.get(key);
	}
	
	public boolean isEmpty() {
		return cookie.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = cookie.keySet();
		int length = keys.size(), i = 0;
		for (String key : keys) {
			sb.append(key);
			sb.append("=");
			sb.append(cookie.get(key));
			if (i < length - 1)
				sb.append("; ");
			i++;
		}
		return sb.toString();
	}

	public static void main(String[] argv) {
		Cookie c = Cookie
				.fromString("sessionid=3ce490dd1913795a5959f38205b9c370; httponly; Path=/");
		System.out.println(c);
		c.update(Cookie
				.fromString("csrftoken=Sl6Nxuz2bk3UCLOYaxhHXFtpZtVXeRqL; expires=Sat, 02-May-2015 06:05:13 GMT; Max-Age=31449600; Path=/"));
		System.out.println(c);
	}
}
