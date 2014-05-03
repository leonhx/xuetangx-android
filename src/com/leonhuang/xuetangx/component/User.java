package com.leonhuang.xuetangx.component;

public class User {
	private String email;
	private String password;
	private String name = "";
	private String nickname = "";
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public String email() {
		return this.email;
	}
	
	public String password() {
		return this.password;
	}
	
	public String name() {
		return this.name;
	}
	
	public String nickname() {
		return this.nickname;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
