package Socket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable{
	
	private String sender, receiver;
	private HashMap<Integer, Boolean> literalMap;
	private ArrayList<Integer> models;
	
	public Message(String reciver, String sender, HashMap<Integer, Boolean> hm, ArrayList<Integer> m) {
		this.receiver = reciver;
		this.sender = sender;
		this.models = m;
				
		literalMap = hm;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public HashMap<Integer, Boolean> getLiteralMap() {
		return this.literalMap;
	}

	public void setLiteralMap(HashMap<Integer, Boolean> lm) {
		this.literalMap = lm;
	}

	public ArrayList<Integer> getModels() {
		return this.models;
	}

	public void setModels(ArrayList<Integer> m) {
		this.models = m;
	}
	
	
}
