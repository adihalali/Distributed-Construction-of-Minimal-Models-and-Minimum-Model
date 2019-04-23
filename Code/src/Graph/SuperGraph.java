package Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import Graph.Graph;

public class SuperGraph {
	
	private Graph<Integer> super_graph;
	
	public SuperGraph(Graph<Integer> graph) {
		super_graph = new Graph<>(true);
		
		if(graph==null) {
			System.out.println("graph is empty!!!!");
			return;
		}
		
		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> CClist = scc.scc(graph);
		
		int id =1;
		for(Set<Vertex<Integer>> set: CClist) {
			super_graph.addSingleVertex(id);
			super_graph.getVertex(id).setCCList(set);
			id++;
		}
		
		int x,y;
		boolean flag;
		
		for(Edge<Integer> edge : graph.getAllEdges()) {
			flag= true;
			x=findIdInVertexCC(super_graph,(int) edge.getVertexS().getId());
			y=findIdInVertexCC(super_graph,(int) edge.getVertexT().getId());

			for(Edge<Integer> e : super_graph.getAllEdges()) {
				if(e.getVertexS().getId()==x && e.getVertexT().getId()==y) {
					flag = false;
					break;
				}
			}

			if( -1==y || x==-1 || !flag) 
				continue;
			else if(x!=y) 
				super_graph.addEdge(x, y, 0, -1);
		}
	}	

	public ArrayList<Vertex<Integer>> getParent(Vertex<Integer> v){
		ArrayList<Vertex<Integer>> parentArray = new ArrayList<>();
		for(Edge<Integer> edge: super_graph.getAllEdges()) 
			if(edge.getVertexT().equals(v)) 
				parentArray.add(edge.getVertexS());
		return parentArray;
	} 
	
	public ArrayList<Vertex<Integer>> getChildren(Vertex<Integer> v){
		ArrayList<Vertex<Integer>> childrenArray = new ArrayList<>();
		for(Edge<Integer> edge: super_graph.getAllEdges()) 
			if(edge.getVertexS().equals(v)) 
				childrenArray.add(edge.getVertexT());
		return childrenArray;
	} 
	
	
	public ArrayList<String> getParentID(Vertex<Integer> v){
		ArrayList<String> parent = new ArrayList<>();
		for(Edge<Integer> edge: super_graph.getAllEdges()) 
			if(edge.getVertexT().equals(v)) 
				parent.add(edge.getVertexS().getNameID());
		return parent;
	} 
	
	public ArrayList<String> getChildrenID(Vertex<Integer> v){
		ArrayList<String> children = new ArrayList<>();
		for(Edge<Integer> edge: super_graph.getAllEdges()) 
			if(edge.getVertexS().equals(v)) 
				children.add(edge.getVertexT().getNameID());
		return children;
	}
	
    /// find vertex of original graph in super graph, return id of where in superGraph it was found (id of superGraph vertex) -1 if not found  
	public int findIdInVertexCC(Graph<Integer> graph,int id) {
		if(graph==null)
			return -1;
		
		for(Vertex<Integer> v : graph.getAllVertex()) 
			for(Vertex<Integer> vertexInCC: v.getCCList() ) 
				if(vertexInCC.getId()==id) 
					return (int) v.getId();

		return -1;
	}

	public Graph<Integer> getSuperGraph() {
		return this.super_graph;
	}

	public void printGraph() {
		System.out.println("********Super Graph**********");
		System.out.println(super_graph);
		System.out.println();
		for(Vertex<Integer> v : super_graph.getAllVertex()) {
			System.out.print("Vertex: "+v.getId());
			
			System.out.print(":=> Vertex of cc are: ");
			for(Vertex<Integer> vertexInCC: v.getCCList() ) {
				System.out.print(vertexInCC.getId()+"-> ");
				
			}
			System.out.println("Parent are: "+ getParent(v)+" Child are: "+getChildren(v));
			System.out.println();
		}
		System.out.println("********Super Graph ID**********");
		for(Vertex<Integer> v : super_graph.getAllVertex()) {
			System.out.print("Vertex: "+v.getNameID());
			
			System.out.print(":=> Vertex of cc are: ");
			for(Vertex<Integer> vertexInCC: v.getCCList() ) {
				System.out.print(vertexInCC.getNameID()+"-> ");
				
			}
			System.out.println("Parent are: "+ getParentID(v)+" Child are: "+getChildrenID(v));
			System.out.println();
		}
		
		
	}
}
