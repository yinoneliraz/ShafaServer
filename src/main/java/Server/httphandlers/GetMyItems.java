package Server.httphandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import Server.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class GetMyItems implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Get my items, started handling");
        JSONArray jsonArr;
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
            query=Constants.getItemsGetQuery(params);
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }
        jsonArr=MySQLQueryExecutor.getInstance().getItems(query);
        JSONObject myItems=new JSONObject();
        JSONArray sold=new JSONArray();
        JSONArray notSold=new JSONArray();
        Iterator it= jsonArr.iterator();
        while(it.hasNext()){
            JSONObject obj=(JSONObject)it.next();
            if(obj.get("isSold")=="1"){
                sold.add(obj);
            }
            else{
                notSold.add(obj);
            }
        }
        myItems.put("sold",sold);
        myItems.put("notSold",notSold);
        String ret=myItems.toString();
        he.sendResponseHeaders(200, ret.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Get my items finished");
    }
}
