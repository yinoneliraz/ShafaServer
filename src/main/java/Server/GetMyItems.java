package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class GetMyItems implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        int retVal=0;
        JSONArray jsonArr = null;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
            params=Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println("ERROR: 		LikeItem,handle,parseQuery, on query: " + query);
        }

        String response = "";

        try {
            query=Constants.getItemsGetQuery(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(query);
        jsonArr=MySQLQueryExecutor.getInstance().getItems(query);
        System.out.println("Insert query execution returned : " + retVal);
        String ret=jsonArr.toString();
        he.sendResponseHeaders(200, ret.length());
        System.out.println(ret);
        OutputStream os = he.getResponseBody();
        os.write(ret.toString().getBytes());
        os.close();
    }
}
