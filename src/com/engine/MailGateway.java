package com.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.engine.da.DBManager;
import com.engine.da.entities.Message;
import com.engine.data.UserInformation;
import com.webmail.data.EmailSummary;
import com.webmail.exc.WebmailWrongLoginException;
import com.webmail.service.IWebmailService;

public class MailGateway {

	private static final Logger logger = Logger.getLogger(MailGateway.class);
	
	private static MailGateway self = null;
	
	private Map<String, Class<IWebmailService>> webmailServicesClassMap;
	
	private Map<UserInformation, IWebmailService> webmailServicesMap;
	
	private Map<String, MailsFetcherThread> fetchers;
	
	private MailGateway() {
		webmailServicesClassMap = new HashMap<String, Class<IWebmailService>>();
		webmailServicesMap = new HashMap<UserInformation, IWebmailService>();
		loadWebmailDescriptions();
	}
	
	public static MailGateway getInstance() {
		if (self == null) self = new MailGateway();
		return self;
	}
	
	private void loadWebmailDescriptions() {
		GlobalConfManager gcm = GlobalConfManager.getInstance();
		URL descDirURL = getClass().getClassLoader().getResource(gcm.getProperty("webmails.properties.dir"));
		try {
			File descDir = new File(descDirURL.toURI());
			for (File file : descDir.listFiles()) {
				loadWebmailDescription(file);
			}
		} catch (URISyntaxException e1) {
			logger.fatal("Impossible to read webmails descriptions. Exiting.", e1);
			System.exit(-1);
		}
	}
	
	private void loadWebmailDescription(File file) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));
			String webmailImplClassStr = prop.getProperty("webmail.impl");
			@SuppressWarnings("unchecked")
			Class<IWebmailService> webmailImplClass = (Class<IWebmailService>) Class.forName(webmailImplClassStr);
			String addressesSuffix = prop.getProperty("webmail.addresses.suffix");
			String addressesSplit[] = addressesSuffix.split(",");
			for (String addressSuffix : addressesSplit) {
				webmailServicesClassMap.put(addressSuffix, webmailImplClass);
			}
		} catch (FileNotFoundException e) {
			logger.fatal("Impossible to read mailserver description file '" 
					+ file.getAbsolutePath() + "'", e);
		} catch (IOException e) {
			logger.fatal("Impossible to read mailserver description file '" 
					+ file.getAbsolutePath() + "'", e);
		} catch (ClassNotFoundException e){
			logger.fatal("Impossible to read mailserver description file '" 
					+ file.getAbsolutePath() + "'", e);
		}
	}
	
	/**
	 * Returns true if username is a mail address and if the address suffix
	 * matches a webmail module.
	 * 
	 * @param userInfo user information containing username and password
	 * @return
	 */
	public boolean isMailProviderSupported(UserInformation userInfo) {
		if (!userInfo.getUsername().contains("@")) return false;
		String mailDomain = userInfo.getUsername().split("@")[1];
		userInfo.setMailDomain(mailDomain);
		return webmailServicesClassMap.containsKey(mailDomain);
	}
	
	/**
	 * This method executes login method of webmail module corresponding to
	 * userInfo. If login fails, WebmailWrongLoginException is thrown.
	 * 
	 * @param userInfo
	 * @throws WebmailWrongLoginException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void webmailLogin(UserInformation userInfo) throws 
		WebmailWrongLoginException, InstantiationException, IllegalAccessException {
		Class<IWebmailService> webmailServiceClass = webmailServicesClassMap.get(userInfo.getMailDomain());
		IWebmailService webmailService = webmailServiceClass.newInstance();
		webmailServicesMap.put(userInfo, webmailService);
		webmailService.login(userInfo);
		
		// TEMP CODE
		//List<EmailSummary> emails = webmailService.getAllEmailsSummaryFromWebmailUID(0L);
	}
	
	public List<String> getDirectories(String pattern) {
		List<String> directories = new ArrayList<String>();

		// Temp code
		if (pattern.equals("*")) {
			directories.add("INBOX");
		} else {
			directories.add("");
		}
		return directories;
	}
	
	public void refreshSelectedDirectory(UserInformation userInfo, String directory) {
		// For the moment, support only mailbox directory
		IWebmailService webmailService = webmailServicesMap.get(userInfo);
		Long lastWebuid = DBManager.getInstance().getLastMessageWebuid();
		List<EmailSummary> newEmails = webmailService.getAllEmailsSummaryFromWebmailUID(lastWebuid);
		
		for (EmailSummary es : newEmails) {
			StringBuffer mailContent = webmailService.getMessageContentWebmailUID(es.getWebuid());
			Message m = new Message();
			m.setContent(mailContent.toString().getBytes());
			m.setWebmailuid(es.getWebuid());
			DBManager.getInstance().saveMessage(m);
		}
	}
}
