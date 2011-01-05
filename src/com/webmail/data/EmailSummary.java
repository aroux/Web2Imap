package com.webmail.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EmailSummary extends EmailBase {

	private String summary;

	public EmailSummary(String from, String subject, String date, String link, String summary) {
		super(from, subject, date, link);
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		String dateStr = formatter.format(getDate().getTime());
		return this.getClass().getName() + " : (from : " + getFrom() + ")(subject : " + getSubject() + ")(summary : " + summary + ")(date : "
				+ dateStr + ")(link : " + getLink() + ")";
	}
}
