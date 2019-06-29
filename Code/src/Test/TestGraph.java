package Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Graph.Graph;
import Graph.StronglyConnectedComponent;
import Graph.SuperGraph;
import Graph.Vertex;
import MinimalModel.MinimalModel;
import Socket.Client;

public class TestGraph {
	public static void main(String[] args) {		
		
		Random rand = new Random(); 
		int value = rand.nextInt(50)+1; 
		int size = rand.nextInt(10)+1;
		
		for(int i=0; i<100; i++) {
			size = rand.nextInt(10)+1;
			for(int j=0; j<size; j++) {
				
			}
		}
		
		
		
		
		
//		MinimalModel m = new MinimalModel();
//		String path=".//CnfFile.txt";
//
//		m.readfile(path);
//
//		Graph<Integer> g = m.createModelGraph();
//
//		// print the graph --> class: Graph
//		System.out.println("############################33");
//		System.out.println(g);
//		System.out.println("############################33");
//
//		StronglyConnectedComponent scc = new StronglyConnectedComponent();
//		List<Set<Vertex<Integer>>> result = scc.scc(g);
//		System.out.println("******\n"+result+"\n******\n");
//
//		System.out.println("-------------------------------------------------------------------------------------------");
//		SuperGraph super_graph = new SuperGraph(g);
//		super_graph.printGraph();
//		System.out.println("------------------------------");
//
//		ArrayList<Client<Integer>> sourceVertex = new ArrayList<>();
//		ArrayList<Vertex<Integer>> finalVertex = new ArrayList<>();
//
//		// default values if not entered
//		int portNumber = 1500;
//		String serverAddress = "localhost";
//		ArrayList<Client<Integer>> clients = new ArrayList<>();
//		Vertex<Integer> sink = new Vertex<Integer>(-100);
//
//		for(Vertex<Integer> v: super_graph.getSuperGraph().getAllVertex()) {
//			Client<Integer> client = new Client<>(serverAddress, portNumber, v.getId(), v, m.getDS(), result);
//			if(!client.start())
//				return;
//			clients.add(client);
//			if(v.getParentVertexes().size()==0)
//				sourceVertex.add(client);
//			
//			if(v.getAdjacentVertexes().size() == 0){
//				client.setLastVertex(true);
//				sink.addParentVertex(v);
//				finalVertex.add(v);
//			}
//		}
//
//		Client<Integer> client = new Client<>(serverAddress, portNumber, sink.getId(), sink, m.getDS(), result);
//		if(!client.start())
//			return;
//		clients.add(client);
//
//		for(int i=0; i<1; i++) {
//			for(Client<Integer> c: sourceVertex) {
//				c.findMinimalModels();
//			}
//		}

		//		Client client = new Client(serverAddress, portNumber, userName);
		// try to connect to the server and return if not connected



		//			// infinite loop to get the input from the user
		//			while(true) {
		//				System.out.print("> ");
		//				// read message from user
		//				String msg = scan.nextLine();
		//				// logout if message is LOGOUT
		//				if(msg.equalsIgnoreCase("LOGOUT")) {
		//					client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
		//					break;
		//				}
		//				// message to check who are present in chatroom
		//				else if(msg.equalsIgnoreCase("WHOISIN")) {
		//					client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
		//				}
		//				// regular text message
		//				else {
		//					client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
		//				}
		//			}
		// close resource
		// client completed its job. disconnect client.
		//		client.disconnect();	
	}





}
