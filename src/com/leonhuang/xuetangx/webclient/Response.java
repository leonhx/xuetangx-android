package com.leonhuang.xuetangx.webclient;


public class Response {
	private String content;
	private int code;
	private String message;
	private HTTPClient client;
	
	public Response(String content, int code, String message, HTTPClient client) {
		this.content = content;
		this.code = code;
		this.message = message;
		this.client = client;
	}

	public String getContent() {
		return content;
	}

	public int getResponseCode() {
		return code;
	}
	
	public String getResponseMessage() {
		return message;
	}

	public HTTPClient getClient() {
		return client;
	}
}
