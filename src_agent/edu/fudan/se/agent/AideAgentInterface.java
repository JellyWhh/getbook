/**
 * 
 */
package edu.fudan.se.agent;

/**
 * @author whh
 *
 */
public interface AideAgentInterface {
	
	public void sendNewTaskMesToExternalAgent(AgentMessage message);
	public void sendNewTaskMesToSelfAgent(AgentMessage message);
	
	public void sendTaskRetToExternalAgent(AgentMessage message);
	
}
