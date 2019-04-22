package Socket;

import java.io.Serializable;

public class Message implements Serializable{
	
	private String message, sender, receiver;
	private boolean isLast;
	
	public Message(String message, String reciver, String sender, boolean last) {
		this.message = message;
		this.receiver = reciver;
		this.sender = sender;
		this.isLast = last;
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

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
	
	
}
