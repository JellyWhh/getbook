/**
 * 
 */
package edu.fudan.se.agent;

import java.io.Serializable;

/**
 * @author whh
 *
 */
public class AgentMessage implements Serializable{
	
	private static final long serialVersionUID = 8483512936845331089L;
	
	private AMHeader header;
	private String fromAgent;
	private String toAgent;
	private AMBody body;
	private String content;
	private String description;
	
	public AgentMessage(AMHeader header, AMBody body){
		this.header = header;
		this.body = body;
	}
	
	public static enum AMHeader implements Serializable{
		NewTask,TaskRet;
	}
	
	public static enum AMBody implements Serializable{
		NoDelegateTo,
		ToConfirm,ToDo,ToInput,ToShow,TaskRet;
	}

	public String getFromAgent() {
		return fromAgent;
	}

	public void setFromAgent(String fromAgent) {
		this.fromAgent = fromAgent;
	}

	public String getToAgent() {
		return toAgent;
	}

	public void setToAgent(String toAgent) {
		this.toAgent = toAgent;
	}

	public AMHeader getHeader() {
		return header;
	}

	public AMBody getBody() {
		return body;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
