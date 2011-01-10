package com.imap.command;

public class LsubCommand extends ListCommand {

	public LsubCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	public static LsubCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new LsubCommand(commandId, commandKey, commandArgs);
	}
}
