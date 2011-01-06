package com.gmail.service.impl;

import com.engine.data.UserInformation;
import com.gmail.connectivity.GmailConnection;
import com.webmail.service.IWebmailService;

public class WebMailService implements IWebmailService {

	GmailConnection gmailConnection = null;
	
	public WebMailService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void login(UserInformation userInfo) {
		gmailConnection = new GmailConnection();
		gmailConnection.login(userInfo.getUsername(), userInfo.getPassword());
	}
	
	@Override
	public Long getLastWebmailUID() {
		// TODO Auto-generated method stub
		return null;
	}
}
