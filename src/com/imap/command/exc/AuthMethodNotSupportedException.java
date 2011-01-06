package com.imap.command.exc;

public class AuthMethodNotSupportedException extends CommandException {

	private static final long serialVersionUID = -1195886916561808267L;

	private final String method;

	public AuthMethodNotSupportedException(String id, String key, String args, String method) {
		super(id, key, args);
		this.method = method;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ".  Authentifcation method not supported" + " for method '" + method + "', id '" + getId()
				+ "', key '" + getKey() + "' and args '" + getArgs() + "'";
	}

	public String getMethod() {
		return method;
	}

}
