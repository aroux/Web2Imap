package com.engine.data;

public class UserInformation {
	
	private String username;
	
	private String password;
	
	private String mailDomain;

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
	
	public String getMailDomain() {
		return mailDomain;
	}
	
	public void setMailDomain(String mailDomain) {
		this.mailDomain = mailDomain;
	}
}
