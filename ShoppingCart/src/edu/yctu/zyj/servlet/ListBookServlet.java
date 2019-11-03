package edu.yctu.zyj.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.yctu.zyj.db.BookDB;
import edu.yctu.zyj.entity.Book;

/**
 * 图书列表servlet
 * 
 * @author zyj
 *
 */
public class ListBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListBookServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        Collection<Book> books = BookDB.getAll();
        out.write("本站提供的图书有: <br />");
        for (Book book : books) {
            String url = "/ShoppingCart/PurchaseServlet?id=" + book.getId();
            out.write(book.getName() + "<a href='" + url + "'>点击购买</a><br />");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
