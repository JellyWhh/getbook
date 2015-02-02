/**
 * 
 */
package edu.fudan.se.getbook.initial;

import jade.util.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

import edu.fudan.se.getbook.data.ProcedureLog;
import edu.fudan.se.getbook.data.userTask.UserTask;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * @author whh
 * 
 */
public class GBApplication extends Application {

	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	private String agentNickname;

	private ArrayList<UserTask> userUnreadTaskList;
	private ArrayList<UserTask> userReadTaskList;
	private ArrayList<ProcedureLog> procedureLogList;

	// 各个优先级
	private int priorityLibrary = 1;
	private int priorityShop = 0;
	private int priorityCBS = 1; // 书店
	private int prioritySHS = 0; // 二手卖家

	@Override
	public void onCreate() {
		super.onCreate();
		initialData();
		initialJadePreferences();
	}

	private void initialData() {
		this.procedureLogList = new ArrayList<ProcedureLog>();
		this.userUnreadTaskList = new ArrayList<UserTask>();
		this.userReadTaskList = new ArrayList<UserTask>();
	}

	/**
	 * zjh所写代码，把jade需要的相关属性初始化
	 */
	private void initialJadePreferences() {
		SharedPreferences settings = getSharedPreferences("jadeChatPrefsFile",
				0);

		String defaultHost = settings.getString("defaultHost", "");
		String defaultPort = settings.getString("defaultPort", "");
		if (defaultHost.isEmpty() || defaultPort.isEmpty()) {
			logger.log(Level.INFO, "Create default properties");
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("defaultHost", "10.131.253.133"); // 改成jade平台的ip
			editor.putString("defaultPort", "1099");
			editor.commit();
		}
	}

	public String getAgentNickname() {
		return agentNickname;
	}

	public void setAgentNickname(String agentNickname) {
		this.agentNickname = agentNickname;
	}

	public ArrayList<UserTask> getUserUnreadTaskList() {
		return userUnreadTaskList;
	}

	public ArrayList<UserTask> getUserReadTaskList() {
		return userReadTaskList;
	}

	public ArrayList<ProcedureLog> getProcedureLogList() {
		return procedureLogList;
	}

	public int getPriorityLibrary() {
		return priorityLibrary;
	}

	public void setPriorityLibrary(int priorityLibrary) {
		this.priorityLibrary = priorityLibrary;
	}

	public int getPriorityShop() {
		return priorityShop;
	}

	public void setPriorityShop(int priorityShop) {
		this.priorityShop = priorityShop;
	}

	public int getPriorityCBS() {
		return priorityCBS;
	}

	public void setPriorityCBS(int priorityCBS) {
		this.priorityCBS = priorityCBS;
	}

	public int getPrioritySHS() {
		return prioritySHS;
	}

	public void setPrioritySHS(int prioritySHS) {
		this.prioritySHS = prioritySHS;
	}

}
