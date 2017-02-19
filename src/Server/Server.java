package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import MySQL.MySQLQueryExecutor;
public class Server {
	static MySQLQueryExecutor mySQLQueryExecutor=new MySQLQueryExecutor("items");
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(Constants.port), 0);
        server.createContext("/get", (HttpHandler) new getHandler());
        server.createContext("/insert", (HttpHandler) new InsertHandler());
        server.setExecutor(null);
        server.start();
        System.out.println(server.getAddress());
    }

    static class InsertHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange he) throws IOException {
			int retVal=0;
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            JSONObject params=new JSONObject();
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            try {
            	params=Constants.parseQuery(query);
			} catch (JSONException e) {
				System.out.println("ERROR: 		InsertHandler,handle,parseQuery, on query: " + query);
			}

            String response = "";

            try {
				query=Constants.getInsertQuery(params);
				System.out.println(query);
				retVal=mySQLQueryExecutor.executeSQL(query);
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

    static class getHandler implements HttpHandler {
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
//			Object t=params.entrySet().
			try {
				query=Constants.getSelectQuery(postData);
				jsonArr = mySQLQueryExecutor.getItems(query);
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
}