package Server.httphandlers;


import Server.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

import java.io.*;

public class GetMyBagHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        int retVal=0;

        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        try {
            params= Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println("ERROR: 		InsertItemToBasketHandler,handle,parseQuery, on query: " + query);
        }

        try {
            query=Constants.getBasketGetQuery(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(query);

        JSONArray jsonArr;
        jsonArr=MySQLQueryExecutor.getInstance().getItems(query);
        System.out.println("Insert query execution returned : " + retVal);
        String ret=jsonArr.toString();
        he.sendResponseHeaders(200, ret.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.toString().getBytes());
        os.close();
    }
}
