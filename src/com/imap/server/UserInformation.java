package com.imap.server;

public class UserInformation {
	
	private String username;
	
	private String password;

	public UserInformation(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
