package com.imap.command;

import java.util.List;

import com.engine.MailGateway;
import com.imap.command.exc.CommandException;
import com.imap.command.exc.ExecuteCompletionNotSupportedException;
import com.imap.enums.EConnectionState;
import com.imap.response.Response;

public class ListCommand extends Command {
	
	private final static String SEPARATOR = "/";
	
	public ListCommand(String commandId, String commandKey, String commandArgs) {
		super(commandId, commandKey, commandArgs);
	}

	@Override
	public boolean checkState(EConnectionState state) {
		return state.getValue() == EConnectionState.AUTHENTICATE.getValue();
	}
	
	public static ListCommand buildCommand(String commandId, String commandKey, 
			String commandArgs) {
		return new ListCommand(commandId, commandKey, commandArgs);
	}
	
	private void returnSeparator(Response resp) {
		String content = "(\\Noselect) \"" + SEPARATOR + "\" \"\"";
		resp.genStandardPrefixedResponseLineWithContent(commandKey, false, content);
	}
	
//	private void returnEmptyMailboxList(Response resp) {
//		String content = "(\\Noselect) \"/\" /";
//		resp.genStandardPrefixedResponseLineWithContent(commandKey, false, content);
//	}
	
	private void processForArgs(Response resp, String reference, String name) {
		MailGateway mg = MailGateway.getInstance();
		String interpretation;
		if (name.startsWith("~") || name.startsWith("/")) {
			interpretation = name;
		} else {
			interpretation = reference + name;
		}
		
		if (interpretation.equals("")) {
			returnSeparator(resp);
		} else  {
			List<String> directories = mg.getDirectories(interpretation);
			for (String dir : directories) {
				String content;
				if (dir.equals("")) {
					content = "(\\Noselect) \"/\" /";
				} else {
					content = "() \"/\" "+ dir;
				}
				resp.genStandardPrefixedResponseLineWithContent(commandKey, false, content);
			}
		}
	}

	@Override
	public Response executeImpl() throws CommandException {
		Response resp = new Response(commandId);
		
		try {
			String split[] = tokenizeArgs();
			processForArgs(resp, split[0], split[1]);
		}
		catch (Exception e) {
			throw new CommandException(commandId, commandKey, commandArgs, 
					"Impossible to get list command arguments : ("
					+ e.getClass().getName() + ") " + e.getMessage());
		}	
		
		genCompletedResponseLine(resp);
		return resp;
	}

	@Override
	public Response executeCompletionImpl() throws CommandException {
		throw new ExecuteCompletionNotSupportedException(commandId, commandKey, commandArgs);
	}

}
