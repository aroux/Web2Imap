package com.gmail.connectivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import com.engine.MailGateway;
import com.gmail.html.WebParser;
import com.gmail.utils.Utils;
import com.webmail.data.EmailRaw;
import com.webmail.data.EmailSummary;
import com.webmail.exc.WebParserException;
import com.webmail.exc.WebmailWrongLoginException;

public class GmailConnection {

	private static final Logger logger = Logger.getLogger(GmailConnection.class);
	
	private final DefaultHttpClient httpClient;

	private final WebParser wp;

	//private List<EmailSummary> emailsSummary;

	private final static String GOOGLE_AUTH_URL = "https://www.google.com/accounts/ServiceLoginAuth";
	private final static String GMAIL_HOME = "http://www.gmail.com";
	private final static String GMAIL_INBOX_BASENAME = "https://mail.google.com/mail/h/";
	private final static String GMAIL_ORIGINAL_MAIL_PARAMS = "?v=om&th=";

	private String homeCurrentLocation = null;
	
	private Integer numberMailsPerPage = null; 

	private static final Pattern PAGE_URL_PATTERN = Pattern.compile("^p(\\d+)$");
	
	public GmailConnection() {
		httpClient = new DefaultHttpClient();
		wp = new WebParser();
	}

	private StringBuffer inputStreamToStringBuffer(InputStream instream) throws IOException {
		StringBuffer sb = new StringBuffer();
		int l;
		char[] tmp = new char[2048];
		InputStreamReader instreamReader = new InputStreamReader(instream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(instreamReader);
		while ((l = bufferedReader.read(tmp)) != -1) {
			sb.append(tmp, 0, l);
		}
		return sb;
	}

	private StringBuffer getLoginPage() {
		return getPage(GMAIL_HOME);
	}

//	@SuppressWarnings("unused")
//	private Cookie getCurrentGALXCookie() {
//		CookieStore cs = httpClient.getCookieStore();
//		List<Cookie> cookies = cs.getCookies();
//		for (Cookie cookie : cookies) {
//			if (cookie.getName().equals("GALX")) {
//				return cookie;
//			}
//		}
//		return null;
//	}

	private HttpPost genLoginPostRequest(Map<String, String> postVars, String username, String password) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		postVars.put("Email", username);
		postVars.put("Passwd", password);

		for (Entry<String, String> entry : postVars.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httpPost = new HttpPost(GOOGLE_AUTH_URL);
		httpPost.setEntity(entity);
		return httpPost;
	}

	private StringBuffer executeRequest(HttpUriRequest request) {
		StringBuffer sb = null;
		try {
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				new RuntimeException("Entity is null after getting page at " + request.getURI());
				System.exit(-1);
			} else {
				sb = inputStreamToStringBuffer(entity.getContent());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return sb;
	}

	private StringBuffer getPage(String url) {
		return executeRequest(new HttpGet(url));
	}
	
	private void loginFailed(Exception e) throws WebmailWrongLoginException {
		logger.error("Login failed. Cause : ", e);
		throw new WebmailWrongLoginException("Wrong login : " + e.getClass().getName());
	}

	public void login(String username, String password) throws WebmailWrongLoginException {
		try {
			StringBuffer loginPageContent = getLoginPage();
			wp.feedParser(loginPageContent);
			Map<String, String> postVars = wp.getLoginFormInputs();

			HttpPost postRequest = genLoginPostRequest(postVars, username, password);
			StringBuffer loginResponseContent = executeRequest(postRequest);
			wp.feedParser(loginResponseContent);
			String redirectUrl = wp.getRedirectingUrl();

			//			Utils.inputStreamToFile(loginResponseContent, "C:\\Users\\Alexandre\\workspace\\GmailConnectivityTest\\data\\result.htm");
			//System.out.println(redirectUrl);
			refreshHomePage(redirectUrl);
			homeCurrentLocation = wp.getCurrentLocation();
			//Utils.inputStreamToFile(homePage, "C:\\Users\\Alexandre\\workspace\\GmailConnectivityTest\\data\\result.htm");
		} catch (WebParserException e) {
			loginFailed(e);
		} catch (ParserException e) {
			loginFailed(e);
		} catch (UnsupportedEncodingException e) {
			loginFailed(e);
		}
	}
	
//	private int getCurrentPageFromUrl(String url) {
//		String basename = Utils.basename(url);
//		
//		Matcher matcher = PAGE_URL_PATTERN.matcher(basename);
//		if (matcher.matches()) {
//			return Integer.parseInt(matcher.group(1));
//		} else {
//			return 1;
//		}
//	}

	public List<EmailSummary> getEmailsSummaryForPage(int page) {
		Integer stArg = null;
		if (page == 1) {
			refreshHomePage(homeCurrentLocation);
		} else {
			stArg = (page-1)*numberMailsPerPage;
			refreshHomePage(homeCurrentLocation + "/?st=" + stArg);
		}
		
		 List<EmailSummary> returnList = wp.getEmailSummaryList();
		 if (numberMailsPerPage == null) {
			 numberMailsPerPage = returnList.size();
		 }
		 
		 return returnList;
//		int pageDl = getCurrentPageFromUrl(wp.getCurrentLocation());
//		if (pageDl == page) {
//			return wp.getEmailSummaryList();
//		} else {
//			return Collections.emptyList();
//		} 
	}

	public void refreshHomePage(String url) {
		StringBuffer homePage = getPage(url);
		wp.feedParser(homePage);
		/*emailsSummary = wp.getEmailSummaryList();
		homeCurrentLocation = wp.getCurrentLocation();*/
	}
	
	public StringBuffer getOriginalMessageFromWebmailUID(Long wmuid) {
		String url = GMAIL_INBOX_BASENAME + "/" + GMAIL_ORIGINAL_MAIL_PARAMS + Long.toHexString(wmuid);
		StringBuffer originalMessage = getPage(url);
		return originalMessage;
	}

	public EmailRaw getEmailRawContent(EmailSummary emailSummary) {
		StringBuffer emailRawContent = getPage(homeCurrentLocation + "/" + emailSummary.getLinkOriginalMessage());
		return new EmailRaw(emailSummary, emailRawContent);
	}

	public void getEmailContent(EmailSummary emailSummary) {
		StringBuffer emailContentPage = getPage(homeCurrentLocation + "/" + emailSummary.getLink());

		/*Utils.inputStreamToFile(emailContentPage, "C:\\Users\\Alexandre\\workspace\\GmailConnectivityTest\\data\\email_content.htm");*/
	}
	
//	public EmailSummary getMostRecentEmailSummary() {
//		emailsSummary.get(0);
//	}

	public void testSummary() {
		FileInputStream fis;
		WebParser wp;

		try {
			fis = new FileInputStream("C:\\Users\\Alexandre\\workspace\\GmailConnectivityTest\\data\\result.htm");
			StringBuffer sb = inputStreamToStringBuffer(fis);
			//sb = HtmlUtils.closeGmailHomeTRTags(sb);
			wp = new WebParser();
			wp.feedParser(sb);

			wp.getEmailSummaryList();

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public void testDetails() {
		FileInputStream fis;
		WebParser wp;

		try {
			fis = new FileInputStream("C:\\Users\\Alexandre\\workspace\\GmailConnectivityTest\\data\\email_content.htm");
			StringBuffer sb = inputStreamToStringBuffer(fis);
			//sb = HtmlUtils.closeGmailHomeTRTags(sb);
			wp = new WebParser();
			wp.feedParser(sb);

			wp.getEmailDetails();

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}
}
