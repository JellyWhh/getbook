/**
 * 
 */
package edu.fudan.se.getbook.data;

/**
 * 展示给用户看的过程日志
 * 
 * @author whh
 * 
 */
public class ProcedureLog {

	private String time;
	private String content;

	public ProcedureLog(String time, String content) {
		this.time = time;
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public String getContent() {
		return content;
	}
}
