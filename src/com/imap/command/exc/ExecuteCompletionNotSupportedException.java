package com.imap.command.exc;

public class ExecuteCompletionNotSupportedException extends CommandException {

	private static final long serialVersionUID = 6109811659521017104L;

	public ExecuteCompletionNotSupportedException(String id, String key, String args) {
		super(id, key, args);
	}
}
