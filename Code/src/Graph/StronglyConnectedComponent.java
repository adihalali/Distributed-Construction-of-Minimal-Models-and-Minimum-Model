package Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StronglyConnectedComponent {

	public List<Set<Vertex<Integer>>> scc(Graph<Integer> graph) {
		//it holds vertices by finish time in reverse order.
		Deque<Vertex<Integer>> stack = new ArrayDeque<>();
		//holds visited vertices for DFS.
		Set<Vertex<Integer>> visited = new HashSet<>();

		//populate stack with vertices with vertex finishing last at the top.
		for (Vertex<Integer> vertex : graph.getAllVertex()) {
			if (visited.contains(vertex)) 
				continue;
			DFSUtil(vertex, visited, stack);
		}

		//reverse the graph.
		Graph<Integer> reverseGraph = reverseGraph(graph);

		//Do a DFS based off vertex finish time in decreasing order on reverse graph..
		visited.clear();
		
		List<Set<Vertex<Integer>>> result = new ArrayList<>();
		while (!stack.isEmpty()) {
			Vertex<Integer> vertex = reverseGraph.getVertex(stack.poll().getId());
			if(visited.contains(vertex))
				continue;
			
			Set<Vertex<Integer>> set = new HashSet<>();
			DFSUtilForReverseGraph(vertex, visited, set);
			result.add(set);
		}
		return result;
	}

	private Graph<Integer> reverseGraph(Graph<Integer> graph) {
		Graph<Integer> reverseGraph = new Graph<>(true);

		for(Vertex<Integer> v: graph.getAllVertex()){
			reverseGraph.addSingleVertex(v.getId());
			for (Edge<Integer> edge : graph.getAllEdges()) 
				if(edge.getVertexS().equals(v)) 
					reverseGraph.addEdge(edge.getVertexT().getId(), edge.getVertexS().getId(), edge.getWeight(), -1);
		}
		return reverseGraph;
	}

	private void DFSUtil(Vertex<Integer> vertex, Set<Vertex<Integer>> visited, Deque<Vertex<Integer>> stack) {
		visited.add(vertex);
		for (Vertex<Integer> v : vertex.getAdjacentVertexes()) {
			if (visited.contains(v)) 
				continue;
			DFSUtil(v, visited, stack);
		}
		stack.offerFirst(vertex);
	}

	private void DFSUtilForReverseGraph(Vertex<Integer> vertex, Set<Vertex<Integer>> visited, Set<Vertex<Integer>> set) {
		visited.add(vertex);
		set.add(vertex);

		for (Vertex<Integer> v : vertex.getAdjacentVertexes()) {
			if ( visited.contains(v)) 
				continue;
			DFSUtilForReverseGraph(v, visited, set);
		}
	}

	public static void main(String args[]){
		Graph<Integer> graph = new Graph<>(true);
		//        graph.addEdge(0, 1);
		//        graph.addEdge(1, 2);
		//        graph.addEdge(2, 0);
		//        graph.addEdge(1, 3);
		//        graph.addEdge(3, 4);
		//        graph.addEdge(4, 5);
		//        graph.addEdge(5, 3);
		//        graph.addEdge(5, 6);

		graph.addEdge(10, 3, 1, -1);
		graph.addEdge(10, 4, 1, -1);
		graph.addEdge(1, 10, 1, -1);
		graph.addEdge(4, 1, 1, -1);
		graph.addEdge(3, 1, 1, -1);
		graph.addEdge(1, 5, 1, -1);
		graph.addEdge(1, 2, 1, -1);
		graph.addEdge(2, 1, 1, -1);
		graph.addEdge(6, 1, 1, -1);
		graph.addEdge(6, 5, 1, -1);
		graph.addEdge(5, 6, 1, -1);
		graph.addEdge(2, 6, 1, -1);
		graph.addEdge(5, 2, 1, -1);


		graph.addEdge(95, 96, 1, -1);
		graph.addEdge(96, 92, 1, -1);
		graph.addEdge(92, 95, 1, -1);

		graph.addEdge(94, 92, 1, -1);

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> result = scc.scc(graph);

		//print the result
		result.forEach(set -> {
			set.forEach(v -> System.out.print(v.getId() + " "));
			System.out.println();
		});
	}
}