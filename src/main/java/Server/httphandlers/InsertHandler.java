package Server.httphandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import Server.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class InsertHandler implements HttpHandler {

	public void handle(HttpExchange he) throws IOException {
		int retVal=0;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
        	params= Constants.parseQuery(query);
		} catch (JSONException e) {
			System.out.println("ERROR: 		InsertHandler,handle,parseQuery, on query: " + query);
		}

        String response = "";

        try {
			query=Constants.getInsertQuery(params);
			System.out.println(query);
			retVal=MySQLQueryExecutor.getInstance().executeSQL(query);
			System.out.println("Insert query execution returned : " + retVal);
		} catch (JSONException e) {
			System.out.println("ERROR: 		InsertHandler,handle,getInsertQuery, on query: " + query);
		}
        JSONObject retJson=new JSONObject();
        JSONArray array=new JSONArray();
        try {
			retJson.put("return", ""+retVal+"");
			array.put(retJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        String ret="OK";
        he.sendResponseHeaders(200, ret.length());
        System.out.println(ret);
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
	}
}
