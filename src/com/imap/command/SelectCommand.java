package com.imap.command;

import com.engine.MailGateway;
import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class SelectCommand extends Command {
	
	public SelectCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.AUTHENTICATE.getValue();
	}
	
	public static SelectCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new SelectCommand(commandId, commandKey, commandArgs);
	}
	

	@Override
	public Response executeImpl() throws CommandException {
		Response resp = new Response(commandId);
		
		MailGateway mg = MailGateway.getInstance();
		mg.refreshSelectedDirectory(conHandler.getUserInfo(), commandArgs);
		
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
