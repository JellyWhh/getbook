/**
 * 
 */
package edu.fudan.se.getbook.data.userMes;

/**
 * @author whh
 * 
 */
public class UserMessage {

	private String time;
	private String content;
	private boolean isDone;

	public UserMessage(String time, String content) {
		this.time = time;
		this.content = content;
		this.isDone = false;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public String getTime() {
		return time;
	}

}
