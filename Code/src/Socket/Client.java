package Socket;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import Graph.Vertex;
import MinimalModel.ConstructMinimalModels;
import Rules.LinkedList;
import Rules.RulesDataStructure;

import java.io.*;


/**
 * The Client that can be run as a console
 **/

public class Client <T> {
	// notification
	private String notif = " *** ";

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;					// socket object

	private String server, username;	// server and username
	private int port;					//port

	private List<Vertex<T>> parent, child;
	private RulesDataStructure DS;
	private LinkedList vertexCC;

	private Boolean lastVertex;

	HashTable htc = new HashTable();

	/***************/
	ConstructMinimalModels mm_DP;

	private List<Set<Vertex<Integer>>> SCC;
	private HashMap<Integer, Boolean> literalMap;
	private Hashtable<Integer, LinkedList> varHT;

	public String getUsername() { return this.username; }
	public List<Vertex<T>> getChildren() { return this.child; }
	public List<Vertex<T>> getParent() { return this.parent; }

	public void setUsername(String username) { this.username = username; }

	/*
	 *  Constructor
	 */
	public Client(String server, int port, long id, Vertex<T> v, RulesDataStructure ds, List<Set<Vertex<Integer>>> scc) {
		this.server = server;
		this.port = port;
		this.username = v.getNameID();
		this.parent = v.getParentVertexes();
		this.child = v.getAdjacentVertexes();
		this.DS = new RulesDataStructure(ds);
		this.lastVertex = false;
		this.vertexCC = new LinkedList();
		if(id!=-100)
			for(Vertex<Integer> vertex: v.getCCList()) 
				this.vertexCC.addAtTail((int)vertex.getId());

		literalMap = DS.getLiteralMap();
		varHT = DS.getVarHT();

		this.SCC = new ArrayList<>();
		for(int i=0; i<scc.size(); i++)
			this.SCC.add(scc.get(i));
	}

	public void setLastVertex(Boolean last) {
		this.lastVertex = last;
	}

	/*
	 * To start the chat
	 */
	public boolean start() {
		try {	// try to connect to the server
			this.socket = new Socket(this.server, this.port);
		} catch(Exception ec) {	// exception handler if it failed
			display("Error connectiong to server:" + ec);
			return false;
		}
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

	public void findMinimalModels() {
		DS.setVarHT(varHT);
//		DS.checkForUnits();
		this.DS.setLiteralMap(literalMap);
//		DS.updateRuleDS();
//		System.out.println("##########");
//		printLiteralMap();
//		System.out.println("##########");
		
		mm_DP = new ConstructMinimalModels(username, this.DS, this.vertexCC, this.SCC, this.lastVertex);
//		mm_DP.minimalModelsUsingDP();
		mm_DP.ModuminUsingWASP();
		
		sendMessage(mm_DP);
	}



	public void printLiteralMap() {
		Set<Integer> keys = literalMap.keySet();
		for(int key: keys){
			System.out.print("Value of " + key +" is ");
			if(literalMap.get(key))
				System.out.println("TRUE");
			else
				System.out.println("FALSE");
		}
	}

	/*
	 * To send a message to the server
	 */
	public void sendMessage(ConstructMinimalModels msg) {
		if(this.child.size()==0) {	
			long endTime;
			endTime = System.nanoTime();
			System.out.println("end time: "+endTime);
			Message newMsg = new Message("vertex-100", username, msg.getLiteralMap(), msg.getMinimalModels());
			try {
				this.sOutput.writeObject(newMsg);
			}catch(IOException e) {
				display("Exception writing to server: " + e);
			}

			return;
		}
		for(Vertex<T> r :this.child) {
			Message newMsg = new Message(r.getNameID(), username, msg.getLiteralMap(), msg.getMinimalModels());
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
			int num=0;
			while(true) {
				
				try {
					// read the message form the input datastream
					Message msg = (Message) sInput.readObject();
					if(!username.equals("vertex-100"))
						htc.put(msg);
					else {
						num++;
						/***run time checking*/
						long startTime,endTime,totalTime;
								endTime = System.nanoTime();
								System.out.println("msg num: "+num+", time: "+endTime);
						printModels(msg.getModels());
					}
				}
				catch(IOException e) {
					display(notif + "Server has closed the connection: " + e + notif);
					break;
				}
				catch(ClassNotFoundException e2) { }
			}
		}

		public void printModels(ArrayList<Integer> models) {
			String str= "[ ";
			for(int var : models)
				str+= "{"+var+"}" + " ";

			str+= "]" + "\r\n" +" |MM| = "+ models.size();
			System.out.println(str);
		}
	}

	class ListenToServer extends Thread{
		public synchronized void run() {
			while(true) {
				if(htc.size()==parent.size() && !username.equals("vertex-100")) {
					for(String pid: htc.getHashedID()) 
						updateLiteralMap(htc.getValue(pid));

					findMinimalModels();
				}
				else {
					try { Thread.sleep(1);} 
					catch (InterruptedException e) {}
				}
			}
		}

		public void updateLiteralMap(Message msg) {
			Set<Integer> keys = msg.getLiteralMap().keySet();
			for(int key: keys) 
				literalMap.put(key, msg.getLiteralMap().get(key));

		}
	}
}
