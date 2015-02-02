/**
 * 
 */
package edu.fudan.se.getbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.fudan.se.agent.AgentMessage;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.getbook.data.ProcedureLog;
import edu.fudan.se.getbook.service.PayToCBS;
import edu.fudan.se.getbook.service.QueryBookFromCBS;
import edu.fudan.se.getbook.service.QueryBookFromLibrary;
import edu.fudan.se.getbook.service.QuerySellerFromSHS;

/**
 * @author whh
 * 
 */
public class GetBookRunnable implements Runnable {

	public static BlockingQueue<AgentMessage> msgPool; // 消息队列，即消息池，可以直观理解为当前element的个人信箱

	private String bookname;
	// 各个优先级
	private int priorityLibrary;
	private int priorityShop;
	private int priorityCBS; // 书店
	private int prioritySHS; // 二手卖家

	private ArrayList<ProcedureLog> procedureLogList;

	private AideAgentInterface aideAgentInterface;

	String fromAgent = "";

	public GetBookRunnable(String bookname, int priorityLibrary,
			int priorityShop, int priorityCBS, int prioritySHS,
			ArrayList<ProcedureLog> procedureLogList,
			AideAgentInterface aideAgentInterface) {
		msgPool = new LinkedBlockingQueue<AgentMessage>();
		// this.isFinish = false;

		this.bookname = bookname;
		this.priorityLibrary = priorityLibrary;
		this.priorityShop = priorityShop;
		this.priorityCBS = priorityCBS;
		this.prioritySHS = prioritySHS;
		this.procedureLogList = procedureLogList;
		this.aideAgentInterface = aideAgentInterface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		newProcedureLog("Start to get a book.");

		if (priorityLibrary >= priorityShop) {// 先从图书馆借
			newProcedureLog("Try to borrow the book from library.");
			if (borrowFromLibrary()) {
				newProcedureLog("Borrowing from library successfully!");
				newProcedureLog("You have got the book: " + bookname + "!");
			} else {
				newProcedureLog("Borrowing from library failed!");
				newProcedureLog("Try to get the book from shop.");
				// 开始从书店买
				if (getBookFromShop()) {
					newProcedureLog("Getting from shop successfully!");
					newProcedureLog("You have got the book: " + bookname + "!");
				} else {
					// 整个获得图书过程失败
					newProcedureLog("Getting from shop failed!");
					newProcedureLog("You have not got the book: " + bookname
							+ "!");
				}
			}

		} else { // 先从书店买
			newProcedureLog("Try to get the book from shop.");
			if (getBookFromShop()) {
				newProcedureLog("Getting from shop successfully!");
				newProcedureLog("You have got the book: " + bookname + "!");
			} else {
				newProcedureLog("Getting from shop failed!");
				newProcedureLog("Try to borrow the book from library.");
				// 开始从图书馆借书
				if (borrowFromLibrary()) {
					newProcedureLog("Borrowing from library successfully!");
					newProcedureLog("You have got the book: " + bookname);
				} else {
					// 整个获得图书过程失败
					newProcedureLog("Borrowing from library failed!");
					newProcedureLog("You have not got the book: " + bookname
							+ "!");
				}
			}

		}
	}

