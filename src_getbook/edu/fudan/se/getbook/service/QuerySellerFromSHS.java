/**
 * 
 */
package edu.fudan.se.getbook.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author whh
 * 
 */
public class QuerySellerFromSHS {

	public static String querySellerFromSHS(String bookname) {
		String ret = "";

		HashMap<String, HashMap<String, String>> seller = new HashMap<>();

		HashMap<String, String> bookList1 = new HashMap<String, String>();
		bookList1.put("Java Web Service", "40");
		bookList1.put("OSGi in Action", "20");
		bookList1.put("Java Script", "30");

		HashMap<String, String> bookList2 = new HashMap<String, String>();
		bookList2.put("Java Web Service", "20");
		bookList2.put("OSGi in Action", "40");
		bookList2.put("Data Mining", "30");

		seller.put("YuHan", bookList1);
		seller.put("ChaiNing", bookList2);

		HashMap<String, String> addrs = new HashMap<>();
		addrs.put("YuHan", "TeachingBuilding");
		addrs.put("ChaiNing", "MEBuilding");

		ArrayList<String> sellerInfoList = new ArrayList<>();

		for (String selleName : seller.keySet()) {
			if (seller.get(selleName).keySet().contains(bookname)) {
				String listItem = "Seller:" + selleName + ";Price:";
				String price = seller.get(selleName).get(bookname);
				listItem += price + ";Addr:" + addrs.get(selleName) + ";Book:"
						+ bookname;
				sellerInfoList.add(listItem);
			}
		}

		if (sellerInfoList.isEmpty()) {
			ret = "";
		} else {
			for (String item : sellerInfoList) {
				ret += item + "###";
			}
		}

		return ret;
	}

}
