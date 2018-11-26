package service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.transform.Result;

import model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import utility.GoogleBookAPI;
import org.json.JSONObject;
import utility.DBConnection;

@WebService()
public class BookServiceImpl implements  BookService {
    @Override
    public Book getBook(String id) {
        GoogleBookAPI coba = new GoogleBookAPI("yulys");
        JSONObject hasilJSON = new JSONObject();
        hasilJSON = coba.searchBook();
        try {
            JSONArray authorsJSON = new JSONArray(hasilJSON.get("authors").toString());
            String[] authors = new String[authorsJSON.length()];
            for(int i = 0; i < authorsJSON.length(); i++) {
                authors[i] = (String) authorsJSON.get(i);
            }
            Book cobaBook = new Book((String) hasilJSON.get("id"), (String) hasilJSON.get("title"), authors, (String) hasilJSON.get("description"), (Integer) hasilJSON.get("price"));
            return cobaBook;
        } catch (JSONException err) {
            System.out.println(err);
        }

        return null;
    }

    @Override
    public int addBook(String id, int price) {
        DBConnection bookDb = new DBConnection();
        int result = bookDb.doPostQuery(String.format("INSERT INTO book_detail(id, price) VALUES(\"%s\", %d)", id, price));

        return result;
    }

    @Override
    public Book[] searchBook(String query) {
        GoogleBookAPI googleBookAPI = new GoogleBookAPI(query);
        JSONObject hasilJSON = new JSONObject();
        hasilJSON = googleBookAPI.searchBook();
        try {
            JSONArray resultItems = new JSONArray(hasilJSON.get("items").toString());
            Book[] bookResults = new Book[resultItems.length()];

            for(int i = 0; i < Math.min(5, resultItems.length()); i++) {
                String id = "";
                String title = "";
                String[] authors = {""};
                String description = "";
                Integer price = 0;

                JSONObject book = new JSONObject(resultItems.get(i).toString());
                id = book.get("id").toString();

                JSONObject volumeInfo = new JSONObject(book.get("volumeInfo").toString());
                if (volumeInfo.has("title")) {
                    title = volumeInfo.get("title").toString();
                }
                if (volumeInfo.has("authors")) {
                    JSONArray authorsJSON = new JSONArray(volumeInfo.get("authors").toString());
                    authors = new String[authorsJSON.length()];
                    for(int j = 0; j < authorsJSON.length(); j++) {
                        authors[j] = authorsJSON.get(j).toString();
                    }
                }
                if (volumeInfo.has("description")) {
                    description = volumeInfo.get("description").toString();
                }

                if (book.has("saleInfo")) {
                    JSONObject saleInfo = new JSONObject(book.get("saleInfo").toString());
                    if (saleInfo.has("listPrice")) {
                        JSONObject listPrice = new JSONObject(saleInfo.get("listPrice").toString());
                        if (listPrice.has("amount")) {
                            price = listPrice.getInt("amount");
                        }
                    }
                }

                Book bookResult = new Book(id, title, authors, description, price);
                bookResults[i] = bookResult;
            }

            return bookResults;
        } catch (JSONException err) {
            System.out.println(err);
        }

        return null;
    }
}
