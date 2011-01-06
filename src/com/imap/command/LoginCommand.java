package com.imap.command;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;
import com.imap.server.UserInformation;

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
			String split[] = commandArgs.split(" ");
			username = split[0].substring(1, split[0].length() - 2);
			password = split[1].substring(1, split[1].length() - 2);
			return new UserInformation(username, password);
		}
		catch (Exception e) {
			throw new CommandException(commandId, commandKey, commandArgs, "Impossible to extract user information from login command : ("
					+ e.getClass().getName() + ") " + e.getMessage());
		}

	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.NOT_AUTHENTICATE.getValue();
	}
	
	@Override
	public Response executeImpl() throws CommandException {
		UserInformation userInfo = extractUserInformation();
		conHandler.setUserInfo(userInfo);
		Response resp = new Response(commandId);
		resp.setNewState(EConnectionState.AUTHENTICATE);
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
