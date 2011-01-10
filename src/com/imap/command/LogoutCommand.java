package com.imap.command;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.enums.EResponse;
import com.imap.response.Response;

public class LogoutCommand extends Command {

	public LogoutCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		// Any state command
		return true;
	}
	
	public static LogoutCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new LogoutCommand(commandId, commandKey, commandArgs);
	}

	@Override
	public Response executeImpl() throws CommandException {
		Response resp = new Response(commandId);
		
		String logoutMessage = "IMAP4rev1 Server logging out";
		resp.genStandardPrefixedResponseLineWithContent(EResponse.BYE.getValue(), false, logoutMessage);
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
