package com.imap.enums;

public enum ECommand {
	CAPABILITY("CAPABILITY"),
	LOGIN("LOGIN"),
	AUTHENTICATE("AUTHENTICATE"),
	LSUB("LSUB"),
	LIST("LIST"),
	SELECT("SELECT");
	
	private final String value;
	
	private ECommand(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
