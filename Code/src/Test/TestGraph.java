package Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Graph.Graph;
import Graph.StronglyConnectedComponent;
import Graph.SuperGraph;
import Graph.Vertex;
import Graph.VertexStructure;
import Socket.Client;

public class TestGraph {
	public static void main(String[] args) {
		Graph<Integer> g = new Graph<>(true);
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


		g.addEdge(2, 1, 1, -1);
		g.addEdge(1, 5, 1, -1);
		g.addEdge(1, 6, 1, -1);
		g.addEdge(1, 4, 1, -1);
		g.addEdge(5, 6, 1, -1);
		g.addEdge(6, 5, 1, -1);
	

		// print the graph --> class: Graph
		System.out.println(g);

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> result = scc.scc(g);
		System.out.println("******\n"+result+"\n******\n");


		System.out.println("-------------------------------------------------------------------------------------------");
		SuperGraph super_graph = new SuperGraph(g);
		super_graph.printGraph();
		System.out.println("------------------------------");

		ArrayList<VertexStructure> vs = new ArrayList<>();
		ArrayList<Client> sourceVertex = new ArrayList<>();
		ArrayList<String> finalVertex = new ArrayList<>();

		for(Vertex<Integer> v : super_graph.getSuperGraph().getAllVertex()) {
			VertexStructure tmp = new VertexStructure(v, super_graph);
			vs.add(tmp);

			if(tmp.getChild().size()==0)
				finalVertex.add(tmp.getId());
		}

		// default values if not entered
		int portNumber = 1500;
		String serverAddress = "localhost";
		ArrayList<Client> clients = new ArrayList<>();

		for(VertexStructure v: vs) {
			Client client = new Client(serverAddress, portNumber, v.getId(), v.getParent(), v.getChild());
			if(!client.start())
				return;
			clients.add(client);
			if(v.getParent().size()==0)
				sourceVertex.add(client);
		}

		Client client = new Client(serverAddress, portNumber, "sink", finalVertex, new ArrayList<>());
		if(!client.start())
			return;
		clients.add(client);

		for(int i=0; i<2; i++) {
			for(Client c: sourceVertex) {
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
