package com.imap.command;

import org.apache.commons.lang.StringUtils;

import com.imap.command.exc.AuthMethodNotSupportedException;
import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class AuthenticateCommand extends Command {
	
	private String method;
	
	public AuthenticateCommand(String commandId, String commandKey,
			String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}
	
	public static AuthenticateCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new AuthenticateCommand(commandId, commandKey, commandArgs);
	}

	@Override
	public Response executeImpl() throws AuthMethodNotSupportedException {
		method = commandArgs.toUpperCase();
		Response resp = new Response(commandId);
		if (StringUtils.equals("PLAIN", commandArgs.toUpperCase())) {
			resp.genWaitingForNextCompletionResponseLine();
			waitingForCompletion = true;
			return resp;
		} else {
			AuthMethodNotSupportedException e = 
				new AuthMethodNotSupportedException(commandId, commandKey, commandArgs, method);
			e.setRequireBadResponse(false);
			throw e;
		}
	}
	
	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.NOT_AUTHENTICATE.getValue();
	}
}
