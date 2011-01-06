package com.imap.command.exc;

import com.imap.enums.EConnectionState;

public class InvalidStateException extends CommandException {

	private static final long serialVersionUID = 1123771220122480713L;

	private final EConnectionState currentState;

	public InvalidStateException(String id, String key, String args, EConnectionState currentState) {
		super(id, key, args);
		this.currentState = currentState;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ".  Invalid state for id '" + getId() + "', key '" + getKey() + "' args '" + getArgs()
				+ "' and state '" + currentState.getName() + "'";
	}

}
