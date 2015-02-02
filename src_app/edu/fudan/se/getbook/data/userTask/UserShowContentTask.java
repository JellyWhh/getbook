/**
 * 
 */
package edu.fudan.se.getbook.data.userTask;

/**
 * @author whh
 *
 */
public class UserShowContentTask extends UserTask {
	
	private String content;

	/**
	 * @param time
	 * @param fromAgent
	 * @param description
	 */
	public UserShowContentTask(String time, String fromAgent, String description) {
		super(time, fromAgent, description);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
