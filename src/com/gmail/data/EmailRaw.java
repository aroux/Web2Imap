package com.gmail.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EmailRaw extends EmailBase {

	private StringBuffer rawContent;

	public EmailRaw(EmailSummary emailSummary, StringBuffer rawContent) {
		super(emailSummary.getFrom(), emailSummary.getSubject(), emailSummary.getDate(), emailSummary.getLink());
		this.rawContent = rawContent;
	}

	public StringBuffer getRawContent() {
		return rawContent;
	}

	public void setRawContent(StringBuffer rawContent) {
		this.rawContent = rawContent;
	}

	@Override
	public String toString() {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		String dateStr = formatter.format(getDate().getTime());
		return this.getClass().getName() + " : (from : " + getFrom() + ")(subject : " + getSubject() + ")(date : " + dateStr + ")(link : "
				+ getLink() + ")(raw:\n" + rawContent.toString() + "\n)";
	}
}
