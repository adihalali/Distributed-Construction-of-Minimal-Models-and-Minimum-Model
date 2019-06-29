package Test;


import java.util.ArrayList;
import Graph.SuperGraph;
import Graph.Vertex;
import MinimalModel.MinimalModel;
import Socket.Client;

public class MainProgram {

	public static void main(String[] args) {
		int portNumber = 1500;	// start server on port 1500 unless a PortNumber is specified 
		MinimalModel m = new MinimalModel();
		String path=".//CnfFile.txt";

		m.readfile(path);
		SuperGraph SP = m.createModelGraph();


		ArrayList<Client<Integer>> sourceVertex = new ArrayList<>();
		ArrayList<Vertex<Integer>> finalVertex = new ArrayList<>();

		// default values if not entered
		String serverAddress = "localhost";
		ArrayList<Client<Integer>> clients = new ArrayList<>();
		Vertex<Integer> sink = new Vertex<Integer>(-100);

		for(Vertex<Integer> v: SP.getSuperGraph().getAllVertex()) {
			Client<Integer> client = new Client<>(serverAddress, portNumber, v.getId(), v, m.getDS(), m.getSCC());
			if(!client.start())
				return;
			clients.add(client);
			if(v.getParentVertexes().size()==0)
				sourceVertex.add(client);

			if(v.getAdjacentVertexes().size() == 0){
				client.setLastVertex(true);
				sink.addParentVertex(v);
				finalVertex.add(v);
			}
		}
	

		Client<Integer> client = new Client<>(serverAddress, portNumber, sink.getId(), sink, m.getDS(), m.getSCC());
		if(!client.start())
			return;
		clients.add(client);

		/***run time checking*/
				long startTime,endTime,totalTime;
				
				
		startTime = System.nanoTime();
//		System.out.println("start: "+ startTime);
		for(Client<Integer> c: sourceVertex) {
			System.out.println("start: "+ startTime);
			c.findMinimalModels();
		}
	}
}
