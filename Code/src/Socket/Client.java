package Socket;

import java.net.*;
import java.util.ArrayList;

import java.io.*;


/**
 * The Client that can be run as a console
 **/

public class Client  {
	// notification
	private String notif = " *** ";

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;					// socket object

	private String server, username;	// server and username
	private int port;					//port

	private ArrayList<String> parent, child;

	HashTable htc = new HashTable();

	public String getUsername() { return this.username; }
	public ArrayList<String> getChildren() { return this.child; }
	public ArrayList<String> getParent() { return this.parent; }

	public void setUsername(String username) { this.username = username; }

	/*
	 *  Constructor to set below things
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 *  vs: the vertex structure
	 */
	public Client(String server, int port, String id, ArrayList<String> p, ArrayList<String> c) {
		this.server = server;
		this.port = port;
		this.username = id;
		this.parent = p;
		this.child = c;
	}

	/*
	 * To start the chat
	 */
	public boolean start() {

		try {	// try to connect to the server
			this.socket = new Socket(this.server, this.port);
		} 
		catch(Exception ec) {	// exception handler if it failed
			display("Error connectiong to server:" + ec);
			return false;
		}

		//		String msg = "Connection accepted " + this.socket.getInetAddress() + ":" + this.socket.getPort();
		//		display(msg);

		/* Creating both Data Stream */
		try{
			this.sInput  = new ObjectInputStream(this.socket.getInputStream());
			this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		if(this.parent.size()>0 && !this.username.equals("sink"))
			new ListenToServer().start();

		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be Message objects
		try{
			this.sOutput.writeObject(this.username);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console
	 */
	private void display(String msg) {
		System.out.println(msg);
	}

	/*
	 * To send a message to the server
	 */
	public void sendMessage(String msg) {
		if(this.child.size()==0) {			
			Message newMsg = new Message(msg+"->"+username, "sink", username, false);
			try {
				this.sOutput.writeObject(newMsg);
			}catch(IOException e) {
				display("Exception writing to server: " + e);
			}
			
			return;
		}
		for(String r :this.child) {
			Message newMsg = new Message(msg+"->"+username, r, username, false);
			try {
				this.sOutput.writeObject(newMsg);
			}catch(IOException e) {
				display("Exception writing to server: " + e);
			}
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect
	 */
	public void disconnect() {
		try { 
			if(this.sInput != null) this.sInput.close();
		} catch(Exception e) {}

		try {
			if(this.sOutput != null) this.sOutput.close();
		} catch(Exception e) {}

		try{
			if(this.socket != null) this.socket.close();
		} catch(Exception e) {}		
	}


	/**
	 * a class that waits for the message from the server
	 **/
	class ListenFromServer extends Thread {
		public synchronized void run() {
			while(true) {
				try {
					// read the message form the input datastream
					Message msg = (Message) sInput.readObject();
					if(!username.equals("sink"))
						htc.put(msg);
					else {
						System.out.println(msg.getMessage() + "=>"+username+" is last");
					}
				}
				catch(IOException e) {
					display(notif + "Server has closed the connection: " + e + notif);
					break;
				}
				catch(ClassNotFoundException e2) { }
			}
		}
	}

	class ListenToServer extends Thread{
		public synchronized void run() {
			while(true) {
				if(htc.size()==parent.size()) {
					String msg = "";
					
					for(String pid: htc.getHashedID()) {
						String str = null;
						while(str==null)
							str = htc.getValue(pid).getMessage();
						
						msg += str;
					}
					sendMessage(msg);
				} 
				else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}

			}
		}
	}
}
