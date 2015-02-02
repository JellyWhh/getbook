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
public class QueryBookFromLibrary {

	/**
	 * 这里应该是用ksoap调用web service来返回结果
	 * 
	 * @param bookname 要查的书名
	 * @return bookname@library
	 */
	public static String queryBookFromLibrary(String bookname) {
		String ret = "";

		HashMap<String, ArrayList<String>> libraryBookList = new HashMap<>();

		// 这里应该是调用图书馆的web service来返回结果
		ArrayList<String> bookList1 = new ArrayList<>();
		bookList1.add("Thinking in Java");
		bookList1.add("Computer Systems Architecture");

		ArrayList<String> bookList2 = new ArrayList<>();
		bookList2.add("Thinking in Java");
		bookList2.add("Design Patterns");

		libraryBookList.put("Library1", bookList1);
		libraryBookList.put("Library2", bookList2);

		for (String lib : libraryBookList.keySet()) {
			ArrayList<String> bookList = libraryBookList.get(lib);
			if (bookList.contains(bookname)) {
				ret = bookname + "@" + lib;
				break;
			}
		}
		return ret;
	}

}
