package Server.httphandlers;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Server.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class GetItems implements HttpHandler {

	public void handle(HttpExchange he) throws IOException {
		JSONArray jsonArr = new JSONArray();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date) + ":Get items, started handling");

		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		JSONObject postData=null;
		try {
			postData= Constants.parseQuery(query);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}

		if(postData.get("userID").toString().equals("10155418397534840")){
			postData.put("lat","31.258308");
			postData.put("lng","34.794415");
		}

		try {
			query=Constants.getSelectQuery(postData);
			if(!query.equals(""))
				jsonArr = MySQLQueryExecutor.getInstance().getItems(query);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}

		String encoding = "UTF-8";
		String ret = jsonArr!=null?jsonArr.toString():"";
		he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

		he.sendResponseHeaders(200, ret.toString().getBytes().length);
		OutputStream os = he.getResponseBody();
		os.write(ret.getBytes());
		os.close();
		System.out.println(dateFormat.format(date) + ":Get items, finished handling");
	}
}
