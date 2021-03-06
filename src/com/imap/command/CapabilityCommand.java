package com.imap.command;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class CapabilityCommand extends Command {

	public CapabilityCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	public static CapabilityCommand buildCommand(String commandId, String commandKey, String commandArgs) {
		return new CapabilityCommand(commandId, commandKey, commandArgs);
	}
	
	@Override
	public boolean checkState(EConnectionState state) {
		// Any state command
		return true;
	}

	@Override
	public Response executeImpl() {
		Response resp = new Response(commandId);

		resp.genStandardPrefixedResponseLineWithContent(commandKey, false, " IMAP4rev1");// STARTTLS AUTH=PLAIN LOGINDISABLED";)

		genCompletedResponseLine(resp);

		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