	/**
	 * 从图书馆查书并且借书
	 * 
	 * @return
	 */
	private boolean borrowFromLibrary() {
		boolean ret = false;

		newProcedureLog("Querying from library...");
		String retFromLibrary = QueryBookFromLibrary
				.queryBookFromLibrary(bookname);

		AgentMessage borrowFromLib = new AgentMessage(
				AgentMessage.AMHeader.NewTask, AgentMessage.AMBody.ToDo);

		if (retFromLibrary.equals("")) { // 图书馆没有这本书，委托给人去查
			newProcedureLog("Querying Failed. Delegate people to query.");

			// 从图书馆1查
			AgentMessage queryFromLib = new AgentMessage(
					AgentMessage.AMHeader.NewTask,
					AgentMessage.AMBody.ToConfirm);
			queryFromLib.setDescription("Does library1 have the book: "
					+ bookname + " ?");
			aideAgentInterface.sendNewTaskMesToExternalAgent(queryFromLib);

			if (waitTaskRet()) { // 图书馆1有这本书
				newProcedureLog(fromAgent
						+ " confirmed that library1 has the book. Delegate people to borrow it.");
				// 委托人从图书馆借书
				borrowFromLib
						.setDescription("Please borrow the book from library1. Bookname: "
								+ bookname);
				aideAgentInterface.sendNewTaskMesToExternalAgent(borrowFromLib);
				if (waitTaskRet()) { // 从图书馆1借书成功
					newProcedureLog(fromAgent
							+ " borrowed the book from library1.");
					ret = true;
				} else {
					ret = false;
				}

			} else {// 图书馆1没有有这本书
					// 从图书馆2查
				queryFromLib.setDescription("Does library2 have the book: "
						+ bookname + " ?");
				aideAgentInterface.sendNewTaskMesToExternalAgent(queryFromLib);
				if (waitTaskRet()) { // 图书馆2有这本书
					newProcedureLog(fromAgent
							+ " confirmed that library2 has the book. Delegate people to borrow it.");
					// 委托人从图书馆借书
					borrowFromLib
							.setDescription("Please borrow the book from library2. Bookname: "
									+ bookname);
					aideAgentInterface
							.sendNewTaskMesToExternalAgent(borrowFromLib);
					if (waitTaskRet()) { // 从图书馆1借书成功
						newProcedureLog(fromAgent
								+ " borrowed the book from library2.");
						ret = true;
					} else {
						ret = false;
					}
				} else {
					// 必须进入开始从书店买
					newProcedureLog("Library does not have the book.");
					ret = false;
				}

			}

		} else {
			String lib = retFromLibrary.split("@")[1];
			newProcedureLog(lib
					+ " has the book. Delegate people to borrow it.");
			// 委托人从图书馆借书
			borrowFromLib.setDescription("Please borrow the book from " + lib
					+ ". Bookname: " + bookname);
			aideAgentInterface.sendNewTaskMesToExternalAgent(borrowFromLib);
			if (waitTaskRet()) { // 从图书馆借书成功
				newProcedureLog(fromAgent + " borrowed the book from " + lib
						+ ".");
				ret = true;
			} else {
				ret = false;
			}
		}

		return ret;
	}

	/**
	 * 从书店获得图书
	 * 
	 * @return
	 */
	private boolean getBookFromShop() {
		boolean ret = false;

		if (priorityCBS >= prioritySHS) { // 从新书店买
			newProcedureLog("Try to get the book from campus bookstore.");
			if (buyFromCBS()) {// 从新书店买成功
				newProcedureLog("You have bought the book from campus bookstore.");
				ret = true;
			} else {// 从新书店买失败
				newProcedureLog("Getting the book from campus bookstore failed.");
				newProcedureLog("Try to get the book from second-hand seller.");
				if (buyFromSHS()) { // 再尝试从二手卖家买
					newProcedureLog("You have bought the book from second-hand seller.");
					ret = true;
				} else {// 失败
					ret = false;
				}
			}

		} else { // 从二手卖家买
			newProcedureLog("Try to get the book from second-hand seller.");
			if (buyFromSHS()) {// 从二手卖家买成功
				newProcedureLog("You have bought the book from second-hand seller.");
				ret = true;
			} else {// 从二手卖家买失败
				newProcedureLog("Getting the book from second-hand seller failed.");
				newProcedureLog("Try to get the book from campus bookstore.");
				if (buyFromCBS()) {// 再尝试从新书店买
					newProcedureLog("You have bought the book from campus bookstore.");
					ret = true;
				} else {// 失败
					ret = false;
				}

			}

		}

		return ret;
	}

	/**
	 * 从新书店买书
	 * 
	 * @return
	 */
	private boolean buyFromCBS() {
		boolean ret = false;

		AgentMessage confirmToBuy = new AgentMessage(
				AgentMessage.AMHeader.NewTask, AgentMessage.AMBody.ToConfirm);

		String bookPrice = QueryBookFromCBS.queryBookFromCBS(bookname);
		if (bookPrice.equals("")) { // 查询web service发现书店没有
			// 委托给人去查
			AgentMessage queryFromCBS = new AgentMessage(
					AgentMessage.AMHeader.NewTask, AgentMessage.AMBody.ToInput);
			queryFromCBS
					.setDescription("Does bookstore have the book: " + bookname
							+ "? If so, please input the price of the book.");
			aideAgentInterface.sendNewTaskMesToExternalAgent(queryFromCBS);
			String price = waitQueryFromCBSRet();
			if (price.equals("")) { // 委托给人查的结果还是没有
				// 失败，该进行二手查询了
				ret = false;
			} else {// 委托给人查询的结果返回的是价钱
				confirmToBuy
						.setDescription("Would you like to buy the book from bookstore? Price is "
								+ price + ".");
				aideAgentInterface.sendNewTaskMesToSelfAgent(confirmToBuy);
				if (waitTaskRet()) {// 用户决定购买
					if (PayToCBS.payToCBS(price)) {// 支付成功
						newProcedureLog("Succeed in paying the book.");
						ret = true;
					} else {
						newProcedureLog("Failed in paying the book.");
						ret = false;
					}
				} else {// 用户决定不买
					newProcedureLog("You decided not to buy the book from bookstore.");
					ret = false;
				}
			}

		} else {
			confirmToBuy
					.setDescription("Would you like to buy the book from bookstore? Price is "
							+ bookPrice + ".");
			aideAgentInterface.sendNewTaskMesToSelfAgent(confirmToBuy);
			if (waitTaskRet()) {// 用户决定购买
				if (PayToCBS.payToCBS(bookPrice)) {// 支付成功
					newProcedureLog("Succeed in paying the book.");
					ret = true;
				} else {
					newProcedureLog("Failed in paying the book.");
					ret = false;
				}
			} else {// 用户决定不买
				newProcedureLog("You decided not to buy the book from bookstore.");
				ret = false;
			}
		}

		return ret;
	}

