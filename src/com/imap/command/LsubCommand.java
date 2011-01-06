package com.imap.command;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class LsubCommand extends Command {

	public LsubCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.AUTHENTICATE.getValue();
	}
	
	public static LsubCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new LsubCommand(commandId, commandKey, commandArgs);
	}

	@Override
	public Response executeImpl() throws CommandException {
		// Not supported for the moment, returns empty response
		Response resp = new Response(commandId);
		
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
