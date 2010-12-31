package com.gmail.data;

import java.util.List;

public class EmailDetails extends EmailBase {
	
	private List<String> to;
	
	private String content;

	
	public EmailDetails(EmailSummary emailSummary, List<String> to, String subject, String content) {
		super(emailSummary.getFrom(), emailSummary.getSubject(), emailSummary.getDate(), emailSummary.getLink());
		this.to = to;
		this.content = content;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
