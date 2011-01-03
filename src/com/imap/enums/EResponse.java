package com.imap.enums;

public enum EResponse {
	OK("OK"), //
	NO("NO"), //
	BAD("BAD");

	private final String value;

	private EResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
