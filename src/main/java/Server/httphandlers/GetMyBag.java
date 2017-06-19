package Server.httphandlers;


import Server.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class GetMyBag implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Get my bag, started handling");

        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        try {
            params= Constants.parseQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            query=Constants.getBasketGetQuery(params);
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }

        JSONArray jsonArr;
        jsonArr=MySQLQueryExecutor.getInstance().getItems(query);
        JSONObject myBag=new JSONObject();
        JSONArray sold=new JSONArray();
        JSONArray notSold=new JSONArray();
        Iterator it= jsonArr.iterator();
        while(it.hasNext()){
            JSONObject obj=(JSONObject)it.next();
            if(obj.get("isSold").toString().equals("1")){
                sold.add(obj);
            }
            else{
                notSold.add(obj);
            }
        }
        myBag.put("sold",sold);
        myBag.put("notSold",notSold);
        String ret=myBag.toString();
        he.sendResponseHeaders(200, ret.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.toString().getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Get my bag, finished handling");
    }
}
