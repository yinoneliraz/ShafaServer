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
 * Created by Yinon on 19/05/2017.
 */
public class InsertMessageHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        int retVal=0;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
            params= Constants.parseQuery(query);

        } catch (Exception e) {
            System.out.println("ERROR: 		InsertMessageHandler,handle,parseQuery, on query: " + query);
        }
        try {
            query=Constants.getInsertMessageQuery(params);
            System.out.println(query);
            retVal= MySQLQueryExecutor.getInstance().executeSQL(query);
            System.out.println("Insert query execution returned : " + retVal);
        } catch (Exception e) {
            System.out.println("ERROR: 		InsertMessageHandler,handle,getInsertQuery, on query: " + query);
        }
        he.sendResponseHeaders(200, String.valueOf(retVal).getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(String.valueOf(retVal).getBytes());
        os.close();

    }
}
