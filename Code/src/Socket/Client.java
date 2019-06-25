package Socket;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import Graph.Vertex;
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

	private List<Set<Vertex<Integer>>> SCC;
	private HashMap<Integer, Boolean> literalMap;
	private Hashtable<Integer, LinkedList> varHT;

	public String getUsername() { return this.username; }
	public List<Vertex<T>> getChildren() { return this.child; }
	public List<Vertex<T>> getParent() { return this.parent; }

	public void setUsername(String username) { this.username = username; }

	/*
	 *  Constructor to set below things
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 *  vs: the vertex structure
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

		literalMap = DS.copyLiteralMap();
		varHT = DS.copyVarHT();

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

	public void constructMinimalModel() {
		System.out.println("~~~~~~~~~~~~~~~~CONSTRUCT MINIMAL MODEL~~~~~~~~~~~~~~~~~~~");
		System.out.println("Vertex "+username);
		System.out.print("CC List: ");
		this.vertexCC.printList();
		System.out.println();
		this.DS.setVarHT(varHT);

		int num = 15;
		while(DS.SIZE!=0) {
			for(int i=0; i<this.SCC.size(); i++) {
				Set<Vertex<Integer>> tmp_scc= this.SCC.get(i);
				int counter = 0;
				for(Vertex<Integer> v: tmp_scc) {
					if(literalMap.containsKey((int)v.getId()))
						counter++;
				}
				if(counter==tmp_scc.size())
					this.SCC.remove(i);
			}
			//			this.vertexCC.printList();
			DS.checkForUnits();


			this.DS.setLiteralMap(literalMap);
			//			System.out.println("^^^^^^^^^^");
//						printLiteralMap();
			//			System.out.println("^^^^^^^^^^");
			//			DS.checkForUnits();
			DS.updateRuleDS();
			//					DS.checkForUnitsByVar(ID);
			//			this.DS.setLiteralMap(literalMap);
			//					DS.checkForUnitsByVar(ID);
			//			System.out.println("^^^");
						printLiteralMap();
			//			DS.printRulesArray();
			//			DS.printHashTable();
			//			System.out.println("^^^");
			//		}
			//		if(child.size()>0)
			//				DS.checkForUnits();
			//		DS.checkForUnitsByVar(ID);
			//			this.DS.printRulesArray();


			System.out.println("size: " + DS.SIZE+"  ,  ");
			vertexCC.printList();
			LinkedList Ts=DS.Ts(this.vertexCC);
			System.out.println("Ts size: " + Ts.getSize());
			Ts.printList();
			if(!DS.FindMinimalModelForTs(Ts)){
				System.out.println("UNSAT");
				//				System.out.println("The amount of time we put value in a variable is : " + DS.counter);
			}
			//			DS.printValueOfVariables();
			HashMap<Integer, Boolean> tmp =	this.DS.copyLiteralMap();
			Set<Integer> keys = tmp.keySet();
			for(int key: keys) {
				if(tmp.get(key))
					literalMap.put(key, true);
				else
					literalMap.put(key, false);
			}
			DS.updateRuleDS();
			DS.printRulesArray();
			if(!this.lastVertex)
				break;
			else {
				int count = 0;
				Boolean update = false;
				for(int j=0; j<this.SCC.size(); j++) {
					for(Vertex<Integer> v: this.SCC.get(j)) {
						if(DS.getLiterals().contains((int)v.getId()))
							count++;
					}
					if(DS.getLiterals().size()==count) {
						update = true;
						break;
					}	
				}
				
				if(update) {
					for(int k: DS.getLiterals()) {
						literalMap.put(k, true);
					}
					DS.setLiteralMap(literalMap);
					DS.updateRuleDS();
				}

				num--;
			}

		}
		//		System.out.println("@@@@@@@@@@@@@@@@@@@@");
		//		System.out.println("size: " + DS.SIZE);
		//		DS.printRulesArray();
		//		printLiteralMap();
		//		System.out.println("@@@@@@@@@@@@@@@@@@@@");		
		System.out.println("#####"+DS.StringMinimalModel()+"#####");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~END~~~~~~~~~~~~~~~~~~~~~~~~~~~");		
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
	public void sendMessage(String msg) {
		constructMinimalModel();

		if(this.child.size()==0) {		
			Message newMsg = new Message(msg+"->"+username, "vertex-100", username, this.literalMap, false);
			try {
				this.sOutput.writeObject(newMsg);
			}catch(IOException e) {
				display("Exception writing to server: " + e);
			}

			return;
		}
		for(Vertex<T> r :this.child) {
			Message newMsg = new Message(msg+"->"+username, r.getNameID(), username, this.literalMap, false);
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
					if(!username.equals("vertex-100"))
						htc.put(msg);
					else {
						System.out.println(DS.StringMinimalModel());
						System.out.println(msg.getMessage() + "=>"+username+" is last");
						break;
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
				if(htc.size()==parent.size() && !username.equals("vertex-100")) {
					String msg = "";
					for(String pid: htc.getHashedID()) {
						String str = null;
						while(str==null) {
							Message tmp = htc.getValue(pid);
							str = tmp.getMessage();

							Set<Integer> keys = tmp.getLiteralMap().keySet();
							for(int key: keys) {
								if(tmp.getLiteralMap().get(key))
									literalMap.put(key, true);
								else
									literalMap.put(key, false);
							}
							//							literalMap.putAll(tmp.getLiteralMap());
							//							hm.putAll(tmp.getLiteralMap());
							//							System.out.println("-*-*-*-*-*-*-THE VALUE TABLE--------");
							//							Set<Integer> keys = tmp.getLiteralMap().keySet();
							//							for(int key: keys){
							//								System.out.print("Value of " + key +" is ");
							//								if(tmp.getLiteralMap().get(key))
							//									System.out.println("TRUE");
							//								else
							//									System.out.println("FALSE");
							//							}
							//							System.out.println("-*-*-*-*-*-*-*-----------------------");
						}
						msg += str;
					}
					sendMessage(msg);
				}
				else if(htc.size()==parent.size() && username.equals("vertex-100")) {
					String msg = "";
					for(String pid: htc.getHashedID()) {
						String str = null;
						while(str==null) {
							Message tmp = htc.getValue(pid);
							str = tmp.getMessage();
						}

						msg += str;
					}
					sendMessage(msg);
					break;
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