	/**
	 * 从二手卖家买书
	 * 
	 * @return
	 */
	private boolean buyFromSHS() {
		boolean ret = false;

		String sellerInfo = QuerySellerFromSHS.querySellerFromSHS(bookname);
		if (sellerInfo.equals("")) {// 没有二手卖家在卖这本书
			newProcedureLog("No second-hand seller selling the book: "
					+ bookname);
			ret = false;
		} else {// 有二手卖家在卖
			AgentMessage toSelectSeller = new AgentMessage(
					AgentMessage.AMHeader.NewTask, AgentMessage.AMBody.ToShow);
			toSelectSeller
					.setDescription("You have received some second-hand sellers' info. Please select one.");
			toSelectSeller.setContent(sellerInfo);
			aideAgentInterface.sendNewTaskMesToSelfAgent(toSelectSeller);

			String selectSeller = waitSelectSellerRet();
			if (selectSeller.equals("")) {// 没有选择二手卖家信息
				newProcedureLog("You did not select a second-hand seller.");
				ret = false;
			} else {
				AgentMessage toTrade = new AgentMessage(
						AgentMessage.AMHeader.NewTask, AgentMessage.AMBody.ToDo);
				toTrade.setDescription("Please trade with the seller face to face. Seller info: "
						+ selectSeller);
				aideAgentInterface.sendNewTaskMesToExternalAgent(toTrade);

				if (waitTaskRet()) {// 与二手卖家交易成功
					ret = true;
				} else {
					newProcedureLog("You did not trade with the seller.");
					ret = false;
				}
			}

		}

		return ret;
	}

	/**
	 * 在全局的procedureLogList中插入一条新的纪录
	 * 
	 * @param content
	 */
	private void newProcedureLog(String content) {
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		ProcedureLog newLog = new ProcedureLog(nowTime, content);
		procedureLogList.add(0,newLog);
	}

	/**
	 * 等待任务返回结果中
	 * 
	 * @return
	 */
	private boolean waitTaskRet() {
		boolean ret = false;
		boolean isFinish = false;
		while (!isFinish) {
			AgentMessage message = msgPool.poll();
			if (message != null) {
				if (message.getBody().equals(AgentMessage.AMBody.NoDelegateTo)) {
					ret = false;
				} else if (message.getBody()
						.equals(AgentMessage.AMBody.TaskRet)) {
					if (message.getContent().equals("YES")) {
						fromAgent = message.getFromAgent();
						ret = true;
					} else if (message.getContent().equals("NO")) {
						ret = false;
					}
				}
				isFinish = true;
			}
		}
		return ret;
	}

	/**
	 * 等待委托人从CBS查询书返回结果中
	 * 
	 * @return
	 */
	private String waitQueryFromCBSRet() {
		String ret = "";
		boolean isFinish = false;
		while (!isFinish) {
			AgentMessage message = msgPool.poll();
			if (message != null) {
				if (message.getBody().equals(AgentMessage.AMBody.NoDelegateTo)) {
					ret = "";
				} else if (message.getBody()
						.equals(AgentMessage.AMBody.TaskRet)) {
					if (message.getContent().equals("NO")) {
						ret = "";
					} else {
						ret = message.getContent();
					}
				}

			}

		}
		return ret;
	}

	/**
	 * 等待用户选择一个二手卖家中
	 * 
	 * @return
	 */
	private String waitSelectSellerRet() {
		String ret = "";
		boolean isFinish = false;
		while (!isFinish) {
			AgentMessage message = msgPool.poll();
			if (message != null) {
				if (message.getBody().equals(AgentMessage.AMBody.TaskRet)) {
					if (message.getContent().equals("NO")) {
						ret = "";
					} else {
						ret = message.getContent();
					}
				}
			}
		}
		return ret;
	}

}
