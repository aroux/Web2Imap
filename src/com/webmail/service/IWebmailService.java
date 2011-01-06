package com.webmail.service;

import java.math.BigInteger;

import com.engine.data.UserInformation;
import com.webmail.exc.WebmailWrongLoginException;

public interface IWebmailService {
	
	void login(UserInformation userInfo) throws WebmailWrongLoginException;
	
	Long getLastWebmailUID();
	
}
