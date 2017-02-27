package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class getHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange he) throws IOException {
		JSONArray jsonArr = null;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        JSONObject postData=null;
        try {
        	postData=Constants.parseQuery(query);
		} catch (JSONException e) {
			System.out.println("ERROR: 		SelectHandler,handle,parseQuery, on query: " + query);
		}
//		Object t=params.entrySet().
		try {
			query=Constants.getSelectQuery(postData);
			jsonArr = MySQLQueryExecutor.getInstance().getItems(query);
		} catch (JSONException e) {
			System.out.println("ERROR: 		SelectHandler,handle,getItems, on query: " + query);
		}
        String ret=jsonArr.toString();
        he.sendResponseHeaders(200, ret.length());
        System.out.println(ret);
        OutputStream os = he.getResponseBody();
        os.write(ret.toString().getBytes());
        os.close();
	}
}
