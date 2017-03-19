package Server;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

		String encoding = "UTF-8";
		String ret = jsonArr.toString();
		he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

		//ret= URLDecoder.decode(ret, "UTF-8");

		byte[] bytes = ret.getBytes(StandardCharsets.UTF_8);
		he.sendResponseHeaders(200, bytes.length);
		System.out.println(ret);
		OutputStream os = he.getResponseBody();
		os.write(bytes);
		os.close();
	}
}
