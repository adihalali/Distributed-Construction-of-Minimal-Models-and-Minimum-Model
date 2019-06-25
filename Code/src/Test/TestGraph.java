package Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Graph.Graph;
import Graph.StronglyConnectedComponent;
import Graph.SuperGraph;
import Graph.Vertex;
import MinimalModel.MinimalModel;
import Rules.RulesDataStructure;
import Socket.Client;

public class TestGraph {
	public static void main(String[] args) {
//		Graph<Integer> g = new Graph<>(true);
		//				g.addEdge(10, 3, 1, -1);
		//				g.addEdge(10, 4, 1, -1);
		//				g.addEdge(1, 10, 1, -1);
		//				g.addEdge(4, 1, 1, -1);
		//				g.addEdge(3, 1, 1, -1);
		//				g.addEdge(1, 5, 1, -1);
		//				g.addEdge(1, 2, 1, -1);
		//				g.addEdge(2, 1, 1, -1);
		//				g.addEdge(6, 1, 1, -1);
		//				g.addEdge(6, 5, 1, -1);
		//				g.addEdge(5, 6, 1, -1);
		//				g.addEdge(2, 6, 1, -1);
		//				g.addEdge(5, 2, 1, -1);
		//		
		//		
		//				g.addEdge(95, 96, 1, -1);
		//				g.addEdge(96, 92, 1, -1);
		//				g.addEdge(92, 95, 1, -1);
		//		
		//				g.addEdge(94, 92, 1, -1);

		//		g.addEdge(1, 2, 1, -1);
		//		g.addEdge(2, 6, 1, -1);
		//		g.addEdge(2, 5, 1, -1);
		//		g.addEdge(2, 3, 1, -1);
		//		g.addEdge(5, 1, 1, -1);
		//		g.addEdge(7, 6, 1, -1);
		//		g.addEdge(3, 4, 1, -1);
		//		g.addEdge(3, 7, 1, -1);
		//		g.addEdge(4, 3, 1, -1);
		//		g.addEdge(4, 8, 1, -1);
		//		g.addEdge(5, 6, 1, -1);
		//		g.addEdge(7, 8, 1, -1);
		//		g.addEdge(6, 7, 1, -1);


//		g.addEdge(2, 1, 1, -1);
//		g.addEdge(1, 5, 1, -1);
//		g.addEdge(1, 6, 1, -1);
//		g.addEdge(1, 4, 1, -1);
//		g.addEdge(5, 6, 1, -1);
//		g.addEdge(6, 5, 1, -1);
		
		
		
		
		MinimalModel m = new MinimalModel();
		String path=".//CnfFile.txt";

		m.readfile(path);
//		m.ModuMinUsingDP();

		Graph<Integer> g = m.createModelGraph();

		// print the graph --> class: Graph
		System.out.println("############################33");
		System.out.println(g);
		System.out.println("############################33");

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> result = scc.scc(g);
		System.out.println("******\n"+result+"\n******\n");

		System.out.println("-------------------------------------------------------------------------------------------");
		SuperGraph super_graph = new SuperGraph(g);
		super_graph.printGraph();
		System.out.println("------------------------------");

		ArrayList<Client<Integer>> sourceVertex = new ArrayList<>();
		ArrayList<Vertex<Integer>> finalVertex = new ArrayList<>();

//		for(Vertex<Integer> v : super_graph.getSuperGraph().getAllVertex()) {
//			if(v.getAdjacentVertexes().size()==0)
//				finalVertex.add(v.getNameID());
//		}

		// default values if not entered
		int portNumber = 1500;
		String serverAddress = "localhost";
		ArrayList<Client<Integer>> clients = new ArrayList<>();
		Vertex<Integer> sink = new Vertex<Integer>(-100);
//		super_graph.addSinkVertex();

		for(Vertex<Integer> v: super_graph.getSuperGraph().getAllVertex()) {
			Client<Integer> client = new Client<>(serverAddress, portNumber, v.getId(), new Vertex<Integer>(v), m.getDS(), result);
			if(!client.start())
				return;
			clients.add(client);
			if(v.getParentVertexes().size()==0)
				sourceVertex.add(client);
			
			else if(v.getAdjacentVertexes().size() == 0){
				client.setLastVertex(true);
				sink.addParentVertex(v);
				finalVertex.add(v);
			}
		}

		Client<Integer> client = new Client<>(serverAddress, portNumber, sink.getId(), sink, m.getDS(), result);
		if(!client.start())
			return;
		clients.add(client);

		for(int i=0; i<1; i++) {
			for(Client<Integer> c: sourceVertex) {
				c.sendMessage("* "+i+" *");
			}
		}

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
