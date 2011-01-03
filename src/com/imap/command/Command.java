package com.imap.command;

import java.util.ArrayList;
import java.util.List;

import com.imap.command.exc.CommandException;
import com.imap.command.exc.InvalidStateException;
import com.imap.enums.EConnectionState;
import com.imap.enums.EResponse;
import com.imap.response.Response;
import com.imap.server.ConnectionHandler;

public abstract class Command {

	protected String commandKey;

	protected String commandArgs;

	protected String commandId;

	protected List<String> completions;

	protected boolean waitingForCompletion;

	protected ConnectionHandler conHandler;

	public Command(String commandId, String commandKey, String commandArgs) {
		this.commandId = commandId;
		this.commandKey = commandKey;
		this.commandArgs = commandArgs;
		this.completions = null;
		this.waitingForCompletion = false;
	}

	protected void genCompletedResponseLine(Response resp) {
		String completedRespLine = resp.getResponsePrefix(EResponse.OK.getValue(), true);
		completedRespLine += " " + commandKey + " completed";
		resp.addResponseLine(completedRespLine);
	}

	public void addCommandCompletion(String input) {
		if (completions == null) {
			completions = new ArrayList<String>();
		}
		completions.add(input);
	}

	public final Response execute(EConnectionState state) throws CommandException {
		if (checkState(state)) {
			return executeImpl();
		}
		throw new InvalidStateException(commandId, commandKey, commandArgs, state);
	}

	public final Response executeCompletion(EConnectionState state) throws CommandException {
		if (checkState(state)) {
			return executeCompletionImpl();
		}
		throw new InvalidStateException(commandId, commandKey, commandArgs, state);
	}

	abstract public boolean checkState(EConnectionState state);

	abstract public Response executeImpl() throws CommandException;

	abstract public Response executeCompletionImpl() throws CommandException;

	public boolean isWaitingForCompletion() {
		return waitingForCompletion;
	}

	public void setConHandler(ConnectionHandler conHandler) {
		this.conHandler = conHandler;
	}
}
