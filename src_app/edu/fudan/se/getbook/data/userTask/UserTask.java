/**
 * 
 */
package edu.fudan.se.getbook.data.userTask;

/**
 * @author whh
 * 
 */
public class UserTask {

	private String time;
	private String fromAgent;
	private String description;
	private boolean isDone;

	public UserTask(String time, String fromAgent,String description) {
		this.time = time;
		this.fromAgent = fromAgent;
		this.description = description;
		this.isDone = false;
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

	public String getFromAgent() {
		return fromAgent;
	}

	public String getDescription() {
		return description;
	}

	

}
