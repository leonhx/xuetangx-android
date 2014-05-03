package com.leonhuang.xuetangx.webclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import com.leonhuang.xuetangx.component.User;

public class HTTPClient extends Client {

	public HTTPClient(User user) {
		super(user);
	}

	public HTTPClient(Cookie cookie, User user) {
		super(cookie, user);
	}

	public Response get(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		request(con, "GET", null);

		return react(con);
	}

	public Response post(URL url, Param query) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		request(con, "POST", query);
		con.setDoOutput(true);

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(query.toString());
		output.close();

		return react(con);
	}

	private Response react(HttpURLConnection con) throws IOException {
		cookie.update(Cookie.fromString(con.getHeaderField("Set-Cookie")));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(con.getInputStream())));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return new Response(sb.toString(), con.getResponseCode(),
				con.getResponseMessage(), this);
	}

	private void request(HttpURLConnection con, String method, Param query)
			throws ProtocolException {
		con.setRequestMethod(method);
		con.addRequestProperty("Accept", "text/plain, */*; q=0.01");
		con.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		con.addRequestProperty("Accept-Language",
				"en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4,zh-TW;q=0.2");
		con.addRequestProperty("Connection", "keep-alive");
		if (method.equals("POST") && null != query) {
			con.addRequestProperty("Content-length",
					String.valueOf(query.toString().length()));
			con.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
		}
		con.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
		if (null != cookie && !cookie.isEmpty()) {
			con.addRequestProperty("Cookie", cookie.toString());
			String csrf_token = cookie.get("csrftoken");
			if (null != csrf_token && !csrf_token.isEmpty()) {
				con.addRequestProperty("X-CSRFToken", csrf_token);
			}
		}
	}

	@Override
	public String type() {
		return "HTTP";
	}

}
