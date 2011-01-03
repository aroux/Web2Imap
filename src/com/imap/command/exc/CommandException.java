package com.imap.command.exc;

public class CommandException extends Exception {

	private static final long serialVersionUID = 8126276206133539313L;

	private final String id;

	private final String key;

	private final String args;

	private String message;

	private boolean requireBadResponse;

	public CommandException(String id, String key, String args) {
		this.id = id;
		this.key = key;
		this.args = args;
		this.requireBadResponse = true;
	}

	public CommandException(String id, String key, String args, String message) {
		this.id = id;
		this.key = key;
		this.args = args;
		this.message = message;
		this.requireBadResponse = true;
	}

	@Override
	public String toString() {
		String errorMessage = this.getClass().getName() + ". Exception for id '" + id + "', key '" + key + "' and args '" + args + "'.";
		if (message != null) {
			errorMessage += (" " + message);
		}
		return errorMessage;
	}

	public String getId() {
		return id;
	}

	public String getKey() {
		return key;

	}

	public String getArgs() {
		return args;
	}

	public boolean isRequireBadResponse() {
		return requireBadResponse;
	}

	public void setRequireBadResponse(boolean requireBadResponse) {
		this.requireBadResponse = requireBadResponse;
	}
}
