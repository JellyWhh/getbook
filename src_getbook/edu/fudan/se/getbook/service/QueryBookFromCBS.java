/**
 * 
 */
package edu.fudan.se.getbook.service;

import java.util.HashMap;

/**
 * @author whh
 * 
 */
public class QueryBookFromCBS {

	public static String queryBookFromCBS(String bookname) {
		String bookPrice = "";
		// 这里应该是调用图书馆的web service来返回结果
		HashMap<String, String> bookList = new HashMap<String, String>();
		bookList.put("OSGi in Action", "99");
		bookList.put("Software Engineering", "189");
		bookList.put("Thinking in Java", "159");
		bookList.put("Design Patterns", "49");

		if (bookList.keySet().contains(bookname)) {
			bookPrice = bookList.get(bookname);
		}
		return bookPrice;
	}

}
