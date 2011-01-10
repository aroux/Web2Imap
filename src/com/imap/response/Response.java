package com.imap.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.imap.enums.EConnectionState;

public class Response {

	protected String commandId;

	private final List<String> responseLines;

	private EConnectionState newState;

	public Response() {
		this.commandId = null;
		responseLines = new ArrayList<String>();
		this.newState = null;
	}

	public Response(String commandId) {
		this.commandId = commandId;
		responseLines = new ArrayList<String>();
		this.newState = null;
	}

	public String getResponsePrefix(String commandKey, boolean tagged) {
		if ((tagged) && (commandId != null)) {
			return commandId + " " + commandKey;
		}
		return "* " + commandKey;
	}
	
	public void genStandardPrefixedResponseLine(String commandKey, boolean tagged) {
		responseLines.add(getResponsePrefix(commandKey, tagged));
	}
	
	public void genStandardPrefixedResponseLineWithContent(String commandKey, 
			boolean tagged, String content) {
		String line = getResponsePrefix(commandKey, tagged) + " " + content;
		responseLines.add(line);
	}

	public void genWaitingForNextCompletionResponseLine() {
		responseLines.add("+");
	}
	
	public void addRawResponseLine(String line) {
		responseLines.add(line);
	}

	public Collection<String> getLines() {
		return responseLines;
	}

	public void setNewState(EConnectionState newState) {
		this.newState = newState;
	}

	public EConnectionState getNewState() {
		return newState;
	}
}
