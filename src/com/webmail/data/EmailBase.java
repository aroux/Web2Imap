package com.webmail.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public abstract class EmailBase {

	private String from;

	private String subject;

	private String link;
	
	private Calendar date;
	
	private Long webuid;
	
	public EmailBase(String from, String subject, String date, String link) {
		super();
		this.from = from;
		this.subject = subject;
		this.link = link;

		//parseDate(date);
	}

	public EmailBase(String from, String subject, Calendar date, String link) {
		super();
		this.from = from;
		this.subject = subject;
		this.link = link;
		this.date = date;

	}

	private void parseDate(String d) {
		date = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		try {
			this.date.setTime(formatter.parse(d));
		}
		catch (ParseException e) {
			formatter = new SimpleDateFormat("d MMM");
			try {
				this.date.setTime(formatter.parse(d));
				this.date.set(Calendar.YEAR, today.get(Calendar.YEAR));
			}
			catch (ParseException f) {
				formatter = new SimpleDateFormat("kk:mm");
				try {
					this.date.setTime(formatter.parse(d));
					this.date.set(Calendar.YEAR, today.get(Calendar.YEAR));
					this.date.set(Calendar.MONTH, today.get(Calendar.MONTH));
					this.date.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException g) {
					g.printStackTrace();
				}
			}
		}
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLinkOriginalMessage() {
		return link.replace("v=c", "v=om");
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public Long getWebuid() {
		return webuid;
	}
	
	public void setWebuid(Long webuid) {
		this.webuid = webuid;
	}

	@Override
	public String toString() {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		String dateStr = formatter.format(date.getTime());
		return this.getClass().getName() + " : (from : " + from + ")(subject : " + subject + ")(date : " + dateStr + ")(link : " + link + ")";
	}

}
