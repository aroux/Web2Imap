package com.imap.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class);

	private final static int PORT = 11143;

	private ServerSocket serverSocket;

	private Set<ConnectionHandler> connections;

	public Server() throws IOException {
		initServerSocket();
	}

	private void initServerSocket() throws IOException {
		serverSocket = new ServerSocket(PORT);
		connections = new HashSet<ConnectionHandler>();
	}

	public void run() throws IOException {
		InetSocketAddress remoteAddress;

		logger.info("Running imap server...");
		while (true) {
			Socket socket = serverSocket.accept();
			remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			logger.info("Received new incoming connection from " + remoteAddress.getHostName() + ":" + remoteAddress.getPort());
			ConnectionHandler ch = new ConnectionHandler(socket);
			connections.add(ch);
			ch.start();
		}
	}
}
