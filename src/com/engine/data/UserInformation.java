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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInformation other = (UserInformation) obj;
		if (mailDomain == null) {
			if (other.mailDomain != null)
				return false;
		} else if (!mailDomain.equals(other.mailDomain))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
}
