package Rules;

import Rules.LinkedList;

//this class represent one rule
public class Rule {
	public LinkedList body;
	public LinkedList head;
	
	public Rule(){
		body = new LinkedList();
		head = new LinkedList();
	}
	
	protected void addToBody(int num){
		body.addAtTail(num);
	}
	
	protected void addToHead(int num){
		head.addAtTail(num);
	}
	
	protected int getSize(){
		return body.getSize() + head.getSize();
	}
}
