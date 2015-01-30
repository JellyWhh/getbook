/**
 * 
 */
package edu.fudan.se.getbook.initial;

import java.util.ArrayList;

import edu.fudan.se.getbook.data.ProcedureLog;
import edu.fudan.se.getbook.data.userMes.UserMessage;

import android.app.Application;

/**
 * @author whh
 *
 */
public class GBApplication extends Application {
	
	private String agentNickname;
	
	private ArrayList<UserMessage> userMessageList;
	private ArrayList<ProcedureLog> procedureLogList;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initialData();
//		initialJadePreferences();
	}
	
	private void initialData(){
		this.procedureLogList = new ArrayList<ProcedureLog>();
		this.userMessageList = new ArrayList<UserMessage>();
	}
	
	

	public String getAgentNickname() {
		return agentNickname;
	}

	public void setAgentNickname(String agentNickname) {
		this.agentNickname = agentNickname;
	}

	public ArrayList<UserMessage> getUserMessageList() {
		return userMessageList;
	}

	public ArrayList<ProcedureLog> getProcedureLogList() {
		return procedureLogList;
	}

}
