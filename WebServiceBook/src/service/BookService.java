package service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import model.Book;

import java.sql.ResultSet;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BookService {
    @WebMethod
    Book getBookDetail(String id);

    @WebMethod
    public List<Book> searchBook(String query);


    @WebMethod
    public Book recommendationBook(String[] categories);
}
