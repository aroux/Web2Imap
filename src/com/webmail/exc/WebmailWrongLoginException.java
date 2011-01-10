package com.webmail.exc;

public class WebmailWrongLoginException extends Exception {

	private static final long serialVersionUID = 1147985398466564421L;

	public WebmailWrongLoginException(String message) {
		super(message);
	}
	
}
