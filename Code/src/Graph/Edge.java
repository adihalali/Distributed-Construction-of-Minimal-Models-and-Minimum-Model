package Graph;


public class Edge<T>{
    private boolean isDirected = false;
    private Vertex<T> Svertex;
    private Vertex<T> Tvertex;
    private int weight;
    private int sizeOfS;
    
    public Edge(Vertex<T> sVertex, Vertex<T> tVertex){
        this.Svertex = sVertex;
        this.Tvertex = tVertex;
    }
    
    public Edge(Vertex<T> sVertex, Vertex<T> tVertex,boolean isDirected){
        this.Svertex = sVertex;
        this.Tvertex = tVertex;
        this.isDirected = isDirected;
    }
    
    public Edge(Vertex<T> sVertex, Vertex<T> tVertex,boolean isDirected,int weight){
        this.Svertex = sVertex;
        this.Tvertex = tVertex;
        this.weight = weight;
        this.isDirected = isDirected;
    }
    public Edge(Vertex<T> sVertex, Vertex<T> tVertex,boolean isDirected,int weight,int s){
        this.Svertex = sVertex;
        this.Tvertex = tVertex;
        this.weight = weight;
        this.isDirected = isDirected;
        this.sizeOfS=s;
    }
    
    public Vertex<T> getVertexS(){
        return this.Svertex;
    }
    
    public Vertex<T> getVertexT(){
        return this.Tvertex;
    }
    
    public void setSizeOfS(int s) {
    	this.sizeOfS=s;
    }
    
    public int getSSize() {
    	return this.sizeOfS;
    }
    public int getWeight(){
        return this.weight;
    }
    
    public boolean isDirected(){
        return this.isDirected;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.Svertex == null) ? 0 : this.Svertex.hashCode());
        result = prime * result + ((this.Tvertex == null) ? 0 : this.Tvertex.hashCode());
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
		Edge<T> other = (Edge<T>) obj;
        if (this.Svertex == null) {
            if (other.Svertex != null)
                return false;
        } else if (!this.Svertex.equals(other.Svertex))
            return false;
        if (this.Tvertex == null) {
            if (other.Tvertex != null)
                return false;
        } else if (!this.Tvertex.equals(other.Tvertex))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Edge [isDirected=" + isDirected + ", vertex1=" + this.Svertex
                + ", vertex2=" + this.Tvertex + ", weight=" + weight + ", size of S is :"+sizeOfS+ "]";
    }
}