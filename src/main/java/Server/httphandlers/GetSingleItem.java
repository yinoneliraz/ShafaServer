package Server.httphandlers;

import MySQL.MySQLQueryExecutor;
import Server.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Yinon on 25/05/2017.
 */
public class GetSingleItem implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        JSONObject json = null;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        JSONObject postData=null;
        try {
            postData= Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println("ERROR: SelectHandler,handle,parseQuery, on query: " + query);
        }

        try {
            query=Constants.getSingleItemQuery(postData);
            json = MySQLQueryExecutor.getInstance().getItem(query);
        } catch (Exception e) {
            System.out.println("ERROR: SelectHandler,handle,getItems, on query: " + query);
        }

        String encoding = "UTF-8";
        String ret = json!=null?json.toString():"";
        he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

        he.sendResponseHeaders(200, ret.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.getBytes());
        os.close();

    }

}
