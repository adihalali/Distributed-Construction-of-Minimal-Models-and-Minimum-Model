package Socket;

import java.io.*;
import java.net.*;
import java.util.*;

// the server that can be run as a console
public class Server {

	private static int uniqueId;				// a unique ID for each connection
	private HashMap<String, ClientThread> al;	// an ArrayList to keep the list of the Client
	private int port;							// the port number to listen for connection
	private boolean keepGoing;					// to check if server is running
	private String notif = " *** ";				// notification

	//constructor that receive the port to listen to for connection as parameter
	public Server(int port) {
		this.port = port;
		this.al = new HashMap<>();
	}

	public void start() {
		this.keepGoing = true;

		try {	//create socket server and wait for connection requests 
			ServerSocket serverSocket = new ServerSocket(this.port);	// the socket used by the server

			while(this.keepGoing) {							// infinite loop to wait for connections ( till server is active )
				display("Server waiting for Clients on port " + this.port + ".");

				Socket socket = serverSocket.accept();		// accept connection if requested from client
				if(!this.keepGoing)							// break if server stoped
					break;

				ClientThread t = new ClientThread(socket);	// if client is connected, create its thread
				this.al.put(t.username, t);					//add this client to arraylist

				t.start();
			}

			try {			// try to stop the server
				serverSocket.close();
				Set<String> keys = this.al.keySet();	
				for(String key: keys) {
					ClientThread tc = this.al.get(key);
					try {	// close all data streams and socket
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch(IOException ioE) { }
				}
			} catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) {
			display(" Exception on new ServerSocket: " + e + "\n");
		}
	}

	// to stop the server
	@SuppressWarnings("resource")
	protected void stop() {
		this.keepGoing = false;
		try {
			new Socket("localhost", this.port);
		} catch(Exception e) { }
	}

	// Display an event to the console
	private void display(String msg) {
		String time = " " + msg;
		System.out.println(time);
	}

	// to broadcast a message to all Clients
	private synchronized boolean broadcast(Message msg) {
		ClientThread ct = this.al.get(msg.getReceiver());
		if(ct != null) {
			if(!ct.writeMsg(msg)) {
				this.al.remove(ct.username);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
			return true;
		}
		return false;
	}

	// if client sent LOGOUT message to exit
	synchronized void remove(String id) {
		ClientThread ct = this.al.get(id);
		if(ct != null)
			this.al.remove(id);
	}


	/**
	 *  To run as a console application
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		int portNumber = 1500;	// start server on port 1500 unless a PortNumber is specified 

		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** 
	 * One instance of this thread will run for each client
	 **/
	class ClientThread extends Thread {
		// the socket to get messages from client
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;

		int id;				// my unique id (easier for deconnection)
		String username;	// the Username of the Client
		Message cm;			// message object to recieve message and its type

		// Constructor
		ClientThread(Socket socket) {
			id = ++uniqueId;	// a unique id
			this.socket = socket;

			//Creating both Data Stream
			System.out.print("Thread trying to create Object Input/Output Streams");
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());

				username = (String) sInput.readObject();	// read the username
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			} catch (ClassNotFoundException e) { }

			System.out.println("*** "+username);
		}

		public String getUsername() { return username; }
		public void setUsername(String username) { this.username = username; }


		public void run() {	// infinite loop to read and forward message

			boolean keepGoing = true;	// to loop until LOGOUT
			while(keepGoing) {

				try {	// read a String (which is an object)
					cm = (Message) sInput.readObject();
				} catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				} catch(ClassNotFoundException e2) { break; }

				boolean confirmation =  broadcast(cm);
				if(confirmation==false){
					display(notif + "Sorry. No such user exists." + notif);
				}
			}
			// if out of the loop then disconnected and remove from client list
			remove(username);
			close();
		}

		// close everything
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			} catch(Exception e) {}

			try {
				if(sInput != null) sInput.close();
			} catch(Exception e) {};

			try {
				if(socket != null) socket.close();
			} catch (Exception e) {}
		}

		// write a String to the Client output stream
		private synchronized boolean writeMsg(Message msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display(notif + "Error sending message to " + username + notif);
				display(e.toString());
			}
			return true;
		}
	}
}
