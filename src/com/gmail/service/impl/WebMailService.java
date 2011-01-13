package com.gmail.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.engine.data.UserInformation;
import com.gmail.connectivity.GmailConnection;
import com.webmail.data.EmailSummary;
import com.webmail.exc.WebmailWrongLoginException;
import com.webmail.service.IWebmailService;

public class WebMailService implements IWebmailService {
	
	private static final Logger logger = Logger.getLogger(WebMailService.class);

	GmailConnection gmailConnection = null;
	
	private static final Pattern WEBUID_URL_PATTERN = Pattern.compile("^.*th=([0-9a-f]+).*$");
	
	public WebMailService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void login(UserInformation userInfo) throws WebmailWrongLoginException {
		gmailConnection = new GmailConnection();
		gmailConnection.login(userInfo.getUsername(), userInfo.getPassword());
	}
	
	private boolean extractWebuidForEmailSummary(EmailSummary es) {
		Matcher matcher = WEBUID_URL_PATTERN.matcher(es.getLink());
		if (matcher.matches()) {
			try {
				es.setWebuid(Long.parseLong(matcher.group(1), 16));
				return true;
			} catch (NumberFormatException e) {
				logger.error(e.getClass().getName() + ". Impossible to extract webuid from mail " + es);
				return false;

			}
		} else {
			logger.error("Impossible to extract webuid from mail " + es);
			return false;
		}
	}
	
	@Override
	public List<EmailSummary> getAllEmailsSummaryFromWebmailUID(Long wmuid) {
		List<EmailSummary> returnList = new ArrayList<EmailSummary>();
		boolean stop = false;
		int page = 1;
		
		List<EmailSummary> emailsList = gmailConnection.getEmailsSummaryForPage(page);
		while ((!emailsList.isEmpty() && !stop)) {
			for (Iterator<EmailSummary> iter = emailsList.iterator(); iter.hasNext();) {
				EmailSummary es = iter.next();
				if (extractWebuidForEmailSummary(es)) {
					if ((wmuid == null) || (wmuid < es.getWebuid())) {
						returnList.add(es);
					} else {
						stop = true;
						break;
					}
				}
			}
			
			// TODO : remove to get all mails
			stop = true;
			
			if (!stop) {
				++page;
				emailsList = gmailConnection.getEmailsSummaryForPage(page);
			}
		}
		
		return returnList;
	}
	
	
	@Override
	public StringBuffer getMessageContentWebmailUID(Long wmuid) {
		return gmailConnection.getOriginalMessageFromWebmailUID(wmuid);
	}
}
