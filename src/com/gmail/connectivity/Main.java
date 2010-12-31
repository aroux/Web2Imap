package com.gmail.connectivity;

import java.io.IOException;
import java.util.List;

import com.gmail.data.EmailBase;
import com.gmail.data.EmailRaw;
import com.gmail.data.EmailSummary;
import com.imap.server.Server;

public class Main {
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.run();
	}
}
