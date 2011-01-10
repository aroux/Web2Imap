package com.imap.command;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class NoopCommand extends Command {

	public NoopCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		// Any state command
		return true;
	}
	
	public static NoopCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new NoopCommand(commandId, commandKey, commandArgs);
	}

	@Override
	public Response executeImpl() throws CommandException {
		Response resp = new Response(commandId);
		
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
