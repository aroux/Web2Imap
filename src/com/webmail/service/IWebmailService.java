package com.webmail.service;

import java.math.BigInteger;
import java.util.List;

import com.engine.data.UserInformation;
import com.webmail.exc.WebmailWrongLoginException;
import com.webmail.data.EmailRaw;
import com.webmail.data.EmailSummary;

public interface IWebmailService {
	
	void login(UserInformation userInfo) throws WebmailWrongLoginException;
	
	List<EmailSummary> getAllEmailsSummaryFromWebmailUID(Long wmuid);
	
	StringBuffer getMessageContentWebmailUID(Long wmuid);
}
