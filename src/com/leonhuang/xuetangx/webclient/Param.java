package com.leonhuang.xuetangx.webclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Param {
	Map<String, String> params = new HashMap<String, String>();

	public void put(String key, String value) {
		params.put(key, value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0, length = params.size();
		for (String key: params.keySet()) {
			try {
				sb.append(URLEncoder.encode(key, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(params.get(key), "UTF-8"));
				if (i < length - 1) {
					sb.append("&");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static void main(String[] argv) {
		Param param = new Param();
		param.put("email", "xerxes.h.hiz@gmail.com");
		param.put("password", "ada");
		System.out.println(param);
	}
}
