package com.imap.enums;

public enum EConnectionState {
	UNKNOWN(0, "UNKNOWN"), //
	NOT_AUTHENTICATE(1, "NOT_AUTHENTICATE"), //
	AUTHENTICATE(2, "AUTHENTICATE"), //
	SELECTED(3, "SELECTED"), //
	LOGOUT(4, "LOGOUT");

	private final int value;

	private final String name;

	private EConnectionState(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return this.value;
	}

	public String getName() {
		return name;
	}
}
