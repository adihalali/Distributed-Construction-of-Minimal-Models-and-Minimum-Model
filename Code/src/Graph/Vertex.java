package Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Vertex<T> {
    private long id;
    private String nameID;
    private T data;
    private List<Edge<T>> edges = new ArrayList<>();
    private List<Vertex<T>> adjacentVertex = new ArrayList<>();
    private List<Vertex<T>> parentVertex = new ArrayList<>();
    private Set<Vertex<Integer>> CCList;
    
    public Vertex(long id){
        this.id = id;
        this.nameID = "vertex"+id;
    }
    
    public Vertex(Vertex<T> v) {
    	id = v.id;
    	nameID = v.nameID;
    	data = v.data;
    	
    	edges = new ArrayList<>();
    	edges.addAll(v.getEdges());
    	adjacentVertex = new ArrayList<>();
    	adjacentVertex.addAll(v.getAdjacentVertexes());
    	parentVertex = new ArrayList<>();
    	parentVertex.addAll(v.getParentVertexes());
    	CCList = v.getCCList();
    	CCList.addAll(v.getCCList());
    }
    

    public Set<Vertex<Integer>> getCCList(){
    	return this.CCList;
    }
    
    public void setCCList(Set<Vertex<Integer>> CCList) {
    	this.CCList=CCList;
    }
	public long getId(){
        return this.id;
    }
	
	public String getNameID() {
		return this.nameID;
	}
    
    public void setData(T data){
        this.data = data;
    }
    
  
    public T getData(){
        return this.data;
    }
    
    public void addAdjacentVertex(Edge<T> e, Vertex<T> v){
    	this.edges.add(e);
    	this.adjacentVertex.add(v);
    }
    
    public void addParentVertex(Vertex<T> v){
    	this.parentVertex.add(v);
    }
    
    public String toString(){
        return String.valueOf(this.id);
    }
    
    public List<Vertex<T>> getAdjacentVertexes(){
        return this.adjacentVertex;
    }
    
    public List<Vertex<T>> getParentVertexes(){
        return this.parentVertex;
    }
    
    public List<Edge<T>> getEdges(){
        return this.edges;
    }
    
    public int getDegree(){
        return this.edges.size();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
		Vertex<T> other = (Vertex<T> ) obj;
        if (this.id != other.id)
            return false;
        return true;
    }
}