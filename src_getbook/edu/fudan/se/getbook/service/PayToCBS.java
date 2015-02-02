/**
 * 
 */
package edu.fudan.se.getbook.service;

/**
 * @author whh
 * 
 */
public class PayToCBS {

	public static boolean payToCBS(String price) {

		int random = (int) (Math.random() * 10); // [0,10)
		if (random > 1) { // 80%的概率
			// 支付成功
			System.out.println("PayToCBS --------- pay succeed!");
			return true;
		} else {
			// 支付失败
			System.out.println("PayToCBS --------- pay failed!");
			return false;
		}
	}

}
