package edu.yctu.zyj.db;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.yctu.zyj.entity.Book;

/**
 * 数据库模拟类
 * 
 * @author zyj
 *
 */
public class BookDB {

    private static Map<String, Book> books = new LinkedHashMap<>();
    static {
        books.put("1", new Book("1", "javaweb 开发"));
        books.put("2", new Book("2", "jdbc 开发"));
        books.put("3", new Book("3", "java 基础"));
        books.put("4", new Book("4", "struts 开发"));
        books.put("5", new Book("5", "spring 开发"));
    }

    /**
     * 获取所有图书
     * 
     * @return
     */
    public static Collection<Book> getAll() {
        return books.values();
    }

    /**
     * 通过id获得图书
     * 
     * @param id
     * @return
     */
    public static Book getBook(String id) {
        return books.get(id);
    }
}
