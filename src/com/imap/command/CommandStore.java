package com.imap.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.imap.command.exc.CommandException;
import com.imap.enums.ECommand;

public class CommandStore extends HashMap<String, Method> {

	private static final Logger logger = Logger.getLogger(CommandStore.class);

	private static final long serialVersionUID = 3373651159069948883L;

	private static CommandStore self = null;

	private CommandStore() {
		initCommands();
	}

	private void initCommands() {
		try {
			this.put(ECommand.CAPABILITY.getValue(), CapabilityCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			this.put(ECommand.LOGIN.getValue(), LoginCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			this.put(ECommand.LSUB.getValue(), LsubCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			this.put(ECommand.LIST.getValue(), ListCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			this.put(ECommand.LOGOUT.getValue(), LogoutCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			this.put(ECommand.NOOP.getValue(), NoopCommand.class
					.getMethod("buildCommand", String.class, String.class, String.class));
			//this.put(ECommand.AUTHENTICATE.getValue(), AuthenticateCommand.class
			//		.getMethod("buildCommand", String.class, String.class, String.class));
		}
		catch (NoSuchMethodException e) {
			logger.fatal("Impossible to init commands", e);
			System.exit(-1);
		}
	}

	synchronized public static CommandStore getInstance() {
		if (self == null) {
			self = new CommandStore();
		}
		return self;
	}
	
	public Command getCommandForInput(String in) throws CommandException, 
		IllegalArgumentException, IllegalAccessException, InvocationTargetException,
		StringIndexOutOfBoundsException {
		String commandId;
		String commandKey;
		String commandArgs;
		String input;

		input = in.trim();
		int indexFirstSpace = input.indexOf(' ');
		commandId = input.substring(0, indexFirstSpace);
		int indexSecondSpace = input.indexOf(' ', indexFirstSpace + 1);

		if (indexSecondSpace == -1) {
			commandKey = input.substring(indexFirstSpace + 1);
			commandArgs = "";
		} else {
			commandKey = input.substring(indexFirstSpace + 1, indexSecondSpace);
			commandArgs = input.substring(indexSecondSpace + 1);
		}

		Method buildMethod = this.get(commandKey.toUpperCase());
		if (buildMethod == null) {
			throw new CommandException(commandId, commandKey, commandArgs, 
					"Command not supported");
		}

		return (Command) buildMethod.invoke(null, commandId, commandKey, commandArgs);
	}
}
