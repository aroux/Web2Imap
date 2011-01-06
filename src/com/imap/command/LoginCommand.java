package com.imap.command;

import com.engine.MailGateway;
import com.engine.data.UserInformation;
import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;
import com.webmail.exc.WebmailWrongLoginException;

public class LoginCommand extends Command {

	public LoginCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	public static LoginCommand buildCommand(String commandId, String commandKey, String commandArgs) {
		return new LoginCommand(commandId, commandKey, commandArgs);
	}

	private UserInformation extractUserInformation() throws CommandException {
		String username;
		String password;

		try {
			String split[] = commandArgs.split("\\s+");
			username = split[0];//substring(1, split[0].length() - 2);
			password = split[1];//.substring(1, split[1].length() - 2);
			if (username.charAt(0) == '"') {
				username = username.substring(1, username.length()-2);
				password = password.substring(1, password.length()-2);
			}
			return new UserInformation(username, password);
		}
		catch (Exception e) {
			throw new CommandException(commandId, commandKey, commandArgs, 
					"Impossible to extract user information from login command : ("
					+ e.getClass().getName() + ") " + e.getMessage());
		}
	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.NOT_AUTHENTICATE.getValue();
	}
	
	@Override
	public Response executeImpl() throws CommandException {
		MailGateway mg = MailGateway.getInstance();
		UserInformation userInfo = extractUserInformation();
		Response resp = new Response(commandId);
		if (mg.isMailProviderSupported(userInfo)) {
			try {
				mg.webmailLogin(userInfo);
				conHandler.setUserInfo(userInfo);
				resp.setNewState(EConnectionState.AUTHENTICATE);
				genCompletedResponseLine(resp);
			} catch (WebmailWrongLoginException e) {
				throw new CommandException(commandId, commandKey, commandArgs, 
						"Impossible to login : ("
						+ e.getClass().getName() + ") " + e.getMessage());
			} catch (InstantiationException e) {
				throw new CommandException(commandId, commandKey, commandArgs, 
						"Impossible to login : ("
						+ e.getClass().getName() + ") " + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new CommandException(commandId, commandKey, commandArgs, 
						"Impossible to login : ("
						+ e.getClass().getName() + ") " + e.getMessage());
			}
		} else {
			throw new CommandException(commandId, commandKey, commandArgs, 
					"No webmail supported for given username '"
					+ userInfo.getUsername() + "'");
		}
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
