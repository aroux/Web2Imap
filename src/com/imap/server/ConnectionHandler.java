package com.imap.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.engine.data.UserInformation;
import com.imap.command.Command;
import com.imap.command.CommandStore;
import com.imap.command.exc.CommandException;
import com.imap.enums.EConnectionState;
import com.imap.enums.EResponse;
import com.imap.response.Response;

public class ConnectionHandler extends Thread {

	private static final Logger logger = Logger.getLogger(ConnectionHandler.class);

	private final Socket socket;

	private BufferedReader socketReader;

	private BufferedWriter socketWriter;

	private EConnectionState currentState;

	private Command lastCommand;

	private UserInformation userInfo;

	public ConnectionHandler(Socket socket) throws IOException {
		this.socket = socket;
		initReaderWriter();
		lastCommand = null;
		currentState = EConnectionState.NOT_AUTHENTICATE;
	}

	private void initReaderWriter() throws IOException {
		socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void run() {
		String line = null;
		CommandStore comStore = CommandStore.getInstance();
		Response okResponse = new Response();

		okResponse.genStandardPrefixedResponseLine(EResponse.OK.getValue(), false);
		try {
			writeResponse(okResponse);
			logger.info("Waiting for commands from client.");
			while ((line = socketReader.readLine()) != null) {
				Response response;

				logger.info("Received input : [" + line + "]");
				try {
					if (lastCommand == null) {
						lastCommand = comStore.getCommandForInput(line);
						lastCommand.setConHandler(this);
						response = lastCommand.execute(currentState);
					} else {
						lastCommand.addCommandCompletion(line);
						response = lastCommand.executeCompletion(currentState);
					}
					if (!lastCommand.isWaitingForCompletion()) {
						lastCommand = null;
					}
					if (response.getNewState() != null) {
						currentState = response.getNewState();
					}
					writeResponse(response);
				}
				catch (CommandException e) {
					logger.error("Error while parsing/executing command.", e);
					triggerErrorResponse(e);
				}
				catch (InvocationTargetException e) {
					logger.error("Error while parsing/executing command.", e);
					triggerErrorResponse(e);
				}
				catch (IllegalArgumentException e) {
					logger.error("Error while parsing/executing command.", e);
					triggerErrorResponse(e);
				}
				catch (IllegalAccessException e) {
					logger.error("Error while parsing/executing command.", e);
					triggerErrorResponse(e);
				} catch (StringIndexOutOfBoundsException e) {
					logger.error("Error while parsing/executing command.", e);
					triggerErrorResponse(e);
				}
			}
		}
		catch (IOException e) {
			logger.fatal("Impossible to read next line from socket.", e);
		}

		shutdown();

		logger.info("Shutdown connection handler");
	}

	private void shutdown() {
		try {
			socketReader.close();
			socketWriter.close();
			socket.close();
		}
		catch (IOException e) {
			logger.error("Error during connection handler shutdown.", e);
		}

	}

	private void triggerErrorResponse(@SuppressWarnings("unused") Exception e) {
		writeErrorResponse(EResponse.BAD);
	}

	private void triggerErrorResponse(CommandException e) {
		EResponse responseType = e.isRequireBadResponse() ? EResponse.BAD : EResponse.NO;
		writeErrorResponse(responseType, e.getId());
	}

	private void writeResponse(Response response) throws IOException {
		Collection<String> lines = response.getLines();
		for (String line : lines) {
			String lineToWrite = line + "\r\n";
			socketWriter.write(lineToWrite);
			logger.info("Server responds : [" + line + "]");
		}
		socketWriter.flush();
	}

	private void writeErrorResponse(EResponse responseType) {
		writeErrorResponse(responseType, null);
	}

	private void writeErrorResponse(EResponse responseType, String commandId) {
		Response badResp = new Response(commandId);

		badResp.genStandardPrefixedResponseLine(responseType.getValue(), commandId != null);
		try {
			writeResponse(badResp);
		}
		catch (IOException e) {
			logger.fatal("Impossible to send BAD response to client. ", e);
		}
	}

	public void setUserInfo(UserInformation userInfo) {
		this.userInfo = userInfo;
	}

	public UserInformation getUserInfo() {
		return userInfo;
	}

	public EConnectionState getCurrentState() {
		return currentState;
	}
}
