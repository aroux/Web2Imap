package com.engine;

import java.io.IOException;

import com.engine.da.DBManager;
import com.engine.da.entities.Message;
import com.imap.server.Server;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.run();
		
//		DBManager dbManager = DBManager.getInstance();
//		Message mw = new Message(1,1L,"Mon premier message!".getBytes());
//		dbManager.saveMessage(mw);
//		Message mr = dbManager.getMessage(1);
//		System.out.println(new String(mr.getContent()));
		
		//MailGateway mg = MailGateway.getInstance();
		
	}
}