package Graph;

import java.util.ArrayList;

public class VertexStructure {
	private String id;
	private ArrayList<String> parent, child;
	
	public VertexStructure(Vertex<Integer> v, SuperGraph sg) {
		this.id = v.getNameID();
		this.parent = sg.getParentID(v);
		this.child = sg.getChildrenID(v);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getParent() {
		return parent;
	}

	public void setParent(ArrayList<String> parent) {
		this.parent = parent;
	}

	public ArrayList<String> getChild() {
		return child;
	}

	public void setChild(ArrayList<String> child) {
		this.child = child;
	}
	
	

}
