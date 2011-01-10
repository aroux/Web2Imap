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

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.NOT_AUTHENTICATE.getValue();
	}
	
	protected UserInformation extractUserInformation() throws CommandException {
		try {
			String split[] = tokenizeArgs();
			return new UserInformation(split[0], split[1]);
		}
		catch (Exception e) {
			throw new CommandException(commandId, commandKey, commandArgs, 
					"Impossible to extract user information from login command : ("
					+ e.getClass().getName() + ") " + e.getMessage());
		}
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
				CommandException exc = new CommandException(commandId, commandKey, commandArgs, 
						"Impossible to login : ("
						+ e.getClass().getName() + ") " + e.getMessage());
				exc.setRequireBadResponse(false);
				throw exc;
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
