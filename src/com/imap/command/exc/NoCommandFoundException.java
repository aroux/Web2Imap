package com.imap.command.exc;

public class NoCommandFoundException extends CommandException {

	private static final long serialVersionUID = 199418441790978348L;

	public NoCommandFoundException(String id, String key, String args) {
		super(id, key, args);
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + ".  No command has been found for id '" 
		+ getId() + "', key '" + getKey() + "' and args '" + getArgs() + "'";
	}
}
