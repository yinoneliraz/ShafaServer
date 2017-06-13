package Server.httphandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Server.Constants;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class InsertNewItem implements HttpHandler {

	public void handle(HttpExchange he) throws IOException {
		int retVal=0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date) + ":Insert new item, started handling");

		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
        	params= Constants.parseQuery(query);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}

        try {
			query=Constants.getInsertQuery(params);
			retVal=MySQLQueryExecutor.getInstance().executeSQL(query);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}
		String encoding = "UTF-8";
		he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
		JSONObject retJson=new JSONObject();
		retJson.put("output",retVal==1 ? "success" : "fail");

		he.sendResponseHeaders(200, retJson.toString().getBytes().length);
		OutputStream os = he.getResponseBody();
		os.write(retJson.toString().getBytes());
		os.close();
		System.out.println(dateFormat.format(date) + ":Insert new item, finished handling");

	}
}
