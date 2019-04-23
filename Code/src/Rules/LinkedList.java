package Rules;

public class LinkedList{
	
	public class Node{
		//Declare class variables
		public Node next;
		public int var;
	
		public Node(int dat){
			var = dat;
		}
	}
	
	//Class variables for the Linked List
	public  Node head;
	public int numNodes;
	
	public LinkedList(){
		this.head = null;
		this.numNodes = 0;
	}
	
	public void addAtHead(int dat){
		Node temp = head;
		head = new Node(dat);
		head.next = temp;
		this.numNodes++;
	}
	
	public void addAtTail(int dat){
		Node temp = head;
		if(temp == null){
			head = new Node(dat);
			this.numNodes++;
			return;
		}
		
		while(temp.next != null)
			temp = temp.next;
		
		temp.next = new Node(dat);
		this.numNodes++;
	}
	
	public void addAtIndex(int index, int dat){
		Node temp = head;
		Node holder;
		
		for(int i=0; i < index-1 && temp.next != null; i++)
			temp = temp.next;
		
		holder = temp.next;
		temp.next = new Node(dat);
		temp.next.next = holder;
		this.numNodes++;
	}
	
	public void deleteAtIndex(int index){
		if(index == 0){
			head = head.next;
			this.numNodes--;
			return;
		}
		
		Node temp = head;
		for(int i=0; i< index - 1 && temp.next != null; i++)
			temp = temp.next;
		
		temp.next = temp.next.next;
		this.numNodes--;
	}
		
	public void printList(){
		Node temp = head;
		System.out.print("[");
		
		while(temp != null){
			System.out.print(temp.var);
			if(temp.next != null)
				System.out.print(", ");
			temp = temp.next;
		}
		
		System.out.println("]");
	}
	
	public void deleteList(){
		head = null;
		numNodes = 0;
	}
	
	public int getSize(){
		return numNodes;
	}
	
	public boolean contains(int var){
		Node n = head;
		
		while(n != null){
			if(var == n.var)
				return true;
			n = n.next;
		}
		
		return false;
	}
	
	public boolean isEmpty(){
		return numNodes == 0;
	}


	/*
	public static void main(String [] args)
	{
		System.out.println("/=/=/=/= TESTING /=/=/=/=");
		LinkedList ll = new LinkedList();
		ll.addAtTail(12);
		ll.addAtTail(15);
		ll.deleteAtIndex(1);
		
		
		ll.printList();
		
	}*/
	
	
	
}
