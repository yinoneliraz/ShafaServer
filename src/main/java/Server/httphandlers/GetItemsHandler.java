package Server.httphandlers;

import java.io.*;

import Server.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class GetItemsHandler implements HttpHandler {

	public void handle(HttpExchange he) throws IOException {
		JSONArray jsonArr = null;
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
			query=Constants.getSelectQuery(postData);
			jsonArr = MySQLQueryExecutor.getInstance().getItems(query);
		} catch (Exception e) {
			System.out.println("ERROR: SelectHandler,handle,getItems, on query: " + query);
			e.printStackTrace();
		}

		String encoding = "UTF-8";
		String ret = jsonArr!=null?jsonArr.toString():"";
		he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

		he.sendResponseHeaders(200, ret.toString().getBytes().length);
		OutputStream os = he.getResponseBody();
		os.write(ret.getBytes());
		os.close();

	}
}
