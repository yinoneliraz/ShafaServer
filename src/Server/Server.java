package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.fabric.xmlrpc.base.Params;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import MySQL.MySQLQueryExecutor;
public class Server {
	static MySQLQueryExecutor mySQLQueryExecutor=new MySQLQueryExecutor("items");
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(Constants.port), 0);
        HttpContext context = server.createContext("/test", (HttpHandler) new TestHandler());
        //context.getFilters().add(new ParameterFilter());
        server.createContext("/get", (HttpHandler) new SelectHandler());
        server.createContext("/insert", (HttpHandler) new InsertHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println(server.getAddress());
    }

    static class InsertHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange he) throws IOException {
			// TODO Auto-generated method stub
            Map<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            try {
				parseQuery(query);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // send response
            String response = "";
            for (String key : parameters.keySet())
                     response += key + " = " + parameters.get(key) + "\n";
            query=String.format("INSERT INTO `menagerie`.`items`"+
            		"(`name`, `address`, `owner_id`, `category`, `size`,"+
            		"`price`, `description`, `lat`, `lng`, `images`) VALUES"+
            		"(%0$s, %1$s, %2$s, %3$s, %4$f, %5$d, %6$s, %7$f, %8$f,"+
            		"%9$s);",parameters.get("name"),parameters.get("address"),
            		parameters.get("owner_id"),parameters.get("category"),
            		parameters.get("size"),parameters.get("price"),
            		parameters.get("description"),parameters.get("lat"),
            		parameters.get("lng"),parameters.get("images"));
            
            he.sendResponseHeaders(200, response.length());
            System.out.println(response);
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
		}
    }

    static class TestHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange he) throws IOException {
			JSONArray jsonArr = null;
	        Map<String, Object> params = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            JSONObject postData=null;
            try {
            	postData=parseQuery(query);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Object t=params.entrySet().
			try {
				jsonArr = mySQLQueryExecutor.getItems(Constants.getSelectQuery(postData));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String ret=jsonArr.toString();
            he.sendResponseHeaders(200, ret.length());
            System.out.println(ret);
            OutputStream os = he.getResponseBody();
            os.write(ret.toString().getBytes());
            os.close();
		}
    }

    //SELECT id, ( 3959 * acos( cos( radians(37) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(-122) ) + sin( radians(37) ) * sin( radians( lat ) ) ) ) AS distance FROM markers HAVING distance < 25 ORDER BY distance LIMIT 0 , 20;

    static class SelectHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			// TODO Auto-generated method stub
            JSONArray jsonArr = null;
            String response="";
			try {
				jsonArr = mySQLQueryExecutor.getAllRecords();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				response=e.getMessage();
			}
            response=jsonArr.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();			
		}
    } 

    public static JSONObject parseQuery(String query) throws UnsupportedEncodingException, JSONException {
    	JSONObject ret = null;
    	if (query != null) {
    		String pairs[] = query.split("[&]");
    		for (String pair : pairs) {
    			String param[] = pair.split("[=]");
    			String key = null;
    			if (param.length > 0) {
    				key = URLDecoder.decode(param[0], 
    					System.getProperty("file.encoding"));
    			}
    			
    			if(key!= null){
    				ret=new JSONObject(key);
    				return ret;
    			}
    		}
    	}
		return ret;
    }
}