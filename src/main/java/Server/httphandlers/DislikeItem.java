package Server.httphandlers;

import MySQL.MySQLQueryExecutor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yinon on 03/06/2017.
 */
public class DislikeItem implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        int retVal;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Dislike item, started handling");

        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
            params= Server.Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }

        query= Server.Constants.getDislikeInsertQuery(params);
        System.out.println(query);
        retVal=MySQLQueryExecutor.getInstance().executeSQL(query);


        String encoding = "UTF-8";
        he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
        JSONObject retJson=new JSONObject();
        retJson.put("output",retVal==1 ? "success" : "fail");

        he.sendResponseHeaders(200, retJson.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(retJson.toString().getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Dislike item, finished handling");
    }
}
