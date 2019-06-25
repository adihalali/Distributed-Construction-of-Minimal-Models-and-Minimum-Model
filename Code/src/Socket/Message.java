package Socket;

import java.io.Serializable;
import java.util.HashMap;

public class Message implements Serializable{
	
	private String message, sender, receiver;
	private boolean isLast;
	private HashMap<Integer, Boolean> literalMap;
	
	public Message(String message, String reciver, String sender, HashMap<Integer, Boolean> hm,  boolean last) {
		this.message = message;
		this.receiver = reciver;
		this.sender = sender;
		this.isLast = last;
				
		literalMap = hm;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
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

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
	
	
}
